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

import java.util.Optional;
import java.util.function.Predicate;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class ShapeStateHelper<V extends ShapeView, S extends Shape<V>> {

    static final double ACTIVE_STROKE_WIDTH_PCT = 1d;
    static final double ACTIVE_STROKE_ALPHA = 1d;

    /*
        The following instance members:
            - strokeWidth
            - strokeAlpha
            - strokeColor
        Are used to keep the original stroke attributes from the
        domain model object because the behavior for changing
        this shape state is based on updating the shape's borders.
        Eg: when the shape is in SELECTED state, the borders are
        using different colors/sizes, so to be able to get
        back to NONE state, it just reverts the border attributes
        to these private instance members.
     */
    private double strokeWidth;
    private double strokeAlpha;
    private String strokeColor;
    private ShapeState state;
    private double activeStrokeWidth;
    private Optional<S> shape = Optional.empty();

    public ShapeStateHelper() {
        forShape(null);
    }

    public ShapeStateHelper(final S shape) {
        forShape(shape);
    }

    public ShapeStateHelper forShape(final S shape) {
        this.shape = Optional.ofNullable(shape);
        this.state = ShapeState.NONE;
        save((state) -> true);
        return this;
    }

    public void setStrokeWidthForActiveState(final double activeStrokeWidth) {
        this.activeStrokeWidth = activeStrokeWidth;
    }

    public ShapeStateHelper save(final Predicate<ShapeState> stateFilter) {
        if (stateFilter.test(this.state)) {
            shape.ifPresent((s) -> this.strokeWidth = s.getShapeView().getStrokeWidth());
            shape.ifPresent((s) -> this.activeStrokeWidth = strokeWidth + (strokeWidth * ACTIVE_STROKE_WIDTH_PCT));
            shape.ifPresent((s) -> this.strokeAlpha = s.getShapeView().getStrokeAlpha());
            shape.ifPresent((s) -> this.strokeColor = s.getShapeView().getStrokeColor());
        }
        return this;
    }

    public ShapeStateHelper applyState(final ShapeState shapeState) {
        if (!this.state.equals(shapeState)) {
            this.state = shapeState;
            if (ShapeState.SELECTED.equals(shapeState)) {
                applySelectedState();
            } else if (ShapeState.HIGHLIGHT.equals(shapeState)) {
                applyHighlightState();
            } else if (ShapeState.INVALID.equals(shapeState)) {
                applyInvalidState();
            } else {
                applyNoneState(strokeColor,
                               strokeWidth,
                               strokeAlpha);
            }
        }
        return this;
    }

    public ShapeState getState() {
        return state;
    }

    protected void applyActiveState(final String color) {
        getShapeView().setStrokeColor(color);
        getShapeView().setStrokeWidth(getActiveStrokeWidth());
        getShapeView().setStrokeAlpha(getActiveStrokeAlpha());
    }

    protected void applyNoneState(final String color,
                                  final double width,
                                  final double alpha) {
        getShapeView().setStrokeColor(color);
        getShapeView().setStrokeWidth(width);
        getShapeView().setStrokeAlpha(alpha);
    }

    protected S getShape() {
        return shape.orElseThrow(() -> new IllegalArgumentException("Shape has not been set."));
    }

    protected double getActiveStrokeWidth() {
        return activeStrokeWidth;
    }

    protected static double getActiveStrokeAlpha() {
        return ACTIVE_STROKE_ALPHA;
    }

    private void applySelectedState() {
        applyActiveState(ShapeState.SELECTED.getColor());
    }

    private void applyInvalidState() {
        applyActiveState(ShapeState.INVALID.getColor());
    }

    private void applyHighlightState() {
        applyActiveState(ShapeState.HIGHLIGHT.getColor());
    }

    private V getShapeView() {
        return shape.orElseThrow(() -> new IllegalArgumentException("Shape has not been set.")).getShapeView();
    }
}
