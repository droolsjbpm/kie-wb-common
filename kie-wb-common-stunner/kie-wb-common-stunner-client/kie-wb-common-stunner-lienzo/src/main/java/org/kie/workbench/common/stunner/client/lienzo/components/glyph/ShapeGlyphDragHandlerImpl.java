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

package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.uberfire.mvp.Command;

@Dependent
public class ShapeGlyphDragHandlerImpl implements ShapeGlyphDragHandler<AbstractCanvas> {

    private final LienzoGlyphRenderer<Glyph> glyphLienzoGlyphRenderer;
    private final List<HandlerRegistration> handlerRegistrations;
    private final Supplier<AbsolutePanel> rootPanelSupplier;
    private final Function<Item, LienzoPanel> lienzoPanelBuilder;
    private final BiConsumer<Command, Integer> timer;
    private LienzoPanel dragProxyPanel;

    @Inject
    public ShapeGlyphDragHandlerImpl(final LienzoGlyphRenderers glyphLienzoGlyphRenderer) {
        this(glyphLienzoGlyphRenderer,
             new ArrayList<>(),
             RootPanel::get,
             item -> new LienzoPanel(item.getWidth() * 2,
                                     item.getHeight() * 2),
             (task, millis) -> new Timer() {
                 @Override
                 public void run() {
                     task.execute();
                 }
             }.schedule(millis));
    }

    ShapeGlyphDragHandlerImpl(final LienzoGlyphRenderer<Glyph> glyphLienzoGlyphRenderer,
                              final List<HandlerRegistration> handlerRegistrations,
                              final Supplier<AbsolutePanel> rootPanelSupplier,
                              final Function<Item, LienzoPanel> lienzoPanelBuilder,
                              final BiConsumer<Command, Integer> timer) {
        this.glyphLienzoGlyphRenderer = glyphLienzoGlyphRenderer;
        this.handlerRegistrations = handlerRegistrations;
        this.rootPanelSupplier = rootPanelSupplier;
        this.lienzoPanelBuilder = lienzoPanelBuilder;
        this.timer = timer;
    }

    @Override
    public DragProxy<AbstractCanvas, Item, DragProxyCallback> proxyFor(final AbstractCanvas context) {
        return this;
    }

    @Override
    public DragProxy<AbstractCanvas, Item, DragProxyCallback> show(final Item item,
                                                                   final int x,
                                                                   final int y,
                                                                   final DragProxyCallback dragProxyCallback) {
        // Create the lienzo "proxy" panel instance.
        final Layer dragProxyLayer = new Layer();
        this.dragProxyPanel = lienzoPanelBuilder.apply(item);
        dragProxyPanel.add(dragProxyLayer);
        attachHandlers(dragProxyCallback);

        // Add the glyph instance into the layer.
        dragProxyLayer.add(glyphLienzoGlyphRenderer
                                   .render(item.getShape(),
                                           item.getWidth(),
                                           item.getHeight())
                                   .setX(0)
                                   .setY(0));

        // Handle the proxy panel instance.
        moveProxyTo(x, y);
        rootPanelSupplier.get().add(dragProxyPanel);

        return this;
    }

    @Override
    public void clear() {
        clearState(() -> dragProxyPanel.clear());
    }

    @Override
    public void destroy() {
        clearState(() -> dragProxyPanel.destroy());
    }

    private void moveProxyTo(final double x,
                             final double y) {
        Style style = dragProxyPanel.getElement().getStyle();
        style.setCursor(Style.Cursor.AUTO);
        style.setPosition(Style.Position.ABSOLUTE);
        style.setLeft(x, Style.Unit.PX);
        style.setTop(y, Style.Unit.PX);
        style.setZIndex(Integer.MAX_VALUE);
    }

    private void attachHandlers(final DragProxyCallback callback) {
        final AbsolutePanel root = rootPanelSupplier.get();
        // Mouse move event registration & handling..
        register(
                root.addDomHandler(event -> onMouseMove(event, callback),
                                   MouseMoveEvent.getType())
        );

        // Mouse up event registration & handling..
        //delay to attach the MouseUpEvent handler, to avoid "clicking" to drop item.
        timer.accept(() -> {
            register(
                    root.addDomHandler(event -> onMouseUp(event, callback),
                                       MouseUpEvent.getType())
            );
        }, 200);

        // Keyboard event registration & handling..
        register(root.addDomHandler(this::onKeyDown,
                                    KeyDownEvent.getType()));
    }

    void onMouseMove(final MouseMoveEvent event,
                     final DragProxyCallback callback) {
        final Style proxyPanelStyle = dragProxyPanel.getElement().getStyle();
        proxyPanelStyle.setLeft(event.getX(), Style.Unit.PX);
        proxyPanelStyle.setTop(event.getY(), Style.Unit.PX);
        callback.onMove(event.getClientX(), event.getClientY());
    }

    void onMouseUp(final MouseUpEvent event,
                   final DragProxyCallback callback) {
        clearHandlers();
        rootPanelSupplier.get().remove(dragProxyPanel);
        callback.onComplete(event.getClientX(),
                            event.getClientY());
    }

    void onKeyDown(final KeyDownEvent event) {
        clear();
    }

    private void register(final HandlerRegistration registration) {
        handlerRegistrations.add(registration);
    }

    private void clearHandlers() {
        handlerRegistrations.stream().forEach(HandlerRegistration::removeHandler);
        handlerRegistrations.clear();
    }

    private void clearState(final Command proxyDestroyCommand) {
        clearHandlers();
        if (Objects.nonNull(dragProxyPanel)) {
            rootPanelSupplier.get().remove(dragProxyPanel);
            proxyDestroyCommand.execute();
            dragProxyPanel = null;
        }
    }
}