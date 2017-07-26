/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.backend;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.inject.spi.BeanManager;

import org.apache.tools.ant.filters.StringInputStream;
import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.stunner.backend.ApplicationFactoryManager;
import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimeDefinitionAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimeDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimePropertyAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimePropertySetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet.MagnetType;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DMNUnmarshallTest {

    private static final String DMN_DEF_SET_ID = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);

    @Mock
    DefinitionManager definitionManager;

    @Mock
    AdapterManager adapterManager;

    @Mock
    AdapterRegistry adapterRegistry;

    @Mock
    BeanManager beanManager;

    @Mock
    RuleManager rulesManager;

    @Mock
    ApplicationFactoryManager applicationFactoryManager;

    EdgeFactory<Object> connectionEdgeFactory;
    NodeFactory<Object> viewNodeFactory;
    DefinitionUtils definitionUtils;

    GraphCommandManager commandManager;
    GraphCommandFactory commandFactory;

    GraphFactory dmnGraphFactory;

    TestScopeModelFactory testScopeModelFactory;

    @Before
    public void setup() throws Exception {
        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        definitionUtils = new DefinitionUtils(definitionManager,
                                              applicationFactoryManager);
        testScopeModelFactory = new TestScopeModelFactory(new DMNDefinitionSet.DMNDefinitionSetBuilder().build());
        // Definition manager.
        final RuntimeDefinitionAdapter definitionAdapter = new RuntimeDefinitionAdapter(definitionUtils);
        final RuntimeDefinitionSetAdapter definitionSetAdapter = new RuntimeDefinitionSetAdapter(definitionAdapter);
        final RuntimePropertySetAdapter propertySetAdapter = new RuntimePropertySetAdapter();
        final RuntimePropertyAdapter propertyAdapter = new RuntimePropertyAdapter();
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
        commandManager = new GraphCommandManagerImpl(null,
                                                     null,
                                                     null);
        commandFactory = new GraphCommandFactory();
        connectionEdgeFactory = new EdgeFactoryImpl(definitionManager);
        viewNodeFactory = new NodeFactoryImpl(definitionUtils);
        dmnGraphFactory = new GraphFactoryImpl(definitionManager);
        doAnswer(invocationOnMock -> {
            String id = (String) invocationOnMock.getArguments()[0];
            return testScopeModelFactory.build(id);
        }).when(applicationFactoryManager).newDefinition(anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            String id = (String) invocationOnMock.getArguments()[1];
            if (DMNDefinitionSet.class.getName().equals(id)) {
                // Emulate DMNGraphFactoryImpl, that adds a DMNDiagram to new Graphs
                // Please note this is different from the stunner jbpm test which this dmn test is based on
                Graph graph = (Graph) dmnGraphFactory.build(uuid,
                                                            DMN_DEF_SET_ID);
                DMNDiagram model = new DMNDiagram.DMNDiagramBuilder().build();
                Node node = viewNodeFactory.build(uuid,
                                                  model);
                graph.addNode(node);
                return graph;
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = RuntimeDefinitionAdapter.getGraphFactory(model.getClass());
                if (element.isAssignableFrom(NodeFactory.class)) {
                    Node node = viewNodeFactory.build(uuid,
                                                      model);
                    return node;
                } else if (element.isAssignableFrom(EdgeFactory.class)) {
                    Edge edge = connectionEdgeFactory.build(uuid,
                                                            model);
                    return edge;
                }
            }
            return null;
        }).when(applicationFactoryManager).newElement(anyString(),
                                                      anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            Class type = (Class) invocationOnMock.getArguments()[1];
            String id = BindableAdapterUtils.getGenericClassName(type);
            if (DMNDefinitionSet.class.equals(type)) {
                Graph graph = (Graph) dmnGraphFactory.build(uuid,
                                                            DMN_DEF_SET_ID);
                return graph;
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = RuntimeDefinitionAdapter.getGraphFactory(model.getClass());
                if (element.isAssignableFrom(NodeFactory.class)) {
                    Node node = viewNodeFactory.build(uuid,
                                                      model);
                    return node;
                } else if (element.isAssignableFrom(EdgeFactory.class)) {
                    Edge edge = connectionEdgeFactory.build(uuid,
                                                            model);
                    return edge;
                }
            }
            return null;
        }).when(applicationFactoryManager).newElement(anyString(),
                                                      any(Class.class));
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            String defSetId = (String) invocationOnMock.getArguments()[1];
            final Graph graph = (Graph) applicationFactoryManager.newElement(uuid,
                                                                             defSetId);
            final DiagramImpl result = new DiagramImpl(uuid,
                                                       new MetadataImpl.MetadataImplBuilder(defSetId).build());
            result.setGraph(graph);
            return result;
        }).when(applicationFactoryManager).newDiagram(anyString(),
                                                      anyString(),
                                                      any(Metadata.class));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test2() throws IOException {
        MappingContextSingleton.loadDynamicMarshallers();

        DMNMarshaller m = new DMNMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                            applicationFactoryManager);

        Graph<?, Node<?, ?>> g = m.unmarshall(null,
                                              this.getClass().getResourceAsStream("/diamond.dmn"));

        System.out.println(g);
        
        DiagramImpl diagram = new DiagramImpl("", null);
        diagram.setGraph(g);
        
        String mString = m.marshall(diagram);
        System.out.println(mString);
    }
    
    @Test
    public void test3() throws IOException {
        MappingContextSingleton.loadDynamicMarshallers();
        
        DMNMarshaller m = new DMNMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                            applicationFactoryManager);

        // first unmarshal from DMN XML to Stunner DMN Graph
        
        @SuppressWarnings("unchecked")
        Graph<?, Node<?, ?>> g = m.unmarshall(null, this.getClass().getResourceAsStream("/potpourri_drawing.dmn"));
        checkPotpourryGraph(g);
        
        // round trip to Stunner DMN Graph back to DMN XML
        DiagramImpl diagram = new DiagramImpl("", null);
        diagram.setGraph(g);
        
        String mString = m.marshall(diagram);
        System.out.println(mString);
        
        // now unmarshal again from the marshalled above back again to Stunner DMN Graph to complete check for round-trip
        @SuppressWarnings("unchecked")
        Graph<?, Node<?, ?>> g2 = m.unmarshall(null, new StringInputStream( mString ) );
        checkPotpourryGraph(g2);
        
    }

    private void checkPotpourryGraph(Graph<?, Node<?, ?>> g) {
        Node<?, ?> _My_Input_Data = g.getNode( "_My_Input_Data" );
        assertNodeContentDefinitionIs( _My_Input_Data, InputData.class );
        assertNodeEdgesTo( _My_Input_Data, g.getNode( "_My_Decision_1" ), InformationRequirement.class );
        assertNodeEdgesTo( _My_Input_Data, g.getNode( "_KS_of_Input_Data" ), AuthorityRequirement.class );
        assertNodeEdgesTo( _My_Input_Data, g.getNode( "_Annotation_for_Input_Data" ), Association.class );
        
        Node<?, ?> _Annotation_for_Input_Data = g.getNode( "_Annotation_for_Input_Data" );
        assertNodeContentDefinitionIs( _Annotation_for_Input_Data, TextAnnotation.class );
        
        Node<?, ?> _KS_of_Input_Data = g.getNode( "_KS_of_Input_Data" );
        assertNodeContentDefinitionIs( _KS_of_Input_Data, KnowledgeSource.class );
        
        Node<?, ?> _KS_of_KS_of_InputData = g.getNode( "_KS_of_KS_of_InputData" );
        assertNodeContentDefinitionIs( _KS_of_KS_of_InputData, KnowledgeSource.class );
        assertNodeEdgesTo( _KS_of_KS_of_InputData, g.getNode( "_KS_of_Input_Data" ), AuthorityRequirement.class );
        
        Node<?, ?> _KS_of_KS_of_Decision_1 = g.getNode( "_KS_of_KS_of_Decision_1" );
        assertNodeContentDefinitionIs( _KS_of_KS_of_Decision_1, KnowledgeSource.class );
        assertNodeEdgesTo( _KS_of_KS_of_Decision_1, g.getNode( "_KS_of_Decision_1" ), AuthorityRequirement.class );
        
        Node<?, ?> _KS_of_Decision_1 = g.getNode( "_KS_of_Decision_1" );
        assertNodeContentDefinitionIs( _KS_of_Decision_1, KnowledgeSource.class );
        assertNodeEdgesTo( _KS_of_Decision_1, g.getNode( "_My_Decision_1" ), AuthorityRequirement.class );
        
        Node<?, ?> _My_Decision_2 = g.getNode( "_My_Decision_2" );
        assertNodeContentDefinitionIs( _My_Decision_2, Decision.class );
        assertNodeEdgesTo( _My_Decision_2, g.getNode( "_KS_of_Decision_2" ), AuthorityRequirement.class );
        assertNodeEdgesTo( _My_Decision_2, g.getNode( "_Annotation_for_Decision_2" ), Association.class );
        
        Node<?, ?> _KS_of_Decision_2 = g.getNode( "_KS_of_Decision_2" );
        assertNodeContentDefinitionIs( _KS_of_Decision_2, KnowledgeSource.class );
        
        Node<?, ?> _Annotation_for_Decision_2 = g.getNode( "_Annotation_for_Decision_2" );
        assertNodeContentDefinitionIs( _Annotation_for_Decision_2, TextAnnotation.class );
        
        Node<?, ?> _Annotation_for_BKM_1 = g.getNode( "_Annotation_for_BKM_1" );
        assertNodeContentDefinitionIs( _Annotation_for_BKM_1, TextAnnotation.class );
        assertNodeEdgesTo( _Annotation_for_BKM_1, g.getNode( "_My_BKM_1_of_Decision_1" ), Association.class );
        
        Node<?, ?> _My_BKM_1_of_Decision_1 = g.getNode( "_My_BKM_1_of_Decision_1" );
        assertNodeContentDefinitionIs( _My_BKM_1_of_Decision_1, BusinessKnowledgeModel.class );
        assertNodeEdgesTo( _My_BKM_1_of_Decision_1, g.getNode( "_My_Decision_1" ), KnowledgeRequirement.class );

        Node<?, ?> _KS_of_BKM_1 = g.getNode( "_KS_of_BKM_1" );
        assertNodeContentDefinitionIs( _KS_of_BKM_1, KnowledgeSource.class );
        assertNodeEdgesTo( _KS_of_BKM_1, g.getNode( "_My_BKM_1_of_Decision_1" ), AuthorityRequirement.class );
        
        Node<?, ?> _KS_of_KS_of_BKM_1 = g.getNode( "_KS_of_KS_of_BKM_1" );
        assertNodeContentDefinitionIs( _KS_of_KS_of_BKM_1, KnowledgeSource.class );
        assertNodeEdgesTo( _KS_of_KS_of_BKM_1, g.getNode( "_KS_of_BKM_1" ), AuthorityRequirement.class );
        
        Node<?, ?> _My_BKM_2_of_BKM_1 = g.getNode( "_My_BKM_2_of_BKM_1" );
        assertNodeContentDefinitionIs( _My_BKM_2_of_BKM_1, BusinessKnowledgeModel.class );
        assertNodeEdgesTo( _My_BKM_2_of_BKM_1, g.getNode( "_My_BKM_1_of_Decision_1" ), KnowledgeRequirement.class );
        
        Node<?, ?> _KS_of_BKM_2 = g.getNode( "_KS_of_BKM_2" );
        assertNodeContentDefinitionIs( _KS_of_BKM_2, KnowledgeSource.class );
        assertNodeEdgesTo( _KS_of_BKM_2, g.getNode( "_My_BKM_2_of_BKM_1" ), AuthorityRequirement.class );
        assertNodeEdgesTo( _KS_of_BKM_2, g.getNode( "_Annotation_for_KS_of_BKM_2" ), Association.class );
        
        Node<?, ?> _Annotation_for_KS_of_BKM_2 = g.getNode( "_Annotation_for_KS_of_BKM_2" );
        assertNodeContentDefinitionIs( _Annotation_for_KS_of_BKM_2, TextAnnotation.class );
        
        Node<?, ?> _My_Decision_1 = g.getNode( "_My_Decision_1" );
        assertNodeContentDefinitionIs( _My_Decision_1, Decision.class );
        
        Node<?, ?> rootNode = DMNMarshaller.findDMNDiagramRoot((Graph<?, ? extends Node<View, ?>>) g); 
        assertNotNull( rootNode );
        assertRootNodeConnectedTo( rootNode, _My_Input_Data );
        assertRootNodeConnectedTo( rootNode, _Annotation_for_Input_Data );
        assertRootNodeConnectedTo( rootNode, _KS_of_Input_Data );
        assertRootNodeConnectedTo( rootNode, _KS_of_KS_of_InputData );
        assertRootNodeConnectedTo( rootNode, _KS_of_KS_of_Decision_1 );
        assertRootNodeConnectedTo( rootNode, _KS_of_Decision_1 );
        assertRootNodeConnectedTo( rootNode, _My_Decision_2 );
        assertRootNodeConnectedTo( rootNode, _KS_of_Decision_2 );
        assertRootNodeConnectedTo( rootNode, _Annotation_for_Decision_2 );
        assertRootNodeConnectedTo( rootNode, _Annotation_for_BKM_1 );
        assertRootNodeConnectedTo( rootNode, _My_BKM_1_of_Decision_1 );
        assertRootNodeConnectedTo( rootNode, _KS_of_BKM_1 );
        assertRootNodeConnectedTo( rootNode, _KS_of_KS_of_BKM_1 );
        assertRootNodeConnectedTo( rootNode, _My_BKM_2_of_BKM_1 );
        assertRootNodeConnectedTo( rootNode, _KS_of_BKM_2 );
        assertRootNodeConnectedTo( rootNode, _Annotation_for_KS_of_BKM_2 );
        assertRootNodeConnectedTo( rootNode, _My_Decision_1 );
    }

    private void assertRootNodeConnectedTo(Node<?, ?> rootNode, Node<?, ?> to) {
        @SuppressWarnings("unchecked")
        List<Edge<?,?>> outEdges = (List<Edge<?, ?>>) rootNode.getOutEdges();
        Optional<Edge<?, ?>> optEdge = outEdges.stream().filter( e -> e.getTargetNode().equals( to ) ).findFirst();
        assertTrue( optEdge.isPresent() );
        
        Edge<?, ?> edge = optEdge.get();
        assertTrue( edge.getContent() instanceof Child );
        
        assertTrue( to.getInEdges().contains( edge ) );
    }

    private void assertNodeEdgesTo(Node<?, ?> from, Node<?, ?> to, Class<?> clazz) {
        @SuppressWarnings("unchecked")
        List<Edge<?,?>> outEdges = (List<Edge<?, ?>>) from.getOutEdges();
        Optional<Edge<?, ?>> optEdge = outEdges.stream().filter( e -> e.getTargetNode().equals( to ) ).findFirst();
        assertTrue( optEdge.isPresent() );
        
        Edge<?, ?> edge = optEdge.get();
        assertTrue( edge.getContent() instanceof View );
        assertTrue( clazz.isInstance( ((View<?>)edge.getContent()).getDefinition() ) );
        
        assertTrue( to.getInEdges().contains( edge ) );
        
        ViewConnector<?> connectionContent = (ViewConnector<?>) edge.getContent();
        assertTrue( connectionContent.getSourceMagnet().isPresent() );
        assertEquals( MagnetType.OUTGOING, connectionContent.getSourceMagnet().get().getMagnetType() );
        assertTrue( connectionContent.getTargetMagnet().isPresent() );
        assertEquals( MagnetType.INCOMING, connectionContent.getTargetMagnet().get().getMagnetType() );
    }

    private void assertNodeContentDefinitionIs(Node<?, ?> node, Class<?> clazz) {
        assertTrue( node.getContent() instanceof View );
        assertTrue( clazz.isInstance( ((View<?>)node.getContent()).getDefinition() ) );
    }

    @Ignore("hard coded test")
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    @Deprecated
    public void test1() {
        Element elementInputData = applicationFactoryManager.newElement(UUID.randomUUID().toString(),
                                                                        InputData.class);
        Node nodeInputData = elementInputData.asNode();

        Element elemDecision = applicationFactoryManager.newElement(UUID.randomUUID().toString(),
                                                                    Decision.class);
        Node nodeDecision = elemDecision.asNode();

        Element elemInformationRequirement = applicationFactoryManager.newElement(UUID.randomUUID().toString(),
                                                                                  InformationRequirement.class);
        Edge myEdge = elemInformationRequirement.asEdge();
        connectEdge(myEdge,
                    nodeInputData,
                    nodeDecision);

        Node dmnDiagramRoot = applicationFactoryManager.newElement(UUID.randomUUID().toString(),
                                                                   DMNDiagram.class).asNode();
        connectRootWithChild(dmnDiagramRoot,
                             nodeInputData);
        connectRootWithChild(dmnDiagramRoot,
                             nodeDecision);

        Diagram newDiagram = applicationFactoryManager.newDiagram("prova",
                                                                  DMN_DEF_SET_ID,
                                                                  null);
        newDiagram.getGraph().addNode(nodeInputData);
        newDiagram.getGraph().addNode(nodeDecision);

        System.out.println(newDiagram);
    }

    private void connectRootWithChild(Node dmnDiagramRoot,
                                      Node child) {
        final String uuid = org.kie.workbench.common.stunner.core.util.UUID.uuid();
        final Edge<Child, Node> edge = new EdgeImpl<>(uuid);
        edge.setContent(new Child());
        connectEdge(edge,
                    dmnDiagramRoot,
                    child);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectEdge(Edge edge,
                             Node source,
                             Node target) {
        edge.setSourceNode(source);
        edge.setTargetNode(target);
        source.getOutEdges().add(edge);
        target.getInEdges().add(edge);
    }
}