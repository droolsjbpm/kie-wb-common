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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDecorators;
import org.kie.workbench.common.stunner.core.client.shape.view.IsConnector;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.DiscreteConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class WiresConnectorView<T> extends WiresConnector
        implements
        ShapeView<T>,
        IsConnector<T>,
        HasControlPoints<T>,
        HasDecorators<Shape<?>> {

    protected String uuid;
    private WiresConnectorControl connectorControl;

    public WiresConnectorView(final AbstractDirectionalMultiPointShape<?> line,
                              final MultiPathDecorator headDecorator,
                              final MultiPathDecorator tailDecorator) {
        super(line,
              headDecorator,
              tailDecorator);
        init();
    }

    public WiresConnectorView(final WiresMagnet headMagnet,
                              final WiresMagnet tailMagnet,
                              final AbstractDirectionalMultiPointShape<?> line,
                              final MultiPathDecorator headDecorator,
                              final MultiPathDecorator tailDecorator) {
        super(headMagnet,
              tailMagnet,
              line,
              headDecorator,
              tailDecorator);
        init();
    }

    private void init() {
        getLine().setFillColor(ColorName.WHITE).setStrokeWidth(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setUUID(final String uuid) {
        this.uuid = uuid;
        WiresUtils.assertShapeUUID(this.getGroup(),
                                   uuid);
        return cast();
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @SuppressWarnings("unchecked")
    public T setControl(final WiresConnectorControl connectorControl) {
        this.connectorControl = connectorControl;
        return (T) this;
    }

    public WiresConnectorControl getControl() {
        return connectorControl;
    }

    @SuppressWarnings("unchecked")
    public T connect(final ShapeView headShapeView,
                     final Connection headConnection,
                     final ShapeView tailShapeView,
                     final Connection tailConnection) {
        final WiresShape headWiresShape = (WiresShape) headShapeView;
        final WiresShape tailWiresShape = (WiresShape) tailShapeView;
        return connect(headWiresShape.getMagnets(),
                       headWiresShape.getGroup().getComputedLocation(),
                       headConnection,
                       tailWiresShape.getMagnets(),
                       tailWiresShape.getGroup().getComputedLocation(),
                       tailConnection);
    }

    T connect(final MagnetManager.Magnets headMagnets,
              final com.ait.lienzo.client.core.types.Point2D headAbsoluteLoc,
              final Connection headConnection,
              final MagnetManager.Magnets tailMagnets,
              final com.ait.lienzo.client.core.types.Point2D tailAbsoluteLoc,
              final Connection tailConnection) {
        // Update head connection.
        updateConnection(headConnection,
                         headMagnets,
                         headAbsoluteLoc,
                         isAuto -> getHeadConnection().setAutoConnection(isAuto),
                         this::applyHeadMagnet);
        // Update tail connection.
        updateConnection(tailConnection,
                         tailMagnets,
                         tailAbsoluteLoc,
                         isAuto -> getTailConnection().setAutoConnection(isAuto),
                         this::applyTailMagnet);
        return cast();
    }

    @Override
    public double getShapeX() {
        return getGroup().getX();
    }

    @Override
    public double getShapeY() {
        return getGroup().getY();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setShapeX(final double x) {
        getGroup().setX(x);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setShapeY(final double y) {
        getGroup().setY(y);
        return (T) this;
    }

    @Override
    public double getAlpha() {
        return getGroup().getAlpha();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setAlpha(final double alpha) {
        getGroup().setAlpha(alpha);
        return (T) this;
    }

    @Override
    public Point2D getShapeAbsoluteLocation() {
        return WiresUtils.getAbsolute(getGroup());
    }

    @Override
    public String getFillColor() {
        return getLine().getFillColor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setFillColor(final String color) {
        getLine().setFillColor(color);
        if (null != getHead()) {
            getHead().setFillColor(color);
        }
        if (null != getTail()) {
            getTail().setFillColor(color);
        }
        return (T) this;
    }

    @Override
    public double getFillAlpha() {
        return getLine().getFillAlpha();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setFillAlpha(final double alpha) {
        getLine().setFillAlpha(alpha);
        if (null != getHead()) {
            getHead().setFillAlpha(alpha);
        }
        if (null != getTail()) {
            getTail().setFillAlpha(alpha);
        }
        return (T) this;
    }

    @Override
    public String getStrokeColor() {
        return getLine().getStrokeColor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setStrokeColor(final String color) {
        getLine().setStrokeColor(color);
        if (null != getHead()) {
            getHead().setStrokeColor(color);
        }
        if (null != getTail()) {
            getTail().setStrokeColor(color);
        }
        return (T) this;
    }

    @Override
    public double getStrokeAlpha() {
        return getLine().getStrokeAlpha();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setStrokeAlpha(final double alpha) {
        getLine().setStrokeAlpha(alpha);
        if (null != getHead()) {
            getHead().setStrokeAlpha(alpha);
        }
        if (null != getTail()) {
            getTail().setStrokeAlpha(alpha);
        }
        return (T) this;
    }

    @Override
    public double getStrokeWidth() {
        return getLine().getStrokeWidth();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setStrokeWidth(final double width) {
        getLine().setStrokeWidth(width);
        if (null != getHead()) {
            getHead().setStrokeWidth(width);
        }
        if (null != getTail()) {
            getTail().setStrokeWidth(width);
        }
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveToTop() {
        getGroup().moveToTop();
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveToBottom() {
        getGroup().moveToBottom();
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveUp() {
        getGroup().moveUp();
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveDown() {
        getGroup().moveDown();
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T showControlPoints(final ControlPointType type) {
        if (null != getControl()) {
            if (ControlPointType.POINTS.equals(type)) {
                getControl().showControlPoints();
            } else {
                throw new UnsupportedOperationException("Control point type [" + type + "] not supported yet");
            }
        }
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T hideControlPoints() {
        if (null != getControl()) {
            getControl().hideControlPoints();
        }
        return (T) this;
    }

    @Override
    public boolean areControlsVisible() {
        return getPointHandles().isVisible();
    }

    @Override
    public List<Shape<?>> getDecorators() {
        final List<Shape<?>> decorators = new ArrayList<>(3);
        decorators.add(getLine());
        if (null != getHead()) {
            decorators.add(getHead());
        }
        if (null != getTail()) {
            decorators.add(getTail());
        }
        return decorators;
    }

    @Override
    public void removeFromParent() {
        // Remove the main line.
        super.removeFromLayer();
    }

    @Override
    public void destroy() {
        // Remove me.
        super.destroy();
        this.connectorControl = null;
    }

    private WiresConnector applyHeadMagnet(final WiresMagnet headMagnet) {
        ifNotSpecialConnection(getHeadConnection(),
                               headMagnet,
                               WiresConnectorView::clearConnectionOffset);
        return super.setHeadMagnet(headMagnet);
    }

    private WiresConnector applyTailMagnet(final WiresMagnet tailMagnet) {
        ifNotSpecialConnection(getTailConnection(),
                               tailMagnet,
                               WiresConnectorView::clearConnectionOffset);
        return super.setTailMagnet(tailMagnet);
    }

    private static void updateConnection(final Connection connection,
                                         final MagnetManager.Magnets magnets,
                                         final com.ait.lienzo.client.core.types.Point2D abeLocation,
                                         final Consumer<Boolean> isAutoConnectionConsumer,
                                         final Consumer<WiresMagnet> magnetConsumer) {
        final WiresMagnet[] magnet = new WiresMagnet[]{null};
        final boolean[] auto = new boolean[]{false};
        if (null != connection) {
            final DiscreteConnection dc = connection instanceof DiscreteConnection ?
                    (DiscreteConnection) connection : null;
            if (null != dc) {
                // Obtain the magnet index and auto flag, if the connection is a discrete type and it has been already set.
                dc.getMagnetIndex().ifPresent(index -> magnet[0] = magnets.getMagnet(index));
                auto[0] = dc.isAuto();
            }
            // If still no magnet found from the connection's cache, figure it out as by the connection's location.
            if (null == magnet[0]) {
                magnet[0] = getMagnetForConnection(connection,
                                                   magnets,
                                                   abeLocation);
                // Update the discrete connection magnet cached index, if possible.
                if (null != dc) {
                    dc.setIndex(magnet[0].getIndex());
                }
            }
        }
        // Call the magnet and auto connection consumers to assign the new values.
        isAutoConnectionConsumer.accept(auto[0]);
        magnetConsumer.accept(magnet[0]);
    }

    private static WiresMagnet getMagnetForConnection(final Connection connection,
                                                      final MagnetManager.Magnets magnets,
                                                      final com.ait.lienzo.client.core.types.Point2D absLocation) {
        if (null != connection) {
            Point2D magnetAbs = new Point2D(absLocation.getX() + connection.getLocation().getX(),
                                            absLocation.getY() + connection.getLocation().getY());
            return getMagnetNearTo(magnets,
                                   magnetAbs);
        }
        return null;
    }

    /**
     * Returns the Lienzo's magnet instance which location is closer to the magnet definition
     */
    private static WiresMagnet getMagnetNearTo(final MagnetManager.Magnets magnets,
                                               final Point2D location) {
        return (WiresMagnet) StreamSupport
                .stream(magnets.getMagnets().spliterator(),
                        false)
                .sorted((m1, m2) -> compare(m1,
                                            m2,
                                            location))
                .findFirst()
                .get();
    }

    private static int compare(final IControlHandle m1,
                               final IControlHandle m2,
                               final Point2D location) {
        final double mx = location.getX();
        final double my = location.getY();
        final com.ait.lienzo.client.core.types.Point2D m1p = m1.getControl().getLocation();
        final com.ait.lienzo.client.core.types.Point2D m2p = m2.getControl().getLocation();
        final double d1 = ShapeUtils.dist(mx,
                                          my,
                                          m1p.getX(),
                                          m1p.getY());
        final double d2 = ShapeUtils.dist(mx,
                                          my,
                                          m2p.getX(),
                                          m2p.getY());
        return Double.compare(d1,
                              d2);
    }

    private static void ifNotSpecialConnection(final WiresConnection connection,
                                               final WiresMagnet magnet,
                                               final Consumer<WiresConnection> regularConnectionConsumer) {
        if (!WiresConnection.isSpecialConnection(connection.isAutoConnection(),
                                                 null != magnet ? magnet.getIndex() : null)) {
            regularConnectionConsumer.accept(connection);
        }
    }

    private static void clearConnectionOffset(final WiresConnection connection) {
        connection.setXOffset(0d);
        connection.setYOffset(0d);
    }

    private T cast() {
        return (T) this;
    }
}
