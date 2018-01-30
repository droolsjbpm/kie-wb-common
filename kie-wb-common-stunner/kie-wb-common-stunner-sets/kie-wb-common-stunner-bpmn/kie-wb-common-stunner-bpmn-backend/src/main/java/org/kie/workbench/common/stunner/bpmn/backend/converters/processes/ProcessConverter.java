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

import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.FlowElementConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.GraphBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.LaneConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Layout;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.ProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class ProcessConverter {

    private final GraphBuildingContext context;
    private final FlowElementConverter flowElementConverter;
    private final LaneConverter laneConverter;
    private final Layout layout;
    private final TypedFactoryManager factoryManager;

    public ProcessConverter(
            TypedFactoryManager typedFactoryManager,
            DefinitionResolver definitionResolver,
            Layout layout,
            GraphBuildingContext context) {

        this.factoryManager = typedFactoryManager;
        this.context = context;
        this.flowElementConverter = new FlowElementConverter(typedFactoryManager, definitionResolver, context, layout);
        this.laneConverter = new LaneConverter(typedFactoryManager, definitionResolver);
        this.layout = layout;
    }

    public void convert(String definitionsId, Process process) {
        Node<View<BPMNDiagramImpl>, ?> firstDiagramNode =
                convertProcessNode(definitionsId, process);

        context.addNode(firstDiagramNode);

        process.getFlowElements()
                .stream()
                .map(flowElementConverter::convertNode)
                .filter(Result::notIgnored)
                .map(Result::value)
                .forEach(n -> {
                    layout.updateNode(n);
                    context.addChildNode(firstDiagramNode, n);
                });

        process.getLaneSets()
                .stream()
                .flatMap(laneSet -> laneSet.getLanes().stream())
                .map(laneConverter::convert)
                .forEach(n -> {
                    layout.updateNode(n);
                    context.addChildNode(firstDiagramNode, n);
                });

        process.getFlowElements()
                .stream()
                .map(flowElementConverter::convertEdge)
                .filter(Result::isSuccess)
                .map(Result::value)
                .forEach(layout::updateEdge);

        process.getFlowElements()
                .forEach(flowElementConverter::convertDockedNodes);
    }

    private Node<View<BPMNDiagramImpl>, ?> convertProcessNode(String definitionId, Process process) {
        // FIXME why must we inherit the container's id ??
        Node<View<BPMNDiagramImpl>, Edge> diagramNode = factoryManager.newNode(definitionId, BPMNDiagramImpl.class);
        BPMNDiagramImpl definition = diagramNode.getContent().getDefinition();

        ProcessPropertyReader e = new ProcessPropertyReader(process);

        definition.setDiagramSet(new DiagramSet(
                new Name(process.getName()),
                new Documentation(e.getDocumentation()),
                new Id(process.getId()),
                new Package(e.getPackageName()),
                new Version(e.getVersion()),
                new AdHoc(e.isAdHoc()),
                new ProcessInstanceDescription(e.getDescription()),
                new Executable()
        ));

        definition.setProcessData(new ProcessData(
                new ProcessVariables(e.getProcessVariables())
        ));

        return diagramNode;
    }
}