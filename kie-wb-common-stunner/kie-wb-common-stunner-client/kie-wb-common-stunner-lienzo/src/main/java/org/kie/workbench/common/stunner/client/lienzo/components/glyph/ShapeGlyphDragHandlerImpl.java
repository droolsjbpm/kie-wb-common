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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Dependent
public class ShapeGlyphDragHandlerImpl implements ShapeGlyphDragHandler {

    private static final int ZINDEX = Integer.MAX_VALUE;

    private final LienzoGlyphRenderer<Glyph> glyphLienzoGlyphRenderer;

    @Inject
    public ShapeGlyphDragHandlerImpl(final LienzoGlyphRenderers glyphLienzoGlyphRenderer) {
        this.glyphLienzoGlyphRenderer = glyphLienzoGlyphRenderer;
    }

    @Override
    public void show(final Glyph shapeGlyph,
                     final double x,
                     final double y,
                     final double width,
                     final double height,
                     final Callback callback) {
        final Group dragShape = glyphLienzoGlyphRenderer.render(shapeGlyph,
                                                                width,
                                                                height);
        dragShape.setX(0);
        dragShape.setY(0);
        final LienzoPanel dragProxyPanel = new LienzoPanel(((int) width * 2),
                                                           ((int) height * 2));
        dragProxyPanel.getElement().getStyle().setCursor(Style.Cursor.AUTO);
        final Layer dragProxyLayer = new Layer();
        dragProxyLayer.add(dragShape);
        dragProxyPanel.add(dragProxyLayer);
        dragProxyLayer.batch();
        setDragProxyPosition(dragProxyPanel,
                             width,
                             height,
                             x,
                             y);
        attachDragProxyHandlers(dragProxyPanel,
                                callback);
        RootPanel.get().add(dragProxyPanel);
    }

    private void setDragProxyPosition(final LienzoPanel dragProxyPanel,
                                      final double proxyWidth,
                                      final double proxyHeight,
                                      final double x,
                                      final double y) {
        Style style = dragProxyPanel.getElement().getStyle();
        style.setPosition(Style.Position.ABSOLUTE);
        style.setLeft(x,
                      Style.Unit.PX);
        style.setTop(y,
                     Style.Unit.PX);
        style.setZIndex(ZINDEX);
    }

    private void attachDragProxyHandlers(final LienzoPanel floatingPanel,
                                         final Callback callback) {
        final Style style = floatingPanel.getElement().getStyle();
        final HandlerRegistration[] handlerRegs = new HandlerRegistration[2];
        //MouseMoveEvents
        handlerRegs[0] = RootPanel.get().addDomHandler(new MouseMoveHandler() {

                                                           @Override
                                                           public void onMouseMove(final MouseMoveEvent mouseMoveEvent) {
                                                               style.setLeft(mouseMoveEvent.getX(),
                                                                             Style.Unit.PX);
                                                               style.setTop(mouseMoveEvent.getY(),
                                                                            Style.Unit.PX);
                                                               final double x = mouseMoveEvent.getX();
                                                               final double y = mouseMoveEvent.getY();
                                                               callback.onMove(x,
                                                                               y);
                                                           }
                                                       },
                                                       MouseMoveEvent.getType());
        //MouseUpEvent
        handlerRegs[1] = RootPanel.get().addDomHandler(new MouseUpHandler() {

                                                           @Override
                                                           public void onMouseUp(final MouseUpEvent mouseUpEvent) {
                                                               handlerRegs[0].removeHandler();
                                                               handlerRegs[1].removeHandler();
                                                               RootPanel.get().remove(floatingPanel);
                                                               final double x = mouseUpEvent.getX();
                                                               final double y = mouseUpEvent.getY();
                                                               callback.onComplete(x,
                                                                                   y);
                                                           }
                                                       },
                                                       MouseUpEvent.getType());
    }
}
