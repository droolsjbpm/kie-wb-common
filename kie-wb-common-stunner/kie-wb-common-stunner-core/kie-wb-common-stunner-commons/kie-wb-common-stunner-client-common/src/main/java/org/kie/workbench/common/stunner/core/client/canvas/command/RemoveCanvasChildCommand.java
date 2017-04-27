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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;

/**
 * Removes the parent-child relationship between two nodes in the canvas context.
 */
public class RemoveCanvasChildCommand extends AbstractCanvasCommand {

    private final Node parent;
    private final Node child;

    public RemoveCanvasChildCommand(final Node parent,
                                    final Node child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        context.removeChild(parent,
                            child);
        context.applyElementMutation(parent,
                                     MutationContext.STATIC);
        context.applyElementMutation(child,
                                     MutationContext.STATIC);
        ShapeUtils.moveViewConnectorsToTop(context,
                                           child);
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new SetCanvasChildNodeCommand(parent,
                                             child).execute(context);
    }

    public Node getParent() {
        return parent;
    }

    public Node getChild() {
        return child;
    }
}
