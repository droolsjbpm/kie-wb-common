/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.processes;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.FlowElementConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.GraphBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.LaneConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Layout;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.EventPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.SubProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class SubProcessConverter {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;
    private final FlowElementConverter flowElementConverter;
    private final LaneConverter laneConverter;
    private final GraphBuildingContext context;
    private final Layout layout;

    public SubProcessConverter(
            TypedFactoryManager factoryManager,
            PropertyReaderFactory propertyReaderFactory,
            DefinitionResolver definitionResolver,
            FlowElementConverter flowElementConverter,
            GraphBuildingContext context, Layout layout) {

        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
        this.context = context;

        this.flowElementConverter = flowElementConverter;
        this.laneConverter = new LaneConverter(factoryManager, propertyReaderFactory, definitionResolver);
        this.layout = layout;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(SubProcess subProcess) {
        Node<? extends View<? extends BPMNViewDefinition>, ?> subProcessNode = convertSubProcessNode(subProcess);
//        layout.updateNode(subProcessNode);

        Map<String, Node<? extends View<? extends BPMNViewDefinition>, ?>> freeFloatingNodes =
                subProcess.getFlowElements()
                        .stream()
                        .map(flowElementConverter::convertNode)
                        .filter(Result::notIgnored)
                        .map(Result::value)
                        .collect(Collectors.toMap(Node::getUUID, Function.identity()));

        subProcess.getLaneSets()
                .stream()
                .flatMap(laneSet -> laneSet.getLanes().stream())

                .forEach(lane -> {
                    Node<? extends View<? extends BPMNViewDefinition>, ?> laneNode =
                            laneConverter.convert(lane);

                    lane.getFlowNodeRefs().forEach(node -> {
                        Node child = freeFloatingNodes.remove(node.getId());
                        context.addChildNode(laneNode, child);
                        layout.updateChildNode(laneNode, child);
                    });

                    layout.updateChildNode(subProcessNode, laneNode);
                    context.addChildNode(subProcessNode, laneNode);
                });


        freeFloatingNodes.values()
                .forEach(n -> {
                    layout.updateChildNode(subProcessNode, n);
                    context.addChildNode(subProcessNode, n);
                });

        subProcess.getFlowElements()
                .stream()
                .map(flowElementConverter::convertEdge)
                .filter(Result::isSuccess)
                .map(Result::value)
                .forEach(layout::updateEdge);

        subProcess.getFlowElements()
                .forEach(flowElementConverter::convertDockedNodes);

        return subProcessNode;
    }

    private Node<? extends View<? extends BPMNViewDefinition>, ?> convertSubProcessNode(SubProcess subProcess) {
        Node<View<EmbeddedSubprocess>, Edge> node = factoryManager.newNode(subProcess.getId(), EmbeddedSubprocess.class);

        EmbeddedSubprocess definition = node.getContent().getDefinition();
        SubProcessPropertyReader p = propertyReaderFactory.of(subProcess);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(subProcess.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.getOnEntryAction().setValue(p.getOnEntryAction());
        definition.getOnExitAction().setValue(p.getOnExitAction());
        definition.getScriptLanguage().setValue(p.getScriptLanguage());
        definition.getIsAsync().setValue(p.isAsync());

        definition.setProcessData(new ProcessData(
                new ProcessVariables(p.getProcessVariables())));

        node.getContent().setBounds(p.getBounds());
        return node;
    }
}
