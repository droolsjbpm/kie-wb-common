/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.util;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.client.wizard.ExamplesWizard;
import org.kie.workbench.common.screens.library.api.LibraryPreferences;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.widgets.LibraryToolbarPresenter;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LibraryPlacesTest {

    @Mock
    private UberfireBreadcrumbs breadcrumbs;

    @Mock
    private TranslationService ts;

    @Mock
    private Event<ProjectDetailEvent> projectDetailEvent;

    @Mock
    private Event<AssetDetailEvent> assetDetailEvent;

    @Mock
    private ResourceUtils resourceUtils;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private LibraryPerspective libraryPerspective;

    @Mock
    private ProjectContext projectContext;

    @Mock
    private LibraryToolbarPresenter libraryToolbar;

    @Mock
    private AuthoringWorkbenchDocks docks;

    private LibraryPreferences libraryPreferences;

    @Mock
    private Event<ProjectContextChangeEvent> projectContextChangeEvent;

    @Mock
    private ExamplesUtils examplesUtils;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private ManagedInstance<ExamplesWizard> examplesWizards;

    private LibraryPlaces libraryPlaces;

    private OrganizationalUnit activeOrganizationalUnit;
    private Repository activeRepository;
    private String activeBranch;
    private Project activeProject;

    private boolean isProjectExplorerExpanded = false;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);
        libraryPreferences = new LibraryPreferences() {
            @Override
            public void load(final ParameterizedCommand<LibraryPreferences> successCallback,
                             final ParameterizedCommand<Throwable> errorCallback) {
                final LibraryPreferences loadedLibraryPreferences = mock(LibraryPreferences.class);
                doReturn(isProjectExplorerExpanded).when(loadedLibraryPreferences).isProjectExplorerExpanded();
                successCallback.execute(loadedLibraryPreferences);
            }
        };

        libraryPlaces = spy(new LibraryPlaces(breadcrumbs,
                                              ts,
                                              projectDetailEvent,
                                              assetDetailEvent,
                                              resourceUtils,
                                              libraryServiceCaller,
                                              placeManager,
                                              libraryPerspective,
                                              projectContext,
                                              libraryToolbar,
                                              docks,
                                              libraryPreferences,
                                              projectContextChangeEvent,
                                              examplesUtils,
                                              notificationEvent,
                                              examplesWizards));

        activeOrganizationalUnit = mock(OrganizationalUnit.class);
        activeRepository = mock(Repository.class);
        activeBranch = "master";
        activeProject = mock(Project.class);

        doReturn(activeOrganizationalUnit).when(projectContext).getActiveOrganizationalUnit();
        doReturn(activeRepository).when(projectContext).getActiveRepository();
        doReturn(activeBranch).when(projectContext).getActiveBranch();
        doReturn(activeProject).when(projectContext).getActiveProject();

        doNothing().when(libraryPlaces).setupToolBar();
        doNothing().when(libraryPlaces).setupLibraryBreadCrumbs();
        doNothing().when(libraryPlaces).setupLibraryBreadCrumbsForNewProject();
        doNothing().when(libraryPlaces).setupLibraryBreadCrumbsForProject(any(ProjectInfo.class));
        doNothing().when(libraryPlaces).setupLibraryBreadCrumbsForAsset(any(ProjectInfo.class),
                                                                        any(Path.class));
    }

    @Test
    public void onSelectPlaceOutsideLibraryTest() {
        doReturn(PlaceStatus.CLOSE).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        final PlaceGainFocusEvent placeGainFocusEvent = mock(PlaceGainFocusEvent.class);
        libraryPlaces.onSelectPlaceEvent(placeGainFocusEvent);

        verify(placeGainFocusEvent,
               never()).getPlace();
    }

    @Test
    public void onSelectAssetTest() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        final ObservablePath path = mock(ObservablePath.class);
        final PathPlaceRequest pathPlaceRequest = mock(PathPlaceRequest.class);
        doReturn(path).when(pathPlaceRequest).getPath();
        final PlaceGainFocusEvent placeGainFocusEvent = mock(PlaceGainFocusEvent.class);
        doReturn(pathPlaceRequest).when(placeGainFocusEvent).getPlace();

        libraryPlaces.onSelectPlaceEvent(placeGainFocusEvent);

        verify(libraryPlaces).setupLibraryBreadCrumbsForAsset(libraryPlaces.getProjectInfo(),
                                                              path);
        verify(libraryPlaces).showDocks();
    }

    @Test
    public void onSelectProjectSettingsTest() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        final DefaultPlaceRequest projectSettingsPlaceRequest = new DefaultPlaceRequest(LibraryPlaces.PROJECT_SETTINGS);
        final PlaceGainFocusEvent placeGainFocusEvent = mock(PlaceGainFocusEvent.class);
        doReturn(projectSettingsPlaceRequest).when(placeGainFocusEvent).getPlace();

        libraryPlaces.onSelectPlaceEvent(placeGainFocusEvent);

        verify(libraryPlaces).hideDocks();
        verify(libraryPlaces).setupLibraryBreadCrumbsForAsset(libraryPlaces.getProjectInfo(),
                                                              null);
    }

    @Test
    public void onSelectProjectTest() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        final DefaultPlaceRequest projectSettingsPlaceRequest = new DefaultPlaceRequest(LibraryPlaces.PROJECT_SCREEN);
        final PlaceGainFocusEvent placeGainFocusEvent = mock(PlaceGainFocusEvent.class);
        doReturn(projectSettingsPlaceRequest).when(placeGainFocusEvent).getPlace();

        libraryPlaces.onSelectPlaceEvent(placeGainFocusEvent);

        verify(libraryPlaces).hideDocks();
        verify(libraryPlaces).setupLibraryBreadCrumbsForProject(libraryPlaces.getProjectInfo());
    }

    @Test
    public void hideDocksTest() {
        libraryPlaces.showDocks();

        reset(docks);

        libraryPlaces.hideDocks();
        libraryPlaces.hideDocks();

        verify(docks,
               times(1)).hide();
        verify(docks,
               never()).setup(anyString(),
                              any(PlaceRequest.class));
        verify(docks,
               never()).show();
        verify(docks,
               never()).expandProjectExplorer();
    }

    @Test
    public void showDocksWithProjectExplorerExpandedDocksTest() {
        isProjectExplorerExpanded = true;

        libraryPlaces.showDocks();
        libraryPlaces.showDocks();

        verify(docks,
               times(1)).setup(LibraryPlaces.LIBRARY_PERSPECTIVE,
                               new DefaultPlaceRequest(LibraryPlaces.PROJECT_EXPLORER));
        verify(docks,
               times(1)).show();
        verify(docks,
               times(1)).expandProjectExplorer();
        verify(docks,
               never()).hide();
    }

    @Test
    public void showDocksWithProjectExplorerNotExpandedTest() {
        isProjectExplorerExpanded = false;

        libraryPlaces.showDocks();
        libraryPlaces.showDocks();

        verify(docks,
               times(1)).setup(LibraryPlaces.LIBRARY_PERSPECTIVE,
                               new DefaultPlaceRequest(LibraryPlaces.PROJECT_EXPLORER));
        verify(docks,
               times(1)).show();
        verify(docks,
               never()).expandProjectExplorer();
        verify(docks,
               never()).hide();
    }

    @Test
    public void projectContextUnchanged() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        doReturn(activeOrganizationalUnit).when(libraryToolbar).getSelectedOrganizationalUnit();
        doReturn(activeRepository).when(libraryToolbar).getSelectedRepository();
        doReturn(activeBranch).when(libraryToolbar).getSelectedBranch();

        doReturn(activeOrganizationalUnit).when(projectContext).getActiveOrganizationalUnit();
        doReturn(activeRepository).when(projectContext).getActiveRepository();
        doReturn(activeBranch).when(projectContext).getActiveBranch();
        doReturn(activeProject).when(projectContext).getActiveProject();

        libraryPlaces.goToProject(new ProjectInfo(activeOrganizationalUnit,
                                                  activeRepository,
                                                  activeBranch,
                                                  activeProject),
                                  false);
        Mockito.reset(libraryPlaces);
        libraryPlaces.projectContextChange();

        verify(libraryToolbar,
               never()).setSelectedInfo(any(),
                                        any(),
                                        any());
        verify(libraryPlaces,
               never()).goToProject(any(),
                                    anyBoolean());
    }

    @Test
    public void projectContextChanged_OrganizationalUnit() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        doReturn(activeOrganizationalUnit).when(libraryToolbar).getSelectedOrganizationalUnit();
        doReturn(activeRepository).when(libraryToolbar).getSelectedRepository();
        doReturn(activeBranch).when(libraryToolbar).getSelectedBranch();

        final OrganizationalUnit newOrganizationalUnit = mock(OrganizationalUnit.class);

        doReturn(newOrganizationalUnit).when(projectContext).getActiveOrganizationalUnit();
        doReturn(activeRepository).when(projectContext).getActiveRepository();
        doReturn(activeBranch).when(projectContext).getActiveBranch();
        doReturn(activeProject).when(projectContext).getActiveProject();

        libraryPlaces.projectContextChange();

        verify(libraryToolbar).setSelectedInfo(eq(newOrganizationalUnit),
                                               eq(activeRepository),
                                               any());
        verify(libraryPlaces,
               never()).goToProject(any(),
                                    anyBoolean());
    }

    @Test
    public void projectContextChanged_Project() {
        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);

        doReturn(activeOrganizationalUnit).when(libraryToolbar).getSelectedOrganizationalUnit();
        doReturn(activeRepository).when(libraryToolbar).getSelectedRepository();
        doReturn(activeBranch).when(libraryToolbar).getSelectedBranch();

        final Project newProject = mock(Project.class);

        doReturn(activeOrganizationalUnit).when(projectContext).getActiveOrganizationalUnit();
        doReturn(activeRepository).when(projectContext).getActiveRepository();
        doReturn(activeBranch).when(projectContext).getActiveBranch();
        doReturn(newProject).when(projectContext).getActiveProject();

        libraryPlaces.goToProject(new ProjectInfo(activeOrganizationalUnit,
                                                  activeRepository,
                                                  activeBranch,
                                                  activeProject),
                                  false);
        Mockito.reset(libraryPlaces);
        libraryPlaces.projectContextChange();

        verify(libraryToolbar,
               never()).setSelectedInfo(any(),
                                        any(),
                                        any());
        verify(libraryPlaces).goToProject(new ProjectInfo(activeOrganizationalUnit,
                                                          activeRepository,
                                                          activeBranch,
                                                          newProject),
                                          false);
    }

    @Test
    public void goToAssetTest() {
        final ObservablePath path = mock(ObservablePath.class);
        final PathPlaceRequest pathPlaceRequest = mock(PathPlaceRequest.class);
        doReturn(path).when(pathPlaceRequest).getPath();
        doReturn(pathPlaceRequest).when(libraryPlaces).createPathPlaceRequest(any(Path.class));

        libraryPlaces.goToAsset(libraryPlaces.getProjectInfo(),
                                path);

        verify(placeManager).goTo(pathPlaceRequest);
    }

    @Test
    public void goToProjectSettingsTest() {
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.PROJECT_SETTINGS);

        libraryPlaces.goToAsset(libraryPlaces.getProjectInfo(),
                                null);

        verify(placeManager).goTo(placeRequest);
    }

    @Test
    public void goToLibraryTest() {
        final PlaceRequest placeRequest = new ConditionalPlaceRequest(LibraryPlaces.LIBRARY_SCREEN)
                .when(p -> false)
                .orElse(new DefaultPlaceRequest(LibraryPlaces.EMPTY_LIBRARY_SCREEN));
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);

        libraryPlaces.goToLibrary();

        verify(libraryPlaces).closeLibraryPlaces();
        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(libraryPlaces).setupLibraryBreadCrumbs();
        verify(projectContextChangeEvent).fire(any(ProjectContextChangeEvent.class));
    }

    @Test
    public void goToNewProjectTest() {
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.NEW_PROJECT_SCREEN);
        final PartDefinitionImpl part = new PartDefinitionImpl(placeRequest);
        part.setSelectable(false);

        libraryPlaces.goToNewProject();

        verify(libraryPlaces).closeLibraryPlaces();
        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(libraryPlaces).setupLibraryBreadCrumbsForNewProject();
    }

    @Test
    public void goToProjectTest() {
        final PlaceRequest projectScreen = new ConditionalPlaceRequest(LibraryPlaces.PROJECT_SCREEN)
                .when(p -> false)
                .orElse(new DefaultPlaceRequest(LibraryPlaces.EMPTY_PROJECT_SCREEN));
        final PartDefinitionImpl part = new PartDefinitionImpl(projectScreen);
        part.setSelectable(false);
        final ProjectInfo projectInfo = mock(ProjectInfo.class);

        libraryPlaces.goToProject(projectInfo);

        verify(libraryPlaces).closeLibraryPlaces();
        verify(placeManager).goTo(eq(part),
                                  any(PanelDefinition.class));
        verify(projectDetailEvent).fire(any(ProjectDetailEvent.class));
        verify(projectContextChangeEvent).fire(any(ProjectContextChangeEvent.class));
        verify(libraryPlaces).setupLibraryBreadCrumbsForProject(projectInfo);
    }

    @Test
    public void goToImportProjectWizardTest() {
        final ExamplesWizard examplesWizard = mock(ExamplesWizard.class);
        doReturn(examplesWizard).when(examplesWizards).get();

        libraryPlaces.goToImportProjectWizard();

        verify(examplesWizard).start();
        verify(examplesWizard).setDefaultTargetOrganizationalUnit(anyString());
        verify(examplesWizard).setDefaultTargetRepository(anyString());
    }

    @Test
    public void projectDeletedRedirectsToLibraryWhenItIsOpenedTest() {
        final Project activeProject = mock(Project.class);
        final DeleteProjectEvent deleteProjectEvent = mock(DeleteProjectEvent.class);

        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);
        doReturn(activeProject).when(projectContext).getActiveProject();
        doReturn(activeProject).when(deleteProjectEvent).getProject();

        libraryPlaces.projectDeleted(deleteProjectEvent);

        verify(libraryPlaces).goToLibrary();
        verify(notificationEvent).fire(any());
    }

    @Test
    public void projectDeletedDoesNotRedirectToLibraryWhenItIsNotOpenedTest() {
        final Project activeProject = mock(Project.class);
        final Project deletedProject = mock(Project.class);
        final DeleteProjectEvent deleteProjectEvent = mock(DeleteProjectEvent.class);

        doReturn(PlaceStatus.OPEN).when(placeManager).getStatus(LibraryPlaces.LIBRARY_PERSPECTIVE);
        doReturn(activeProject).when(projectContext).getActiveProject();
        doReturn(deletedProject).when(deleteProjectEvent).getProject();

        libraryPlaces.projectDeleted(deleteProjectEvent);

        verify(libraryPlaces,
               never()).goToLibrary();
        verify(notificationEvent,
               never()).fire(any());
    }
}
