/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.BoundsExceededViolation;

/**
 * A Command to update an element's bounds.
 */
@Portable
public final class UpdateElementPositionCommand extends AbstractGraphCommand {

    private static Logger LOGGER = Logger.getLogger(UpdateElementPositionCommand.class.getName());

    private final String uuid;
    private final Point2D location;
    private final Point2D previousLocation;
    private transient Node<? extends View<?>, Edge> node;
    private Element<? extends View<?>> parent;
    private Boolean isDocked;

    public UpdateElementPositionCommand(final @MapsTo("uuid") String uuid,
                                        final @MapsTo("location") Point2D location,
                                        final @MapsTo("previousLocation") Point2D previousLocation) {
        this.uuid = PortablePreconditions.checkNotNull("uuid",
                                                       uuid);
        this.location = PortablePreconditions.checkNotNull("location",
                                                           location);
        this.previousLocation = PortablePreconditions.checkNotNull("previousLocation",
                                                                   previousLocation);
        this.node = null;
    }

    public UpdateElementPositionCommand(final Node<? extends View<?>, Edge> node,
                                        final Point2D location) {
        this(node.getUUID(),
             location,
             GraphUtils.getPosition(node.getContent()));
        this.node = PortablePreconditions.checkNotNull("node",
                                                       node);
    }

    public Point2D getLocation() {
        return location;
    }

    public Point2D getPreviousLocation() {
        return previousLocation;
    }

    public Node<?, Edge> getNode() {
        return node;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        return execute(context,
                       bounds -> {
                       });
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        return execute(context,
                       bounds -> node.getContent().setBounds(bounds));
    }

    @SuppressWarnings("unchecked")
    private CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context,
                                                 final Consumer<Bounds> boundsConsumer) {
        final Graph<DefinitionSet, Node> graph = (Graph<DefinitionSet, Node>) getGraph(context);
        final Node<? extends View<?>, Edge> element = getNodeNotNull(context);
        final Bounds targetBounds = getTargetBounds(element);
        final Bounds parentBounds = getParentBounds(getNodeNotNull(context), graph);
        boundsConsumer.accept(targetBounds);
        if (areBoundsExceeded(element, targetBounds, parentBounds)) {
            return new GraphCommandResultBuilder()
                    .addViolation(new BoundsExceededViolation(parentBounds)
                                          .setUUID(node.getUUID()))
                    .build();
        }
        return GraphCommandResultBuilder.SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private boolean areBoundsExceeded(final Node<? extends View<?>, Edge> element,
                                      final Bounds bounds,
                                      final Bounds parentBounds) {
        return !(GraphUtils.checkBoundsExceeded(parentBounds, bounds) || isDockedNode(element));
    }

    private boolean isDockedNode(Node<? extends View<?>, Edge> element) {
        if (Objects.isNull(isDocked)) {
            isDocked = GraphUtils.isDockedNode(element);
        }
        return isDocked;
    }

    @SuppressWarnings("unchecked")
    private Bounds getTargetBounds(final Element<? extends View<?>> element) {
        return computeCandidateBounds(element, location);
    }

    @SuppressWarnings("unchecked")
    private Bounds getParentBounds(Element element, final Graph<DefinitionSet, Node> graph) {
        if (Objects.isNull(parent)) {
            //set the parent on the first execution, this avoid getting a wrong parent when undoing the command in case the parent has changed
            parent = (Element<? extends View<?>>) GraphUtils.getParent((Node) element);
        }

        if (parent != null && !GraphUtils.isRootNode(parent, graph)) {
            final double[] size = GraphUtils.getNodeSize(parent.getContent());
            return Bounds.create(0d, 0d, size[0], size[1]);
        } else {
            return null;
        }
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final UpdateElementPositionCommand undoCommand = new UpdateElementPositionCommand(getNodeNotNull(context),
                                                                                          previousLocation);
        return undoCommand.execute(context);
    }

    private Node<? extends View<?>, Edge> getNodeNotNull(final GraphCommandExecutionContext context) {
        if (null == node) {
            node = super.getNodeNotNull(context,
                                        uuid);
        }
        return node;
    }

    public static Bounds computeCandidateBounds(final Element<? extends View<?>> element,
                                                final Point2D location) {
        final double[] oldSize = GraphUtils.getNodeSize(element.getContent());
        final double w = oldSize[0];
        final double h = oldSize[1];
        return Bounds.create(location.getX(),
                             location.getY(),
                             location.getX() + w,
                             location.getY() + h);
    }

    @Override
    public String toString() {
        return "UpdateElementPositionCommand " +
                "[element=" + uuid +
                ", location=" + location +
                ", previousLocation=" + previousLocation +
                "]";
    }
}