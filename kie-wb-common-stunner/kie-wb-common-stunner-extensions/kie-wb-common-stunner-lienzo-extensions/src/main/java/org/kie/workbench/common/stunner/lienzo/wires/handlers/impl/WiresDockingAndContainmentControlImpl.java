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

package org.kie.workbench.common.stunner.lienzo.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.WiresUtils;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingAndContainmentControl;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.FillGradient;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;

public class WiresDockingAndContainmentControlImpl
        implements WiresDockingAndContainmentControl {

    protected WiresShape m_shape;

    protected WiresContainer m_parent;

    protected WiresLayer m_layer;

    protected ColorMapBackedPicker m_picker;

    private WiresManager m_wiresManager;

    private String m_priorFill;

    private boolean m_priorFillChanged;

    private FillGradient m_priorFillGradient;

    private double m_priorAlpha;

    private PickerPart m_parentPart;

    private MultiPath m_path;

    protected double m_mouseStartX;

    protected double m_mouseStartY;

    private double m_shapeStartCenterX;

    private double m_shapeStartCenterY;

    private double m_shapeStartX;

    private double m_shapeStartY;

    private boolean m_startDocked;

    public WiresDockingAndContainmentControlImpl(final WiresShape shape,
                                                 final WiresManager wiresManager) {
        m_shape = shape;
        m_wiresManager = wiresManager;
        m_layer = m_wiresManager.getLayer();
        m_priorFillChanged = false;
    }

    @Override
    public ColorMapBackedPicker getPicker() {
        return m_picker;
    }

    @Override
    public void dragStart(final Context context) {
        final Point2D absShapeLoc = WiresUtils.getLocation(m_shape.getPath());
        final BoundingBox box = m_shape.getPath().getBoundingBox();
        m_shapeStartX = absShapeLoc.getX();
        m_shapeStartY = absShapeLoc.getY();

        m_shapeStartCenterX = m_shapeStartX + (box.getWidth() / 2);
        m_shapeStartCenterY = m_shapeStartY + (box.getHeight() / 2);

        m_mouseStartX = context.getX();
        m_mouseStartY = context.getY();

        m_startDocked = false;

        m_parent = m_shape.getParent();

        m_picker = makeColorMapBackedPicker(m_layer,
                                            m_parent,
                                            m_shape);

        if (m_parent != null && m_parent instanceof WiresShape) {
            if (m_shape.getDockedTo() == null) {
                highlightBody((WiresShape) m_parent);
                m_parentPart = new PickerPart((WiresShape) m_parent,
                                              PickerPart.ShapePart.BODY);
            } else {
                highlightBorder((WiresShape) m_parent);
                m_parentPart = m_picker.findShapeAt((int) m_shapeStartCenterX,
                                                    (int) m_shapeStartCenterY);
                m_startDocked = true;
            }
            m_layer.getLayer().batch();
            m_layer.getLayer().getOverLayer().batch();
        }
    }

    @Override
    public void dragMove(final Context context) {
        // Nothing to do.
    }

    @Override
    public void dragEnd(final Context context) {
        addShapeToParent();
    }

    @Override
    public boolean dragAdjust(final Point2D dxy) {

        int x = 0;
        int y = 0;
        if (m_startDocked) {
            x = (int) m_shapeStartCenterX;
            y = (int) m_shapeStartCenterY;
        } else {
            x = (int) m_mouseStartX;
            y = (int) m_mouseStartY;
        }

        WiresContainer parent = null;
        x = (int) (x + dxy.getX());
        y = (int) (y + dxy.getY());
        PickerPart parentPart = m_picker.findShapeAt(x,
                                                     y);

        if (parentPart != null) {
            parent = parentPart.getShape();
        }

        if (parent != m_parent || parentPart != m_parentPart) {

            // Create and populate again the color map picker
            // in order to include shapes hotspots
            // in case docking is allowed for the new parent (and was
            // not for the old parent).
            m_picker = makeColorMapBackedPicker(m_layer,
                                                parent,
                                                m_shape);
            parentPart = m_picker.findShapeAt(x,
                                              y);
            parent = null != parentPart ? parentPart.getShape() : null;

            boolean batch = false;

            if (m_parent != null && m_parent instanceof WiresShape) {
                if (m_parentPart != null && m_parentPart.getShapePart() == PickerPart.ShapePart.BODY) {
                    restoreBody();
                } else if (m_path != null) {
                    m_path.removeFromParent();
                    m_path = null;
                    m_shape.setDockedTo(null);
                    m_startDocked = false;
                }
                batch = true;
            }

            if (parent != null && parent instanceof WiresShape) {
                if (parentPart.getShapePart() == PickerPart.ShapePart.BODY) {
                    if (parent.getContainmentAcceptor().containmentAllowed(parent,
                                                                           m_shape)) {
                        highlightBody((WiresShape) parent);
                    }
                } else if (parent.getDockingAcceptor().dockingAllowed(parent,
                                                                      m_shape)) {
                    highlightBorder((WiresShape) parent);
                } else {
                    // There is no valid parentPart, so null it.
                    parentPart = null;
                }
                batch = true;
            }

            if (batch) {
                m_layer.getLayer().batch();
                m_layer.getLayer().getOverLayer().batch();
            }
        }
        m_parent = parent;
        m_parentPart = parentPart;

        if (m_path != null) {
            Point2D absLoc = WiresUtils.getLocation(m_parent.getGroup());// convert to local xy of the path
            Point2D intersection = Geometry.findIntersection((int) (x - absLoc.getX()),
                                                             (int) (y - absLoc.getY()),
                                                             ((WiresShape) m_parent).getPath());
            if (intersection != null) {
                BoundingBox box = m_shape.getPath().getBoundingBox();
                double newX = absLoc.getX() + intersection.getX() - (box.getWidth() / 2);
                double newY = absLoc.getY() + intersection.getY() - (box.getHeight() / 2);
                dxy.setX(newX - m_shapeStartX).setY(newY - m_shapeStartY);
                return true;
            }
        }

        return false;
    }

    private void restoreBody() {
        if (m_priorFillChanged) {
            ((WiresShape) m_parent).getPath().setFillColor(m_priorFill);
            if (m_priorFillGradient instanceof LinearGradient) {
                ((WiresShape) m_parent).getPath().setFillGradient((LinearGradient) m_priorFillGradient);
            } else if (m_priorFillGradient instanceof PatternGradient) {
                ((WiresShape) m_parent).getPath().setFillGradient((PatternGradient) m_priorFillGradient);
            } else if (m_priorFillGradient instanceof RadialGradient) {
                ((WiresShape) m_parent).getPath().setFillGradient((RadialGradient) m_priorFillGradient);
            }
            ((WiresShape) m_parent).getPath().setFillAlpha(m_priorAlpha);

            m_priorFillChanged = false;
            m_priorFill = null;
            m_priorFillGradient = null;
            m_priorAlpha = 0.0;
        }
    }

    private void highlightBorder(final WiresShape parent) {
        MultiPath path = parent.getPath();
        m_path = path.copy();
        m_path.setStrokeWidth(parent.getDockingAcceptor().getHotspotSize());
        final Point2D absLoc = WiresUtils.getLocation(path);
        m_path.setX(absLoc.getX());
        m_path.setY(absLoc.getY());
        m_path.setStrokeColor("#CC1100");
        m_path.setStrokeAlpha(0.8);
        m_layer.getLayer().getOverLayer().add(m_path);
    }

    private void highlightBody(final WiresShape parent) {
        m_priorFill = parent.getPath().getFillColor();
        m_priorFillGradient = parent.getPath().getFillGradient();
        m_priorAlpha = parent.getPath().getFillAlpha();
        parent.getPath().setFillColor("#CCCCCC");
        parent.getPath().setFillAlpha(0.8);
        m_priorFillChanged = true;
    }

    @Override
    public void onNodeMouseDown() {
        m_parent = m_shape.getParent();
    }

    @Override
    public void onNodeMouseUp() {
        if (m_parent != m_shape.getParent()) {
            addShapeToParent();
        }
    }

    protected void addShapeToParent() {
        Point2D absLoc = WiresUtils.getLocation(m_shape.getGroup());

        if (m_parent == null) {
            m_parent = m_layer;
        }

        if (m_path != null) {
            m_path.removeFromParent();
            m_layer.getLayer().getOverLayer().batch();
        }

        if (m_parentPart == null || m_parentPart.getShapePart() == PickerPart.ShapePart.BODY) {
            if (m_parent.getContainmentAcceptor().acceptContainment(m_parent,
                                                                    m_shape)) {
                if (m_parent instanceof WiresShape) {
                    restoreBody();
                }

                if (m_parent == m_layer) {
                    m_parent.getLayoutHandler().add(m_shape,
                                                    m_parent,
                                                    absLoc);
                } else {
                    final Point2D trgAbsOffset = WiresUtils.getLocation(m_parent.getGroup());
                    final Point2D relativeLoc = new Point2D(absLoc.getX() - trgAbsOffset.getX(),
                                                            absLoc.getY() - trgAbsOffset.getY());
                    m_parent.getLayoutHandler().add(m_shape,
                                                    m_parent,
                                                    relativeLoc);
                }

                m_shape.setDockedTo(null);

                m_layer.getLayer().batch();
            }
        } else if (m_parentPart != null && m_parentPart.getShapePart() != PickerPart.ShapePart.BODY && m_parent.getDockingAcceptor().acceptDocking(m_parent,
                                                                                                                                                   m_shape)) {
            m_shape.removeFromParent();

            final Point2D trgAbsOffset = WiresUtils.getLocation(m_parent.getContainer());
            m_shape.getGroup().setX(absLoc.getX() - trgAbsOffset.getX()).setY(absLoc.getY() - trgAbsOffset.getY());
            m_parent.add(m_shape);

            m_shape.setDockedTo(m_parent);

            m_layer.getLayer().batch();
        } else {
            throw new IllegalStateException("Defensive Programming: Should not happen");
        }

        m_parent = null;
        m_parentPart = null;
        m_priorFill = null;
        m_priorFillChanged = false;
        m_priorFillGradient = null;
        m_picker = null;
    }

    protected ColorMapBackedPicker makeColorMapBackedPicker(final NFastArrayList<WiresShape> children,
                                                            final ScratchPad scratchPad,
                                                            final WiresShape shape,
                                                            final boolean isDockingAllowed,
                                                            final int hotSpotSize) {
        return new ColorMapBackedPicker(children,
                                        scratchPad,
                                        shape,
                                        isDockingAllowed,
                                        hotSpotSize);
    }

    protected ColorMapBackedPicker makeColorMapBackedPicker(final WiresLayer m_layer,
                                                            final WiresContainer m_parent,
                                                            final WiresShape m_shape) {
        return makeColorMapBackedPicker(m_layer.getChildShapes(),
                                        m_layer.getLayer().getScratchPad(),
                                        m_shape,
                                        m_shape.getDockingAcceptor().dockingAllowed(m_parent,
                                                                                    m_shape),
                                        m_shape.getDockingAcceptor().getHotspotSize());
    }
}
