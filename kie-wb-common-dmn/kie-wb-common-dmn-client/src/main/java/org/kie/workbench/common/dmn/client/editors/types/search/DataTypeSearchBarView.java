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

package org.kie.workbench.common.dmn.client.editors.types.search;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.NodeList;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSearchBarView_Search;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Templated
@ApplicationScoped
public class DataTypeSearchBarView implements DataTypeSearchBar.View {

    static final String RESULT_ENTRY_CSS_CLASS = "kie-search-engine-result";

    static final String ENABLED_SEARCH = "kie-search-engine-enabled";

    @DataField("search-bar")
    private final HTMLInputElement searchBar;

    @DataField("search-icon")
    private final HTMLElement searchIcon;

    @DataField("close-search")
    private final HTMLButtonElement closeSearch;

    private final TranslationService translationService;

    private DataTypeSearchBar presenter;

    @Inject
    public DataTypeSearchBarView(final HTMLInputElement searchBar,
                                 final @Named("span") HTMLElement searchIcon,
                                 final HTMLButtonElement closeSearch,
                                 final TranslationService translationService) {
        this.searchBar = searchBar;
        this.searchIcon = searchIcon;
        this.closeSearch = closeSearch;
        this.translationService = translationService;
    }

    @PostConstruct
    public void setupSearchBar() {
        searchBar.placeholder = translationService.format(DataTypeSearchBarView_Search);
    }

    @Override
    public void init(final DataTypeSearchBar presenter) {
        this.presenter = presenter;
    }

    @EventHandler("close-search")
    public void onSearchBarCloseButton(final ClickEvent e) {
        presenter.reset();
    }

    @EventHandler("search-bar")
    public void onSearchBarKeyUpEvent(final KeyUpEvent event) {
        if (isEscape(event)) {
            presenter.reset();
        } else {
            search();
        }
    }

    @EventHandler("search-bar")
    public void onSearchBarKeyDownEvent(final KeyDownEvent e) {
        refreshSearchBarState();
    }

    @EventHandler("search-bar")
    public void onSearchBarChangeEvent(final ChangeEvent e) {
        refreshSearchBarState();
    }

    @Override
    public void showSearchResults(final List<DataType> results) {
        enableSearch();
        disableResults();

        results.forEach(dataType -> {
            getResultEntry(dataType).ifPresent(e -> e.classList.add(RESULT_ENTRY_CSS_CLASS));
        });
    }

    @Override
    public void resetSearchBar() {
        searchBar.value = "";

        refreshSearchBarState();
        disableSearch();
        disableResults();
    }

    void enableSearch() {
        getResultsContainer().classList.add(ENABLED_SEARCH);
    }

    void disableSearch() {
        getResultsContainer().classList.remove(ENABLED_SEARCH);
    }

    void search() {

        final String currentValue = searchBar.value;

        setTimeout((type) -> {
            if (Objects.equals(searchBar.value, currentValue)) {
                presenter.search(currentValue);
            }
        }, 500d);
    }

    void disableResults() {

        final NodeList<Element> results = getResultsContainer().querySelectorAll("." + RESULT_ENTRY_CSS_CLASS);

        for (int i = 0; i < results.length; i++) {
            results.getAt(i).classList.remove(RESULT_ENTRY_CSS_CLASS);
        }
    }

    void refreshSearchBarState() {
        final boolean isActive = !isEmpty(searchBar.value);
        searchBarActive(isActive);
    }

    private void searchBarActive(final boolean isActive) {
        if (isActive) {
            hide(searchIcon);
            show(closeSearch);
        } else {
            show(searchIcon);
            hide(closeSearch);
        }
    }

    Optional<Element> getResultEntry(final DataType dataType) {
        final Element entry = getResultsContainer().querySelector("[" + UUID_ATTR + "=\"" + dataType.getUUID() + "\"]");
        return Optional.ofNullable(entry);
    }

    void setTimeout(final DomGlobal.SetTimeoutCallbackFn callback,
                    final double delay) {
        DomGlobal.setTimeout(callback, delay);
    }

    private boolean isEscape(final KeyUpEvent event) {
        return Objects.equals(event.getNativeKeyCode(), KeyCodes.KEY_ESCAPE);
    }

    private Element getResultsContainer() {
        return presenter.getResultsContainer();
    }
}
