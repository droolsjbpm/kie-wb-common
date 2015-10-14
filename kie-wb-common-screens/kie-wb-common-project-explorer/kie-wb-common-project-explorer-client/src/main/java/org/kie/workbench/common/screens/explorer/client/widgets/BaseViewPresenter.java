/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.asset.management.social.AssetManagementEventTypes;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.social.ProjectEventType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.uberfire.social.activities.model.ExtendedTypes;
import org.kie.uberfire.social.activities.model.SocialFileSelectedEvent;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.client.widgets.tagSelector.TagChangedEvent;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.model.URIStructureExplorerModel;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

public abstract class BaseViewPresenter
        implements ViewPresenter {

    private static final String BUILD_PROJECT_PROPERTY_NAME = "build.disable-project-explorer";

    @Inject
    protected User identity;

    @Inject
    protected RuntimeAuthorizationManager authorizationManager;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected Caller<ExplorerService> explorerService;

    @Inject
    protected Caller<BuildService> buildService;

    @Inject
    protected Caller<VFSService> vfsService;

    @Inject
    protected Caller<ValidationService> validationService;

    @Inject
    protected Event<BuildResults> buildResultsEvent;

    @Inject
    protected Event<ProjectContextChangeEvent> contextChangedEvent;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private ActiveContextItems activeContextItems;

    @Inject
    private ActiveContextManager activeContextManager;

    @Inject
    private ActiveContextOptions activeOptions;

    private boolean isOnLoading = false;
    private BaseViewImpl baseView;

    protected Set<String> activeContentTags = new TreeSet<String>();
    protected String currentTag = null;

    public BaseViewPresenter( BaseViewImpl baseView ) {
        this.baseView = baseView;
    }

    @PostConstruct
    public void init() {
        activeContextManager.init( baseView,
                                   getContentCallback() );
        baseView.init( this );
    }

    @Override
    public void update() {
        baseView.setOptions( activeOptions.getOptions() );

        if ( activeOptions.getOptions().contains( Option.TREE_NAVIGATOR ) ) {
            baseView.setNavType( Explorer.NavType.TREE );
        } else {
            baseView.setNavType( Explorer.NavType.BREADCRUMB );
        }
        if ( activeOptions.getOptions().contains( Option.NO_CONTEXT_NAVIGATION ) ) {
            baseView.hideHeaderNavigator();
        }
        if ( activeOptions.getOptions().contains( Option.SHOW_TAG_FILTER ) ) {
            baseView.showTagFilter();
            activeContextManager.refresh( false );
        } else {
            baseView.hideTagFilter();
            if ( activeContextItems.getActiveContent() != null ) {
                baseView.setItems( activeContextItems.getActiveContent() );
            }
        }
    }

    @Override
    public void refresh() {
        activeContextManager.refresh( true );
    }

    @Override
    public void loadContent( final FolderItem item ) {
        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( FolderListing fl ) {
                baseView.getExplorer().loadContent( fl );
            }
        } ).getFolderListing( activeContextItems.getActiveOrganizationalUnit(),
                              activeContextItems.getActiveRepository(),
                              activeContextItems.getActiveProject(),
                              item,
                              activeOptions.getOptions() );
    }

    @Override
    public FolderListing getActiveContent() {
        return activeContextItems.getActiveContent();
    }

    @Override
    public void deleteItem( final FolderItem folderItem ) {
        baseView.deleteItem( new ParameterizedCommand<String>() {
            @Override
            public void execute( final String comment ) {
                baseView.showBusyIndicator( CommonConstants.INSTANCE.Deleting() );
                explorerService.call( new RemoteCallback<Object>() {
                                          @Override
                                          public void callback( Object o ) {
                                              notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
                                              activeContextManager.refresh( false );
                                          }
                                      },
                                      new HasBusyIndicatorDefaultErrorCallback( baseView ) ).deleteItem( folderItem, comment );
            }
        } );
    }

    @Override
    public void renameItem( final FolderItem folderItem ) {
        final Path path = getFolderItemPath( folderItem );
        baseView.renameItem( path,
                             new Validator() {
                                 @Override
                                 public void validate( final String value,
                                                       final ValidatorCallback callback ) {
                                     validationService.call( new RemoteCallback<Object>() {
                                         @Override
                                         public void callback( Object response ) {
                                             if ( Boolean.TRUE.equals( response ) ) {
                                                 callback.onSuccess();
                                             } else {
                                                 callback.onFailure();
                                             }
                                         }
                                     } ).isFileNameValid( path,
                                                          value );
                                 }
                             },
                             new CommandWithFileNameAndCommitMessage() {
                                 @Override
                                 public void execute( final FileNameAndCommitMessage details ) {
                                     baseView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                     explorerService.call(
                                             new RemoteCallback<Void>() {
                                                 @Override
                                                 public void callback( final Void o ) {
                                                     notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
                                                     baseView.hideBusyIndicator();
                                                     refresh();
                                                 }
                                             },
                                             new HasBusyIndicatorDefaultErrorCallback( baseView )
                                                         ).renameItem( folderItem,
                                                                       details.getNewFileName(),
                                                                       details.getCommitMessage() );
                                 }
                             }
                           );
    }

    @Override
    public void copyItem( final FolderItem folderItem ) {
        final Path path = getFolderItemPath( folderItem );
        baseView.copyItem( path,
                           new Validator() {
                               @Override
                               public void validate( final String value,
                                                     final ValidatorCallback callback ) {
                                   validationService.call( new RemoteCallback<Object>() {
                                       @Override
                                       public void callback( Object response ) {
                                           if ( Boolean.TRUE.equals( response ) ) {
                                               callback.onSuccess();
                                           } else {
                                               callback.onFailure();
                                           }
                                       }
                                   } ).isFileNameValid( path,
                                                        value );
                               }
                           },
                           new CommandWithFileNameAndCommitMessage() {
                               @Override
                               public void execute( final FileNameAndCommitMessage details ) {
                                   baseView.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                                   explorerService.call( new RemoteCallback<Void>() {
                                                             @Override
                                                             public void callback( final Void o ) {
                                                                 notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
                                                                 baseView.hideBusyIndicator();
                                                                 refresh();
                                                             }
                                                         },
                                                         new HasBusyIndicatorDefaultErrorCallback( baseView ) ).copyItem( folderItem,
                                                                                                                          details.getNewFileName(),
                                                                                                                          details.getCommitMessage() );
                               }
                           }
                         );
    }

    @Override
    public void uploadArchivedFolder( final FolderItem folderItem ) {
        if ( folderItem.getItem() instanceof Path ) {
            final Path path = (Path) folderItem.getItem();

            Window.open( URLHelper.getDownloadUrl( path ),
                         "downloading",
                         "resizable=no,scrollbars=yes,status=no" );
        }
    }

    private Path getFolderItemPath( final FolderItem folderItem ) {
        if ( folderItem.getItem() instanceof Package ) {
            final Package pkg = ( (Package) folderItem.getItem() );
            return pkg.getPackageMainSrcPath();
        } else if ( folderItem.getItem() instanceof Path ) {
            return (Path) folderItem.getItem();
        }
        return null;
    }

    private void loadContent( final FolderListing content ) {
        if ( !activeContextItems.getActiveContent().equals( content ) ) {
            setActiveContent( content );
            baseView.getExplorer().loadContent( content );
        }
    }

    protected void setActiveContent( FolderListing activeContent ) {
        activeContextItems.setActiveContent( activeContent );
        resetTags( false );
    }

    protected void resetTags( boolean maintainSelection ) {
        if ( !isFilterByTagEnabled() ) {
            return;
        }
        if ( !maintainSelection ) {
            currentTag = null;
        }
        activeContentTags.clear();
        for ( FolderItem item : activeContextItems.getActiveContent().getContent() ) {
            if ( item.getTags() != null ) {
                activeContentTags.addAll( item.getTags() );
            }
        }
    }

    @Override
    public String getCurrentTag() {
        return currentTag;
    }

    @Override
    public Set<String> getActiveContentTags() {
        return activeContentTags;
    }

    private RemoteCallback<ProjectExplorerContent> getContentCallback() {
        return new RemoteCallback<ProjectExplorerContent>() {
            @Override
            public void callback( final ProjectExplorerContent content ) {
                doContentCallback( content );
            }

        };
    }

    //Process callback in separate method to better support testing
    void doContentCallback( final ProjectExplorerContent content ) {
        boolean signalChange = false;
        boolean buildSelectedProject = false;

        signalChange = activeContextItems.setupActiveOrganizationalUnit( content );
        if ( activeContextItems.setupActiveRepository( content ) ) {
            signalChange = true;
        }

        if ( activeContextItems.setupActiveProject( content ) ) {
            signalChange = true;
            buildSelectedProject = true;
        }

        boolean folderChange = activeContextItems.setupActiveFolderAndPackage(
                content );
        if ( signalChange || folderChange ) {
            activeContextItems.fireContextChangeEvent();
        }

        if ( buildSelectedProject ) {
            buildProject( activeContextItems.getActiveProject() );
        }

        setActiveContent( content.getFolderListing() );

        baseView.getExplorer().clear();
        activeContextItems.setRepositories( content.getRepositories() );
        baseView.setContent( content.getOrganizationalUnits(),
                             activeContextItems.getActiveOrganizationalUnit(),
                             activeContextItems.getRepositories(),
                             activeContextItems.getActiveRepository(),
                             content.getProjects(),
                             activeContextItems.getActiveProject(),
                             activeContextItems.getActiveContent(),
                             content.getSiblings() );

        if ( activeContextItems.getActiveFolderItem() == null ) {
            activeContextItems.setupActiveFolderAndPackage( content );
        }
        baseView.hideBusyIndicator();
    }

    private void buildProject( final Project project ) {
        //Don't build automatically if disabled
        if ( ApplicationPreferences.getBooleanPref( BUILD_PROJECT_PROPERTY_NAME ) ) {
            return;
        }
        if ( project == null ) {
            return;
        }
        buildService.call(
                new RemoteCallback<BuildResults>() {
                    @Override
                    public void callback( final BuildResults results ) {
                        buildResultsEvent.fire( results );
                    }
                },
                new DefaultErrorCallback() ).build( project );
    }

    @Override
    public void organizationalUnitSelected( final OrganizationalUnit organizationalUnit ) {
        if ( Utils.hasOrganizationalUnitChanged( organizationalUnit,
                                                 activeContextItems.getActiveOrganizationalUnit() ) ) {
            baseView.getExplorer().clear();
            activeContextManager.initActiveContext( organizationalUnit );
        }
    }

    @Override
    public void branchChanged( final String branch ) {
        if ( activeContextItems.getActiveRepository() instanceof GitRepository ) {
            ( (GitRepository) activeContextItems.getActiveRepository() ).changeBranch( branch );
            baseView.getExplorer().clear();
            activeContextManager.initActiveContext( activeContextItems.getActiveProject() );
        }
    }

    @Override
    public void repositorySelected( final Repository repository ) {
        if ( Utils.hasRepositoryChanged( repository,
                                         activeContextItems.getActiveRepository() ) ) {
            baseView.getExplorer().clear();
            activeContextManager.initActiveContext( activeContextItems.getActiveOrganizationalUnit(),
                                                    repository );
        }
    }

    @Override
    public void projectSelected( final Project project ) {
        if ( Utils.hasProjectChanged( project,
                                      activeContextItems.getActiveProject() ) ) {
            baseView.getExplorer().clear();
            activeContextManager.initActiveContext( activeContextItems.getActiveOrganizationalUnit(),
                                                    activeContextItems.getActiveRepository(),
                                                    project );
        }
    }

    @Override
    public void activeFolderItemSelected( final FolderItem item ) {
        if ( !isOnLoading && Utils.hasFolderItemChanged( item, activeContextItems.getActiveFolderItem() ) ) {
            activeContextItems.setActiveFolderItem( item );
            activeContextItems.fireContextChangeEvent();

            //Show busy popup. Once Items are loaded it is closed
            baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            explorerService.call( new RemoteCallback<FolderListing>() {
                                      @Override
                                      public void callback( final FolderListing folderListing ) {
                                          isOnLoading = true;
                                          loadContent( folderListing );
                                          baseView.setItems( folderListing );
                                          baseView.hideBusyIndicator();
                                          isOnLoading = false;
                                      }
                                  },
                                  new HasBusyIndicatorDefaultErrorCallback( baseView ) ).getFolderListing( activeContextItems.getActiveOrganizationalUnit(),
                                                                                                           activeContextItems.getActiveRepository(),
                                                                                                           activeContextItems.getActiveProject(),
                                                                                                           item,
                                                                                                           activeOptions.getOptions() );
        }
    }

    @Override
    public void itemSelected( final FolderItem folderItem ) {
        final Object _item = folderItem.getItem();
        if ( _item == null ) {
            return;
        }
        if ( folderItem.getType().equals( FolderItemType.FILE ) && _item instanceof Path ) {
            placeManager.goTo( (Path) _item );
        } else {
            activeFolderItemSelected( folderItem );
        }
    }

    @Override
    public boolean isVisible() {
        return baseView.isVisible();
    }

    @Override
    public void setVisible( final boolean visible ) {
        baseView.setVisible( visible );
    }

    public void onTagFilterChanged( @Observes TagChangedEvent event ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        if ( !isFilterByTagEnabled() ) {
            return;
        }
        filterByTag( event.getTag() );

    }

    protected void filterByTag( String tag ) {
        currentTag = tag;
        List<FolderItem> filteredItems = new ArrayList<FolderItem>();

        for ( FolderItem item : activeContextItems.getActiveContent().getContent() ) {
            if ( tag == null || item.getTags().contains( tag ) || item.getType().equals( FolderItemType.FOLDER ) ) {
                filteredItems.add( item );
            }
        }

        FolderListing filteredContent = new FolderListing( activeContextItems.getActiveContent().getItem(), filteredItems, activeContextItems.getActiveContent().getSegments() );
        baseView.renderItems( filteredContent );
    }

    // Refresh when a Resource has been updated, if it exists in the active package
    public void onResourceUpdated( @Observes final ResourceUpdatedEvent event ) {
        refresh( event.getPath() );
    }

    // Refresh when a Resource has been added, if it exists in the active package
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        refresh( event.getPath() );
    }

    // Refresh when a Resource has been deleted, if it exists in the active package
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        refresh( event.getPath() );
    }

    // Refresh when a Resource has been copied, if it exists in the active package
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        refresh( event.getDestinationPath() );
    }

    // Refresh when a lock status changes has occurred, if it affects the active package
    public void onLockStatusChange( @Observes final LockInfo lockInfo ) {
        refresh( lockInfo.getFile(), true );
    }

    private void refresh( final Path resource ) {
        refresh( resource, false );
    }

    private void refresh( final Path resource,
                          boolean force ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        if ( resource == null || activeContextItems.getActiveProject() == null ) {
            return;
        }
        if ( !force && !Utils.isInFolderItem( activeContextItems.getActiveFolderItem(),
                                              resource ) ) {
            return;
        }

        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( final FolderListing folderListing ) {
                activeContextItems.setActiveContent( folderListing );
                if ( isFilterByTagEnabled() ) {
                    resetTags( true );
                    filterByTag( currentTag );
                } else {
                    baseView.setItems( folderListing );
                }
            }
        }, new DefaultErrorCallback() ).getFolderListing( activeContextItems.getActiveOrganizationalUnit(),
                                                          activeContextItems.getActiveRepository(),
                                                          activeContextItems.getActiveProject(),
                                                          activeContextItems.getActiveFolderItem(),
                                                          activeOptions.getOptions() );
    }

    public void onSocialFileSelected( @Observes final SocialFileSelectedEvent event ) {
        vfsService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path path ) {
                openBestSuitedScreen( event.getEventType(), path );
                setupActiveContextFor( path );
            }
        } ).get( event.getUri() );
    }

    private void openBestSuitedScreen( final String eventType,
                                       final Path path ) {
        if ( isRepositoryEvent( eventType ) ) {
            //the event is relative to a Repository and not to a file.
            placeManager.goTo( "repositoryStructureScreen" );
        } else if ( isProjectEvent( eventType ) ) {
            placeManager.goTo( "projectScreen" );
        } else {
            placeManager.goTo( path );
        }
    }

    private boolean isRepositoryEvent( String eventType ) {
        if ( eventType == null || eventType.isEmpty() ) {
            return false;
        }

        if ( ExtendedTypes.NEW_REPOSITORY_EVENT.name().equals( eventType ) ||
                AssetManagementEventTypes.BRANCH_CREATED.name().equals( eventType ) ||
                AssetManagementEventTypes.PROCESS_START.name().equals( eventType ) ||
                AssetManagementEventTypes.PROCESS_END.name().equals( eventType ) ||
                AssetManagementEventTypes.ASSETS_PROMOTED.name().equals( eventType ) ||
                AssetManagementEventTypes.PROJECT_BUILT.name().equals( eventType ) ||
                AssetManagementEventTypes.PROJECT_DEPLOYED.name().equals( eventType ) ||
                AssetManagementEventTypes.REPOSITORY_CHANGE.name().equals( eventType ) ) {

            return true;
        }
        return false;
    }

    private boolean isProjectEvent( final String eventType ) {
        return ProjectEventType.NEW_PROJECT.name().equals( eventType );
    }

    private void setupActiveContextFor( final Path path ) {

        explorerService.call( new RemoteCallback<URIStructureExplorerModel>() {
            @Override
            public void callback( URIStructureExplorerModel model ) {
                activeContextManager.initActiveContext( model.getOrganizationalUnit(),
                                                        model.getRepository(),
                                                        model.getProject() );
            }
        } ).getURIStructureExplorerModel( path );

    }

    @Override
    public void initialiseViewForActiveContext( ProjectContext context ) {
        activeContextManager.initActiveContext( context );
    }

    @Override
    public void initialiseViewForActiveContext( String initPath ) {
        activeContextManager.initActiveContext( initPath );
    }

    // Refresh when a Resource has been renamed, if it exists in the active package
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        final Path sourcePath = event.getPath();
        final Path destinationPath = event.getDestinationPath();

        boolean refresh = false;
        if ( Utils.isInFolderItem( activeContextItems.getActiveFolderItem(),
                                   sourcePath ) ) {
            refresh = true;
        } else if ( Utils.isInFolderItem( activeContextItems.getActiveFolderItem(),
                                          destinationPath ) ) {
            refresh = true;
        }

        if ( refresh ) {
            explorerService.call( new RemoteCallback<FolderListing>() {
                                      @Override
                                      public void callback( final FolderListing folderListing ) {
                                          baseView.setItems( folderListing );
                                      }
                                  },
                                  new DefaultErrorCallback() ).getFolderListing( activeContextItems.getActiveOrganizationalUnit(),
                                                                                 activeContextItems.getActiveRepository(),
                                                                                 activeContextItems.getActiveProject(),
                                                                                 activeContextItems.getActiveFolderItem(),
                                                                                 activeOptions.getOptions() );
        }
    }

    protected boolean isFilterByTagEnabled() {
        return activeOptions.getOptions().contains( Option.SHOW_TAG_FILTER );
    }

}
