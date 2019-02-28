/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style.HasCssName;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.common.cards.CardComponent;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;
import org.uberfire.client.mvp.UberElemental;

import static org.gwtbootstrap3.client.ui.constants.IconType.DOWNLOAD;

public class DMNCardComponent implements CardComponent {

    private final ContentView contentView;

    private DMNCardsGridComponent grid;

    private IncludedModel includedModel;

    @Inject
    public DMNCardComponent(final ContentView contentView) {
        this.contentView = contentView;
    }

    @PostConstruct
    public void init() {
        contentView.init(this);
    }

    public void setup(final DMNCardsGridComponent grid,
                      final IncludedModel includedModel) {
        this.grid = grid;
        this.includedModel = includedModel;
        refreshView();
    }

    void refreshView() {
        contentView.setPath(getTruncatedPath());
        contentView.setDataTypesCount(getDataTypesCount());
        contentView.setDrgElementsCount(getDrgElementsCount());
    }

    @Override
    public HasCssName getIcon() {
        return DOWNLOAD;
    }

    @Override
    public String getTitle() {
        return getIncludedModel().getName();
    }

    @Override
    public String getUUID() {
        return getIncludedModel().getUUID();
    }

    @Override
    public HTMLElement getContent() {
        return contentView.getElement();
    }

    @Override
    public boolean onTitleChanged(final String newName) {

        final String oldName = getIncludedModel().getName();

        getIncludedModel().setName(newName);

        if (getIncludedModel().isValid()) {
            getIncludedModel().update();
            getGrid().refresh();
            return true;
        } else {
            getIncludedModel().setName(oldName);
            return false;
        }
    }

    private String getTruncatedPath() {
        final String path = getIncludedModel().getPath();
        return Optional.ofNullable(path).map(p -> truncate(path, 60)).orElse("");
    }

    String truncate(final String value,
                    final int limit) {

        if (value.length() > limit) {
            return "..." + value.substring(value.length() - limit);
        }

        return value;
    }

    private Integer getDataTypesCount() {
        return getIncludedModel().getDataTypesCount();
    }

    private Integer getDrgElementsCount() {
        return getIncludedModel().getDrgElementsCount();
    }

    public void remove() {
        getIncludedModel().destroy();
        getGrid().refresh();
    }

    IncludedModel getIncludedModel() {
        return includedModel;
    }

    DMNCardsGridComponent getGrid() {
        return grid;
    }

    public interface ContentView extends UberElemental<DMNCardComponent>,
                                         IsElement {

        void setPath(final String path);

        void setDataTypesCount(final Integer dataTypesCount);

        void setDrgElementsCount(final Integer drgElementsCount);
    }
}
