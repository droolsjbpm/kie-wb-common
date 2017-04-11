/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Creates a dock relationship (edge + Dock content type) from the child node to the target node.
 */
@Portable
public final class DockNodeCommand extends AbstractGraphCommand {

    private String parentUUID;
    private String candidateUUID;
    private transient Node<?, Edge> parent;
    private transient Node<?, Edge> candidate;

    public DockNodeCommand(final @MapsTo("parentUUID") String parentUUID,
                           final @MapsTo("candidate") String candidateUUID) {
        this.parentUUID = PortablePreconditions.checkNotNull("parentUUID",
                                                             parentUUID);
        this.candidateUUID = PortablePreconditions.checkNotNull("candidate",
                                                                candidateUUID);
    }

    public DockNodeCommand(final Node<?, Edge> parent,
                           final Node<?, Edge> candidate) {
        this(parent.getUUID(),
             candidate.getUUID());
        this.parent = parent;
        this.candidate = candidate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = allow(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            final Node<?, Edge> parent = getParent(context);
            final Node<?, Edge> candidate = getCandidate(context);
            // TODO: Create a DockEdgeFactory iface extending EdgeFactory using as content generics type Relationship
            final String uuid = UUID.uuid();
            final Edge<Dock, Node> edge = new EdgeImpl<>(uuid);
            edge.setContent(new Dock());
            edge.setSourceNode(parent);
            edge.setTargetNode(candidate);
            parent.getOutEdges().add(edge);
            candidate.getInEdges().add(edge);
            getMutableIndex(context).addEdge(edge);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        final Element<? extends Definition<?>> parent = (Element<? extends Definition<?>>) getParent(context);
        final Node<Definition<?>, Edge> candidate = (Node<Definition<?>, Edge>) getCandidate(context);
        final Collection<RuleViolation> dockingRuleViolations =
                doEvaluate(context,
                           RuleContextBuilder.GraphContexts.docking(getGraph(context),
                                                                    parent,
                                                                    candidate));
        return new GraphCommandResultBuilder(dockingRuleViolations).build();
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final UnDockNodeCommand undoCommand = new UnDockNodeCommand(parent,
                                                                    candidate);
        return undoCommand.execute(context);
    }

    public Node<?, Edge> getCandidate() {
        return candidate;
    }

    public Node<?, Edge> getParent() {
        return parent;
    }

    @SuppressWarnings("unchecked")
    private Node<?, Edge> getParent(final GraphCommandExecutionContext context) {
        if (null == parent) {
            parent = checkNodeNotNull(context,
                                      parentUUID);
        }
        return parent;
    }

    @SuppressWarnings("unchecked")
    private Node<?, Edge> getCandidate(final GraphCommandExecutionContext context) {
        if (null == candidate) {
            candidate = checkNodeNotNull(context,
                                         candidateUUID);
        }
        return candidate;
    }

    @Override
    public String toString() {
        return "AddDockEdgeCommand [parent=" + parentUUID + ", candidate=" + candidateUUID + "]";
    }
}
