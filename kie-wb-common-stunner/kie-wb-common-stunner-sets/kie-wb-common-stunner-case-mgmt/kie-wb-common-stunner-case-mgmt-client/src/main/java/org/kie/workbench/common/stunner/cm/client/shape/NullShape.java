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

package org.kie.workbench.common.stunner.cm.client.shape;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.cm.client.shape.def.NullShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.view.NullView;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.AbstractElementShape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class NullShape extends AbstractElementShape<BPMNDefinition, View<BPMNDefinition>, Node<View<BPMNDefinition>, Edge>, NullShapeDef, NullView> {

    public NullShape(final NullShapeDef shapeDef,
                     final NullView view) {
        super(shapeDef,
              view);
    }

    @Override
    public void applyPosition(final Node<View<BPMNDefinition>, Edge> element,
                              final MutationContext mutationContext) {
        //A NullShape should not be rendered; and represents a BPMN2 node that is not rendered for Case Management
    }

    @Override
    public void applyProperties(final Node<View<BPMNDefinition>, Edge> element,
                                final MutationContext mutationContext) {
        //A NullShape should not be rendered; and represents a BPMN2 node that is not rendered for Case Management
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        //A NullShape should not be rendered; and represents a BPMN2 node that is not rendered for Case Management
    }
}
