/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.handlers;

import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.wizard.NewProjectWizard;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Projects
 */
@ApplicationScoped
public class NewProjectHandler
        implements NewResourceHandler {

    @Inject
    private Caller<ValidationService> validationService;

    @Inject
    //We don't really need this for Packages but it's required by DefaultNewResourceHandler
    private AnyResourceTypeDefinition resourceType;

    @Inject
    private ProjectContext context;

    @Inject
    private NewProjectWizard wizard;

    @Inject
    private Caller<RepositoryStructureService> repoStructureService;

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        return Collections.emptyList();
    }

    @Override
    public String getDescription() {
        return ProjectEditorResources.CONSTANTS.newProjectDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ProjectEditorResources.INSTANCE.newProjectIcon() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String projectName,
                        final NewResourcePresenter presenter ) {
        if ( context.getActiveRepository() != null ) {

            repoStructureService.call( new RemoteCallback<RepositoryStructureModel>() {

                @Override
                public void callback( RepositoryStructureModel repoModel ) {
                    if ( repoModel != null && repoModel.isManaged() ) {
                        wizard.setContent( projectName,
                                           repoModel.getPOM().getGav().getGroupId(),
                                           repoModel.getPOM().getGav().getVersion() );
                    } else {
                        wizard.setContent( projectName );
                    }
                    wizard.start();
                    presenter.complete();
                }
            } ).load( context.getActiveRepository() );

        } else {
            ErrorPopup.showMessage( ProjectEditorResources.CONSTANTS.NoRepositorySelectedPleaseSelectARepository() );
        }
    }

    @Override
    public void validate( final String projectName,
                          final ValidatorWithReasonCallback callback ) {
        validationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean response ) {
                if ( Boolean.TRUE.equals( response ) ) {
                    callback.onSuccess();
                } else {
                    callback.onFailure( CommonConstants.INSTANCE.InvalidFileName0( projectName ) );
                }
            }
        } ).isProjectNameValid( projectName );
    }

    @Override
    public void acceptContext( final ProjectContext context,
                               final Callback<Boolean, Void> response ) {

        if ( context.getActiveRepository() != null ) {

            //You can always create a new Project (provided a repository has been selected)
            repoStructureService.call( new RemoteCallback<RepositoryStructureModel>() {

                @Override
                public void callback( RepositoryStructureModel repoModel ) {
                    if ( repoModel != null && repoModel.isManaged() ) {
                        boolean isMultiModule = repoModel.isMultiModule();
                        response.onSuccess( isMultiModule );
                    } else {
                        response.onSuccess( true );
                    }
                }
            } ).load( context.getActiveRepository() );
        } else {
            response.onSuccess( false );
        }

    }

}