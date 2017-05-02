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

package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.kie.workbench.common.stunner.core.client.shape.Lifecycle;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.NodeShape;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

/**
 * The default Shape implementation for nodes. It acts as the bridge between a node and the shape view.
 * <p>
 * This implementation relies on ShapeDefinitions. This way provides the bridge between the node and it's
 * bean definition instance, and delegates the interaction logic between the definition instance and the shape's
 * view to a ShapeDefViewHandler type.
 * @param <W> The bean type.
 * @param <V> The view type.
 * @param <D> The mutable shape definition type..
 */
public class NodeShapeImpl<W, D extends MutableShapeDef<W>, V extends ShapeView<?>>
        extends AbstractElementShape<W, View<W>, Node<View<W>, Edge>, D, V>
        implements NodeShape<W, View<W>, Node<View<W>, Edge>, V>,
                   Lifecycle {

    public NodeShapeImpl(final D shapeDef,
                         final V view) {
        super(shapeDef,
              view);
    }

    public NodeShapeImpl(final D shapeDef,
                         final V view,
                         final ShapeStateHelper<V, Shape<V>> shapeStateHelper) {
        super(shapeDef,
              view,
              shapeStateHelper);
    }

    @Override
    public void applyPosition(final Node<View<W>, Edge> element,
                              final MutationContext mutationContext) {
        final Point2D position = GraphUtils.getPosition(element.getContent());
        getShapeView().setShapeX(position.getX());
        getShapeView().setShapeY(position.getY());
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        getShape().applyState(shapeState);
    }
}
