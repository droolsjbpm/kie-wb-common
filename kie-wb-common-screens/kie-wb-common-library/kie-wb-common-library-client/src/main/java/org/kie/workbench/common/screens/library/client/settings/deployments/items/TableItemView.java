/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.settings.deployments.items;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("#root")
public class TableItemView implements TableItemPresenter.View,
                                      IsElement {

    @Inject
    @Named("span")
    @DataField("name")
    private HTMLElement name;

    @Inject
    @DataField("resolvers")
    private HTMLDivElement resolversContainer;

    @Inject
    @Named("span")
    @DataField("parameters-count")
    private HTMLElement parametersCount;

    @Inject
    @DataField("remove-button")
    private HTMLAnchorElement removeButton;

    private TableItemPresenter presenter;

    @Override
    public void init(final TableItemPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("remove-button")
    private void onRemoveButtonClicked(final ClickEvent ignore) {
        presenter.remove();
    }

    public void setName(final String name) {
        this.name.textContent = name;
    }

    public void setParametersCount(final int parametersCount) {
        this.parametersCount.textContent = Integer.toString(parametersCount);
    }

    @Override
    public Element getResolversContainer() {
        return resolversContainer;
    }
}
