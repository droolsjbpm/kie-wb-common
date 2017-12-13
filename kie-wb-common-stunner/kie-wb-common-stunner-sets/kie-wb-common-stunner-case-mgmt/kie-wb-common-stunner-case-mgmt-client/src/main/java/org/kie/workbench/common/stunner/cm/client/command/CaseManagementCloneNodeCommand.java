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

package org.kie.workbench.common.stunner.cm.client.command;

import java.util.function.Consumer;

import org.kie.workbench.common.stunner.cm.client.command.canvas.CaseManagementCloneCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.CloneNodeCommand;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;

public class CaseManagementCloneNodeCommand extends CloneNodeCommand {

    public CaseManagementCloneNodeCommand(Node candidate, String parentUuid, Point2D cloneLocation, Consumer<Node> cloneNodeCommandCallback, ChildrenTraverseProcessor childrenTraverseProcessor) {
        super(candidate, parentUuid, cloneLocation, cloneNodeCommandCallback, childrenTraverseProcessor);
    }

    @Override
    public CaseManagementCloneCanvasNodeCommand getCloneCanvasNodeCommand(Node parent, Node clone, String shapeId) {
        return new CaseManagementCloneCanvasNodeCommand(parent, clone, shapeId, getChildrenTraverseProcessor());
    }
}
