/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.marshall.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.Imports;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNDiagramFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNClientDiagramServiceTest {

    private static final String PATH_DIAGRAM = "org/kie/workbench/common/stunner/bpmn/client/marshall/testFlight.bpmn";

    private static String xml;

    static {
        try {
            xml = loadStreamAsString(PATH_DIAGRAM);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BPMNClientDiagramService service;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private BPMNClientMarshalling marshalling;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private BPMNDiagramFactory diagramFactory;

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private Promises promises;

    @Mock
    public TypeDefinitionSetRegistry definitionSetRegistry;

    @Mock
    Graph<DefinitionSet, Node> graph;

    private Name processName;

    private Documentation processDocumentation;

    private Id processId;

    private Package packageProperty;

    private Version version;

    private AdHoc adHoc;

    private ProcessInstanceDescription processInstanceDescription;

    private Executable executable;

    private ProcessData processData;

    @Mock
    private Imports imports;

    private GlobalVariables globalVariables;

    @Mock
    private SLADueDate slaDueDate;

    private DiagramSet diagramSet;

    private BPMNDiagramImpl bpmnDiagram;

    private List<Node> nodes;

    @Before
    public void setUp() {
        service = new BPMNClientDiagramService(definitionManager, marshalling, factoryManager, diagramFactory, shapeManager, promises);
        //DiagramSet
        processName = new Name("someName");
        processDocumentation = new Documentation("someDocumentation");
        packageProperty = new Package("some.package");
        version = new Version("1.0");
        adHoc = new AdHoc(false);
        processInstanceDescription = new ProcessInstanceDescription("description");
        executable = new Executable(false);
        processId = new Id("someUUID");
        globalVariables = new GlobalVariables("GL1:java.lang.String:false,GL2:java.lang.Boolean:false");
        slaDueDate = new SLADueDate("");

        diagramSet = new DiagramSet(processName,
                                    processDocumentation,
                                    processId,
                                    packageProperty,
                                    version,
                                    adHoc,
                                    processInstanceDescription,
                                    globalVariables,
                                    imports,
                                    executable,
                                    slaDueDate);

        bpmnDiagram = new BPMNDiagramImpl(
                diagramSet,
                processData,
                new CaseManagementSet(),
                new BackgroundSet(),
                new FontSet(),
                new RectangleDimensionsSet()
        );

        nodes = Arrays.asList(createNode(bpmnDiagram));
    }

    public static String loadStreamAsString(final String path) throws IOException {

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        InputStreamReader isReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(isReader);
        StringBuffer sb = new StringBuffer();
        String str;
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }
        return sb.toString();
    }

    private Node createNode(Object content) {
        NodeImpl node = new NodeImpl(UUID.uuid());
        node.setContent(new ViewImpl<>(content, new Bounds(new Bound(0d, 0d), new Bound(1d, 1d))));
        return node;
    }

    @Test
    public void testNameAndIdAsFileName() throws IOException {

        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(marshalling.unmarshall(any(), any())).thenReturn(graph);
        when(graph.nodes()).thenReturn(nodes);

        service.transform("someFile", xml,
                          new ServiceCallback<Diagram>() {

                              @Override
                              public void onSuccess(Diagram item) {

                              }

                              @Override
                              public void onError(ClientRuntimeError error) {

                              }
                          });

        assertEquals(diagramSet.getName().getValue(), "someFile");
        assertEquals(diagramSet.getId().getValue(), "someFile");
    }

    @Test
    public void testNameAndIdAsXML() {

        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(marshalling.unmarshall(any(), any())).thenReturn(graph);
        when(graph.nodes()).thenReturn(nodes);
        service.transform(BPMNClientDiagramService.DEFAULT_PROCESS_ID, xml,

                          new ServiceCallback<Diagram>() {

                              @Override
                              public void onSuccess(Diagram item) {

                              }

                              @Override
                              public void onError(ClientRuntimeError error) {

                              }
                          });

        assertEquals(diagramSet.getName().getValue(), BPMNClientDiagramService.DEFAULT_PROCESS_ID);
        assertEquals(diagramSet.getId().getValue(), BPMNClientDiagramService.DEFAULT_PROCESS_ID);
    }
}
