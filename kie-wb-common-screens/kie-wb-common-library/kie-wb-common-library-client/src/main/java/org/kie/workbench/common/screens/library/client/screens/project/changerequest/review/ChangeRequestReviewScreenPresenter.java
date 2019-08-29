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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatusUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.NothingToMergeException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.RepositoryFileListUpdated;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.changedfiles.ChangedFilesScreenPresenter;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.overview.OverviewScreenPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.CHANGE_REQUEST_REVIEW,
        owningPerspective = LibraryPerspective.class)
public class ChangeRequestReviewScreenPresenter {

    private final View view;
    private final TranslationService ts;
    private final LibraryPlaces libraryPlaces;
    private final Caller<ChangeRequestService> changeRequestService;
    private final Caller<RepositoryService> repositoryService;
    private final BusyIndicatorView busyIndicatorView;
    private final OverviewScreenPresenter overviewScreen;
    private final ChangedFilesScreenPresenter changedFilesScreen;
    private final Promises promises;
    private final ProjectController projectController;
    private final Event<NotificationEvent> notificationEvent;
    private final SessionInfo sessionInfo;
    private WorkspaceProject workspaceProject;
    private long currentChangeRequestId;
    private Branch currentSourceBranch;
    private Branch currentTargetBranch;
    private boolean overviewTabLoaded;
    private boolean changedFilesTabLoaded;
    private Repository repository;

    @Inject
    public ChangeRequestReviewScreenPresenter(final View view,
                                              final TranslationService ts,
                                              final LibraryPlaces libraryPlaces,
                                              final Caller<ChangeRequestService> changeRequestService,
                                              final Caller<RepositoryService> repositoryService,
                                              final BusyIndicatorView busyIndicatorView,
                                              final OverviewScreenPresenter overviewScreen,
                                              final ChangedFilesScreenPresenter changedFilesScreen,
                                              final Promises promises,
                                              final ProjectController projectController,
                                              final Event<NotificationEvent> notificationEvent,
                                              final SessionInfo sessionInfo) {
        this.view = view;
        this.ts = ts;
        this.libraryPlaces = libraryPlaces;
        this.changeRequestService = changeRequestService;
        this.repositoryService = repositoryService;
        this.busyIndicatorView = busyIndicatorView;
        this.overviewScreen = overviewScreen;
        this.changedFilesScreen = changedFilesScreen;
        this.promises = promises;
        this.projectController = projectController;
        this.notificationEvent = notificationEvent;
        this.sessionInfo = sessionInfo;
    }

    @PostConstruct
    public void postConstruct() {
        this.workspaceProject = libraryPlaces.getActiveWorkspace();
        this.repository = workspaceProject.getRepository();

        this.view.init(this);
        this.view.setTitle(this.getTitle());
    }

    public void refreshOnFocus(@Observes final SelectPlaceEvent selectPlaceEvent) {
        if (workspaceProject != null && workspaceProject.getMainModule() != null) {
            final PlaceRequest place = selectPlaceEvent.getPlace();

            if (place.getIdentifier().equals(LibraryPlaces.CHANGE_REQUEST_REVIEW)) {
                final String changeRequestIdValue = place.getParameter(ChangeRequestUtils.CHANGE_REQUEST_ID_KEY, null);

                if (changeRequestIdValue != null && !changeRequestIdValue.equals("")) {
                    this.currentChangeRequestId = Long.parseLong(changeRequestIdValue);
                    this.loadContent();
                }
            }
        }
    }

    public void onChangeRequestUpdated(@Observes final ChangeRequestUpdatedEvent event) {
        if (event.getRepositoryId().equals(repository.getIdentifier())
                && event.getChangeRequestId() == currentChangeRequestId) {
            this.refreshContent(true,
                                false);
            this.notifyOtherUsers(event.getUserId());
        }
    }

    public void onChangeRequestStatusUpdated(@Observes final ChangeRequestStatusUpdatedEvent event) {
        if (event.getRepositoryId().equals(repository.getIdentifier())
                && event.getChangeRequestId() == currentChangeRequestId) {
            this.refreshContent(true,
                                false);
            this.notifyOtherUsers(event.getUserId());
        }
    }

    public void onRepositoryFileListUpdated(@Observes final RepositoryFileListUpdated event) {
        if (event.getRepositoryId().equals(repository.getIdentifier())) {
            final String updatedBranch = event.getBranchName();

            if (currentSourceBranch.getName().equals(updatedBranch) ||
                    currentTargetBranch.getName().equals(updatedBranch)) {
                this.refreshContent(false,
                                    true);
            }
        }
    }

    public void onRepositoryUpdated(@Observes final RepositoryUpdatedEvent event) {
        if (event.getRepository().getIdentifier().equals(repository.getIdentifier())) {
            this.repository = event.getRepository();

            if (!this.repository.getBranches().contains(currentSourceBranch) ||
                    !this.repository.getBranches().contains(currentTargetBranch)) {
                this.goBackToProject();
            }
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation(LibraryConstants.ChangeRequest);
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    public void showOverviewContent() {
        this.view.setContent(this.overviewScreen.getView().getElement());
    }

    public void showChangedFilesContent() {
        this.view.setContent(this.changedFilesScreen.getView().getElement());
    }

    public void cancel() {
        this.goBackToProject();
    }

    public void reject() {
        this.doActionIfAllowed(this::rejectChangeRequestAction);
    }

    public void accept() {
        this.doActionIfAllowed(this::acceptChangeRequestAction);
    }

    public void revert() {
        this.doActionIfAllowed(this::revertChangeRequestAction);
    }

    private void notifyOtherUsers(final String userWhoMadeUpdates) {
        if (!sessionInfo.getIdentity().getIdentifier().equals(userWhoMadeUpdates)) {
            fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestUpdatedMessage,
                                            currentChangeRequestId,
                                            userWhoMadeUpdates),
                                  NotificationEvent.NotificationType.INFO);
        }
    }

    private void goBackToProject() {
        this.reset();
        this.libraryPlaces.closeChangeRequestReviewScreen();
        this.libraryPlaces.goToProject(workspaceProject);
    }

    private void doActionIfAllowed(final Runnable action) {
        projectController.canUpdateBranch(workspaceProject,
                                          this.currentTargetBranch).then(userCanUpdateBranch -> {
            if (userCanUpdateBranch) {
                action.run();
            }

            return promises.resolve();
        });
    }

    private void loadContent() {
        this.reset();

        this.view.setTitle(ts.format(LibraryConstants.ChangeRequestAndId, currentChangeRequestId));

        this.busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        this.repositoryService.call((Repository updatedRepository) -> {
            this.repository = updatedRepository;

            this.setup(loadChangeRequestCallback());
        }).getRepositoryFromSpace(this.workspaceProject.getSpace(),
                                  this.repository.getAlias());
    }

    private void refreshContent(final boolean refreshOverview,
                                final boolean refreshChangedFiles) {
        this.busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        this.setup(reloadChangeRequestCallback(refreshOverview,
                                               refreshChangedFiles));
    }

    private void reset() {
        this.overviewTabLoaded = false;
        this.changedFilesTabLoaded = false;

        this.overviewScreen.reset();
        this.changedFilesScreen.reset();

        this.view.resetAll();

        this.view.activateOverviewTab();
    }

    private void setup(final RemoteCallback<ChangeRequest> getChangeRequestCallback) {
        changeRequestService.call(getChangeRequestCallback,
                                  new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                .getChangeRequest(workspaceProject.getSpace().getName(),
                                  repository.getAlias(),
                                  currentChangeRequestId);
    }

    private RemoteCallback<ChangeRequest> loadChangeRequestCallback() {
        return (final ChangeRequest changeRequest) -> {
            this.resolveInvolvedBranches(changeRequest);

            this.setupOverviewScreen(changeRequest);
            this.setupChangedFilesScreen(changeRequest);
        };
    }

    private void resolveInvolvedBranches(final ChangeRequest changeRequest) {
        this.currentSourceBranch = repository.getBranch(changeRequest.getSourceBranch())
                .orElseThrow(() -> new IllegalArgumentException(
                        "The branch " + changeRequest.getSourceBranch() + " does not exist."));

        this.currentTargetBranch = repository.getBranch(changeRequest.getTargetBranch())
                .orElseThrow(() -> new IllegalStateException(
                        "The branch " + changeRequest.getTargetBranch() + " does not exist."));
    }

    private void setupOverviewScreen(final ChangeRequest changeRequest) {
        this.overviewScreen.setup(changeRequest,
                                  (final Boolean success) -> {
                                      overviewTabLoaded = true;
                                      finishLoading(changeRequest);
                                  });
    }

    private void setupChangedFilesScreen(final ChangeRequest changeRequest) {
        this.changedFilesScreen.setup(changeRequest,
                                      (final Boolean success) -> {
                                          changedFilesTabLoaded = true;
                                          finishLoading(changeRequest);
                                      }, this.view::setChangedFilesCount);
    }

    private RemoteCallback<ChangeRequest> reloadChangeRequestCallback(final boolean refreshOverview,
                                                                      final boolean refreshChangedFiles) {
        return (final ChangeRequest changeRequest) -> {
            if (refreshOverview) {
                overviewTabLoaded = false;
                this.setupOverviewScreen(changeRequest);
            } else {
                overviewTabLoaded = true;
            }

            if (changeRequest.getStatus() == ChangeRequestStatus.OPEN && refreshChangedFiles) {
                changedFilesTabLoaded = false;
                this.setupChangedFilesScreen(changeRequest);
            } else {
                changedFilesTabLoaded = true;
            }
        };
    }

    private void setupActionButtons(final ChangeRequest changeRequest,
                                    final Branch targetBranch) {
        projectController.canUpdateBranch(workspaceProject, targetBranch).then(userCanUpdateBranch -> {
            this.view.resetButtonState();

            if (userCanUpdateBranch) {
                if (changeRequest.getStatus() == ChangeRequestStatus.ACCEPTED) {
                    this.view.showRevertButton(true);
                } else if (changeRequest.getStatus() == ChangeRequestStatus.OPEN) {
                    final boolean canBeAccepted = !changeRequest.isConflict() &&
                            changeRequest.getChangedFilesCount() > 0;

                    this.view.showRejectButton(true);
                    this.view.showAcceptButton(true);
                    this.view.enableAcceptButton(canBeAccepted);
                }
            }
            return promises.resolve();
        });
    }

    private void rejectChangeRequestAction() {
        this.changeRequestService.call(v -> {
            fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestRejectMessage,
                                            currentChangeRequestId),
                                  NotificationEvent.NotificationType.SUCCESS);
        }).rejectChangeRequest(workspaceProject.getSpace().getName(),
                               repository.getAlias(),
                               currentChangeRequestId);
    }

    private void acceptChangeRequestAction() {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        this.changeRequestService.call((final Boolean succeeded) -> {
            if (succeeded) {
                fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestAcceptMessage,
                                                currentChangeRequestId),
                                      NotificationEvent.NotificationType.SUCCESS);
            }

            busyIndicatorView.hideBusyIndicator();
        }, acceptChangeRequestErrorCallback())
                .acceptChangeRequest(workspaceProject.getSpace().getName(),
                                     repository.getAlias(),
                                     currentChangeRequestId);
    }

    private ErrorCallback<Object> acceptChangeRequestErrorCallback() {
        return (message, throwable) -> {
            busyIndicatorView.hideBusyIndicator();

            if (throwable instanceof NothingToMergeException) {
                notificationEvent.fire(
                        new NotificationEvent(ts.getTranslation(LibraryConstants.NothingToMergeMessage),
                                              NotificationEvent.NotificationType.WARNING));
                return false;
            }

            return true;
        };
    }

    private void revertChangeRequestAction() {
        this.changeRequestService.call((final Boolean succeeded) -> {
            if (succeeded) {
                fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestRevertMessage,
                                                currentChangeRequestId),
                                      NotificationEvent.NotificationType.SUCCESS);
            } else {
                fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestRevertFailMessage,
                                                currentChangeRequestId),
                                      NotificationEvent.NotificationType.WARNING);
            }
        }).revertChangeRequest(workspaceProject.getSpace().getName(),
                               repository.getAlias(),
                               currentChangeRequestId);
    }

    private void fireNotificationEvent(final String message,
                                       final NotificationEvent.NotificationType type) {
        notificationEvent.fire(new NotificationEvent(message,
                                                     type));
    }

    private void finishLoading(final ChangeRequest changeRequest) {
        if (overviewTabLoaded && changedFilesTabLoaded) {
            this.setupActionButtons(changeRequest,
                                    this.currentTargetBranch);

            this.overviewScreen.checkWarnConflict(changeRequest);

            busyIndicatorView.hideBusyIndicator();
        }
    }

    public interface View extends UberElemental<ChangeRequestReviewScreenPresenter> {

        void setTitle(final String title);

        void setChangedFilesCount(final int count);

        void setContent(final HTMLElement content);

        void showRejectButton(final boolean isVisible);

        void showAcceptButton(final boolean isVisible);

        void enableAcceptButton(final boolean isEnabled);

        void showRevertButton(final boolean isVisible);

        void activateOverviewTab();

        void activateChangedFilesTab();

        void resetButtonState();

        void resetAll();
    }
}
