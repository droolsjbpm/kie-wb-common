/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.categories;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.MouseDownEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;

@Templated
@Dependent
public class DefinitionPaletteCategoryWidgetViewImpl implements DefinitionPaletteCategoryWidgetView,
                                                                IsElement {

    @Inject
    private Document document;

    @Inject
    @DataField
    private Anchor categoryIcon;

    @Inject
    @DataField
    private Div floatingPanel;

    @Inject
    @DataField
    private Span header;

    private Presenter presenter;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void render(IconRenderer renderer) {
        DefinitionPaletteCategory category = presenter.getCategory();

        categoryIcon.setTitle(category.getTitle());
        header.setTextContent(category.getTitle());

        categoryIcon.appendChild(renderer.getElement());
    }

    @Override
    public void addItem(DefinitionPaletteItemWidget item) {
        floatingPanel.appendChild(item.getElement());
    }

    @Override
    public void addGroup(DefinitionPaletteGroupWidget groupWidget) {
        floatingPanel.appendChild(groupWidget.getElement());
    }

    @EventHandler("categoryIcon")
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        presenter.onMouseDown(mouseDownEvent.getClientX(),
                              mouseDownEvent.getClientY(),
                              mouseDownEvent.getX(),
                              mouseDownEvent.getY());
    }
}
