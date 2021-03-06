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

package org.kie.workbench.common.dmn.client.shape.view.decisionservice;

import com.ait.lienzo.client.core.event.AbstractNodeDragEvent;
import com.ait.lienzo.client.core.shape.wires.event.AbstractWiresDragEvent;

public class MoveDividerStepEvent extends AbstractWiresDragEvent<MoveDividerStepHandler> {

    public static final Type<MoveDividerStepHandler> TYPE = new Type<>();

    public MoveDividerStepEvent(final DecisionServiceSVGShapeView shape,
                                final AbstractNodeDragEvent<?> nodeDragEvent) {
        super(shape, nodeDragEvent);
    }

    @Override
    public Type<MoveDividerStepHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final MoveDividerStepHandler handler) {
        handler.onMoveDividerStep(this);
    }
}
