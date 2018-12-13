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

package org.kie.workbench.common.stunner.client.widgets.canvas;

import com.ait.lienzo.client.core.shape.Layer;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;

public abstract class DelegateLienzoPanel<P extends LienzoPanel> implements LienzoPanel {

    protected abstract P getDelegate();

    @Override
    public LienzoPanel show(final LienzoLayer layer) {
        getDelegate().show(layer);
        return this;
    }

    @Override
    public LienzoPanel show(final LienzoLayer layer,
                            final int width,
                            final int height) {
        getDelegate().show(layer,
                           width,
                           height);
        return this;
    }

    @Override
    public LienzoPanel setPixelSize(final int wide,
                                    final int high) {
        getDelegate().setPixelSize(wide,
                                   high);
        return this;
    }

    @Override
    public LienzoPanel focus() {
        getDelegate().focus();
        return this;
    }

    @Override
    public int getWidth() {
        return getDelegate().getWidth();
    }

    @Override
    public int getHeight() {
        return getDelegate().getHeight();
    }

    @Override
    public void setBackgroundLayer(final Layer layer) {
        getDelegate().setBackgroundLayer(layer);
    }

    @Override
    public void destroy() {
        getDelegate().destroy();
    }

    @Override
    public Widget asWidget() {
        return getDelegate().asWidget();
    }
}
