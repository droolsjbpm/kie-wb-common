/*
 * Copyright 2014 JBoss Inc
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
package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.defaulteditor.client.editor.resources.i18n.GuvnorDefaultEditorConstants;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorNewFileUpload;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class NewFileUploader
        extends DefaultNewResourceHandler {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private DefaultEditorNewFileUpload options;

    @Inject
    private AnyResourceTypeDefinition resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @PostConstruct
    private void setupExtensions() {
        extensions.add( new Pair<String, DefaultEditorNewFileUpload>( GuvnorDefaultEditorConstants.INSTANCE.Options(),
                                                                      options ) );
    }

    @Override
    public String getDescription() {
        return GuvnorDefaultEditorConstants.INSTANCE.NewFileDescription();
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final org.guvnor.common.services.project.model.Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        busyIndicatorView.showBusyIndicator( GuvnorDefaultEditorConstants.INSTANCE.Uploading() );

        final Path path = pkg.getPackageMainResourcesPath();
        final Path newPath = PathFactory.newPathBasedOn( baseFileName,
                                                         URL.encode( path.toURI() + "/" + baseFileName ),
                                                         path );

        options.setFolderPath( pkg.getPackageMainResourcesPath() );
        options.setFileName( baseFileName );

        options.upload( new Command() {

                            @Override
                            public void execute() {
                                busyIndicatorView.hideBusyIndicator();
                                presenter.complete();
                                notifySuccess();
                                placeManager.goTo( newPath );
                            }

                        },
                        new Command() {

                            @Override
                            public void execute() {
                                busyIndicatorView.hideBusyIndicator();
                            }
                        } );
    }
}
