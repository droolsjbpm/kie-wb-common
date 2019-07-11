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
package org.kie.workbench.common.dmn.showcase.client.editor;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.client.widgets.event.LoadDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramsNavigator;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = DMNDiagramsNavigatorScreen.SCREEN_ID)
public class DMNDiagramsNavigatorScreen {

    public static final String SCREEN_ID = "DMNDiagramsNavigatorScreen";

    private DiagramsNavigator diagramsNavigator;
    private ShapeSetsMenuItemsBuilder newDiagramMenuItemsBuilder;
    private PlaceManager placeManager;

    private Menus menu = null;
    private LoadDiagramEvent selectedDiagramEvent = null;

    public DMNDiagramsNavigatorScreen() {
        //CDI proxy
    }

    @Inject
    public DMNDiagramsNavigatorScreen(final DiagramsNavigator diagramsNavigator,
                                      final ShapeSetsMenuItemsBuilder newDiagramMenuItemsBuilder,
                                      final PlaceManager placeManager) {
        this.diagramsNavigator = diagramsNavigator;
        this.newDiagramMenuItemsBuilder = newDiagramMenuItemsBuilder;
        this.placeManager = placeManager;
    }

    @PostConstruct
    public void init() {
        this.selectedDiagramEvent = null;
    }

    @OnStartup
    @SuppressWarnings("unused")
    public void onStartup(final PlaceRequest placeRequest) {
        this.menu = makeMenuBar();
        clear();
    }

    private Menus makeMenuBar() {
        final MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> m =
                MenuFactory
                        .newTopLevelMenu("Load diagrams from server")
                        .respondsWith(() -> diagramsNavigator.show())
                        .endMenu()
                        .newTopLevelMenu("Edit")
                        .respondsWith(this::edit)
                        .endMenu();
        m.newTopLevelMenu(newDiagramMenuItemsBuilder.build("Create",
                                                           "Create a new",
                                                           this::create)).endMenu();
        return m.build();
    }

    private void edit() {
        if (Objects.nonNull(selectedDiagramEvent)) {
            final Maps.Builder<String, String> builder = new Maps.Builder<>();
            builder.put("name", selectedDiagramEvent.getName());
            launchEditor(builder.build());
        }
    }

    private void create(final ShapeSet shapeSet) {
        final String shapeSetName = shapeSet.getDescription();
        final String defSetId = shapeSet.getDefinitionSetId();
        final Maps.Builder<String, String> builder = new Maps.Builder<>();
        builder.put("defSetId", defSetId);
        builder.put("shapeSetId", shapeSet.getId());
        builder.put("title", "New " + shapeSetName + " diagram");
        launchEditor(builder.build());
    }

    private void launchEditor(final Map<String, String> params) {
        final PlaceRequest diagramScreenPlaceRequest = new DefaultPlaceRequest(DMNDiagramEditor.EDITOR_ID, params);
        placeManager.goTo(diagramScreenPlaceRequest);
    }

    private void clear() {
        diagramsNavigator.clear();
        selectedDiagramEvent = null;
    }

    @OnClose
    public void onClose() {
        clear();
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(menu);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Diagrams Navigator";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return diagramsNavigator.asWidget();
    }

    private void onLoadDiagramEvent(final @Observes LoadDiagramEvent loadDiagramEvent) {
        this.selectedDiagramEvent = loadDiagramEvent;
    }
}