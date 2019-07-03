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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.tab;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.changerequest.ChangeRequestListUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.spaces.Space;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChangeRequestListPresenterTest {

    private ChangeRequestListPresenter presenter;

    @Mock
    private ChangeRequestListPresenter.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private EmptyChangeRequestListPresenter emptyChangeRequestListPresenter;

    @Mock
    private PopulatedChangeRequestListPresenter populatedChangeRequestListPresenter;

    @Mock
    private TranslationService ts;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private ChangeRequestService changeRequestService;

    @Mock
    private WorkspaceProject workspaceProject;

    @Before
    public void setUp() {
        doReturn(mock(KieModule.class)).when(workspaceProject).getMainModule();
        doReturn(workspaceProject).when(libraryPlaces).getActiveWorkspace();

        doReturn(mock(Space.class)).when(workspaceProject).getSpace();

        Repository repository = mock(Repository.class);
        doReturn(repository).when(workspaceProject).getRepository();
        doReturn("myRepository").when(repository).getIdentifier();

        EmptyChangeRequestListView emptyView = mock(EmptyChangeRequestListView.class);
        PopulatedChangeRequestListView populatedView = mock(PopulatedChangeRequestListView.class);

        HTMLElement emptyElement = mock(HTMLElement.class);
        HTMLElement populatedElement = mock(HTMLElement.class);

        doReturn(emptyView).when(emptyChangeRequestListPresenter).getView();
        doReturn(emptyElement).when(emptyView).getElement();

        doReturn("LoadingChangeRequests").when(ts).getTranslation(LibraryConstants.LoadingChangeRequests);

        doReturn(populatedView).when(populatedChangeRequestListPresenter).getView();
        doReturn(populatedElement).when(populatedView).getElement();

        this.presenter = spy(new ChangeRequestListPresenter(view,
                                                            libraryPlaces,
                                                            emptyChangeRequestListPresenter,
                                                            populatedChangeRequestListPresenter,
                                                            ts,
                                                            busyIndicatorView,
                                                            new CallerMock<>(changeRequestService)));
    }

    @Test
    public void showEmptyListTest() {
        doReturn(0).when(changeRequestService).countChangeRequests(anyString(), anyString());

        presenter.postConstruct();

        verify(view).init(presenter);
        verify(busyIndicatorView, times(1))
                .showBusyIndicator(LibraryConstants.LoadingChangeRequests);
        verify(busyIndicatorView, times(1))
                .hideBusyIndicator();

        verify(emptyChangeRequestListPresenter,
               times(1)).getView();
        verify(populatedChangeRequestListPresenter,
               never()).getView();
        verify(view).setContent(emptyChangeRequestListPresenter.getView().getElement());
    }

    @Test
    public void showPopulatedListTest() {
        doReturn(10).when(changeRequestService).countChangeRequests(anyString(), anyString());

        presenter.postConstruct();

        verify(view).init(presenter);
        verify(busyIndicatorView, times(1))
                .showBusyIndicator(LibraryConstants.LoadingChangeRequests);
        verify(busyIndicatorView, times(1))
                .hideBusyIndicator();

        verify(emptyChangeRequestListPresenter,
               never()).getView();
        verify(populatedChangeRequestListPresenter,
               times(1)).getView();
        verify(view).setContent(populatedChangeRequestListPresenter.getView().getElement());
    }

    @Test
    public void refreshWhenEmptyOnListUpdatedTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestListPresenter.class.getDeclaredField("empty"))
                .set(true);
        new FieldSetter(presenter,
                        ChangeRequestListPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        presenter.onChangeRequestListUpdated(new ChangeRequestListUpdatedEvent("myRepository"));

        verify(changeRequestService, times(1)).countChangeRequests(anyString(), anyString());
    }

    @Test
    public void doNothingWhenNotEmptyOnListUpdatedTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestListPresenter.class.getDeclaredField("empty"))
                .set(false);
        new FieldSetter(presenter,
                        ChangeRequestListPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        presenter.onChangeRequestListUpdated(new ChangeRequestListUpdatedEvent("myRepository"));

        verify(changeRequestService, never()).countChangeRequests(anyString(), anyString());
    }

    @Test
    public void doNothingWhenDifferentRepositoryOnListUpdatedTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestListPresenter.class.getDeclaredField("empty"))
                .set(false);
        new FieldSetter(presenter,
                        ChangeRequestListPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        presenter.onChangeRequestListUpdated(new ChangeRequestListUpdatedEvent("otherRepository"));

        verify(changeRequestService, never()).countChangeRequests(anyString(), anyString());
    }
}