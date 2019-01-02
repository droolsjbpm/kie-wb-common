/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.v1_1.DMNElementReference;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionService;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.DecisionServiceRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class DecisionServiceConverter implements NodeConverter<org.kie.dmn.model.api.DecisionService, org.kie.workbench.common.dmn.api.definition.v1_1.DecisionService> {

    private FactoryManager factoryManager;

    public DecisionServiceConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<DecisionService>, ?> nodeFromDMN(final org.kie.dmn.model.api.DecisionService dmn) {
        @SuppressWarnings("unchecked")
        Node<View<DecisionService>, ?> node = (Node<View<DecisionService>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                         DecisionService.class).asNode();
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        Name name = new Name(dmn.getName());
        InformationItemPrimary informationItem = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn.getVariable());
        final List<DMNElementReference> outputDecision = dmn.getOutputDecision().stream().map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        final List<DMNElementReference> encapsulatedDecision = dmn.getEncapsulatedDecision().stream().map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        final List<DMNElementReference> inputDecision = dmn.getInputDecision().stream().map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        final List<DMNElementReference> inputData = dmn.getInputData().stream().map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        DecisionService decisionService = new DecisionService(id,
                                                              description,
                                                              name,
                                                              informationItem,
                                                              outputDecision,
                                                              encapsulatedDecision,
                                                              inputDecision,
                                                              inputData,
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new DecisionServiceRectangleDimensionsSet());
        node.getContent().setDefinition(decisionService);

        if (informationItem != null) {
            informationItem.setParent(decisionService);
        }

        return node;
    }

    @Override
    public org.kie.dmn.model.api.DecisionService dmnFromNode(Node<View<DecisionService>, ?> node) {
        DecisionService source = node.getContent().getDefinition();
        org.kie.dmn.model.api.DecisionService ds = new org.kie.dmn.model.v1_2.TDecisionService();
        ds.setId(source.getId().getValue());
        ds.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        ds.setName(source.getName().getValue());
        org.kie.dmn.model.api.InformationItem variable = InformationItemPrimaryPropertyConverter.dmnFromWB(source.getVariable());
        if (variable != null) {
            variable.setParent(ds);
        }
        ds.setVariable(variable);
        
        List<org.kie.dmn.model.api.DMNElementReference> existing_outputDecision = source.getOutputDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        List<org.kie.dmn.model.api.DMNElementReference> existing_encapsulatedDecision = source.getEncapsulatedDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        List<org.kie.dmn.model.api.DMNElementReference> existing_inputDecision = source.getInputDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        List<org.kie.dmn.model.api.DMNElementReference> existing_inputData = source.getInputData().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        List<org.kie.dmn.model.api.DMNElementReference> candidate_outputDecision = new ArrayList<>();
        List<org.kie.dmn.model.api.DMNElementReference> candidate_encapsulatedDecision = new ArrayList<>();
        List<org.kie.dmn.model.api.DMNElementReference> candidate_inputDecision = new ArrayList<>();
        List<org.kie.dmn.model.api.DMNElementReference> candidate_inputData = new ArrayList<>();
        
        List<InputData> reqInputs = new ArrayList<>();
        List<Decision> reqDecisions = new ArrayList<>();
        // DMN spec table 2: Requirements connection rules
        List<Edge<?, ?>> outEdges = (List<Edge<?, ?>>) node.getOutEdges();
        for (Edge<?, ?> e : outEdges) {
            if (e.getContent() instanceof Child) {
                @SuppressWarnings("unchecked")
                Node<View<?>, ?> targetNode = e.getTargetNode();
                if (targetNode.getContent() instanceof View<?>) {
                    View<?> view = (View<?>) targetNode.getContent();
                    if (view.getDefinition() instanceof DRGElement) {
                        DRGElement drgElement = (DRGElement) view.getDefinition();
                        if (drgElement instanceof Decision) {
                            Decision decision = (Decision) drgElement;
                            org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                            ri.setHref(new StringBuilder("#").append(decision.getId().getValue()).toString());
                            if (isNodeUpperHalfOfDS(targetNode, node)) {
                                candidate_outputDecision.add(ri);
                            } else {
                                candidate_encapsulatedDecision.add(ri);
                            }
                            inspectDecisionForDSReqs(targetNode, reqInputs, reqDecisions);
                        } else {
                            throw new UnsupportedOperationException("wrong model definition: a DecisionService is expected to encapsulate only Decision");
                        }
                    }
                }
            } else if (e.getContent() instanceof KnowledgeRequirement) {
                // this was taken care by the receiving Decision or BKM.
            } else {
                throw new UnsupportedOperationException("wrong model definition.");
            }
        }
        reqInputs.stream()
                 .sorted(Comparator.comparing(x -> x.getName().getValue()))
                 .map(x -> {
                     org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                     ri.setHref(new StringBuilder("#").append(x.getId().getValue()).toString());
                     return ri;
                 })
                 .forEach(candidate_inputData::add);
        reqDecisions.stream()
                    .sorted(Comparator.comparing(x -> x.getName().getValue()))
                    .map(x -> {
                        org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(x.getId().getValue()).toString());
                        return ri;
                    })
                    .forEach(candidate_inputDecision::add);

        reconcileExistingAndCandidate(ds.getInputData(), existing_inputData, candidate_inputData);
        reconcileExistingAndCandidate(ds.getInputDecision(), existing_inputDecision, candidate_inputDecision);
        reconcileExistingAndCandidate(ds.getEncapsulatedDecision(), existing_encapsulatedDecision, candidate_encapsulatedDecision);
        reconcileExistingAndCandidate(ds.getOutputDecision(), existing_outputDecision, candidate_outputDecision);

        return ds;
    }

    private void reconcileExistingAndCandidate(List<org.kie.dmn.model.api.DMNElementReference> targetList,
                                               List<org.kie.dmn.model.api.DMNElementReference> existingList,
                                               List<org.kie.dmn.model.api.DMNElementReference> candidateList) {
        List<org.kie.dmn.model.api.DMNElementReference> existing = new ArrayList<>(existingList);
        List<org.kie.dmn.model.api.DMNElementReference> candidate = new ArrayList<>(candidateList);
        for (org.kie.dmn.model.api.DMNElementReference e : existing) {
            boolean existingIsAlsoCandidate = candidate.removeIf(er -> er.getHref().equals(e.getHref()));
            if (existingIsAlsoCandidate) {
                targetList.add(e);
            }
        }
        for (org.kie.dmn.model.api.DMNElementReference c : candidate) {
            targetList.add(c);
        }
    }

    private void inspectDecisionForDSReqs(Node<View<?>, ?> targetNode, List<InputData> reqInputs, List<Decision> reqDecisions) {
        List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) targetNode.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    DRGElement drgElement = (DRGElement) view.getDefinition();
                    if (drgElement instanceof Decision) {
                        reqDecisions.add((Decision) drgElement);
                    } else if (drgElement instanceof InputData) {
                        reqInputs.add((InputData) drgElement);
                    }
                }
            }
        }
    }

    private static boolean isNodeUpperHalfOfDS(Node<View<?>, ?> node, Node<View<DecisionService>, ?> dsNode) {
        double dsHeight = node.getContent().getBounds().getLowerRight().getY() - node.getContent().getBounds().getUpperLeft().getY();
        double yUpperHalf = node.getContent().getBounds().getUpperLeft().getY() + (dsHeight / 2);
        return node.getContent().getBounds().getUpperLeft().getY() < yUpperHalf;
    }
}