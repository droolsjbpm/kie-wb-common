/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.handlers;

import com.google.gwt.core.client.Callback;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.client.wizard.NewProjectWizard;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NewProjectHandlerTest {

    private NewProjectHandler handler;

    private ProjectContext context;
    private NewProjectWizard wizard;
    private RepositoryStructureService repoStructureService;
    private CallerMock<RepositoryStructureService> repoStructureCaller;

    private AnyResourceTypeDefinition resourceType = mock( AnyResourceTypeDefinition.class );
    private NewResourcePresenter newResourcePresenter = mock( NewResourcePresenter.class );

    @Before
    public void setup() {
        context = mock( ProjectContext.class );
        wizard = mock( NewProjectWizard.class );
        repoStructureService = mock( RepositoryStructureService.class );
        repoStructureCaller = new CallerMock<RepositoryStructureService>( repoStructureService );
        handler = new NewProjectHandler( context,
                                         wizard,
                                         repoStructureCaller,
                                         resourceType );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreate() {
        handler.create( mock( org.guvnor.common.services.project.model.Package.class ),
                        "projectName",
                        newResourcePresenter );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testValidate() {
        handler.validate( "projectName",
                          mock( ValidatorWithReasonCallback.class ) );
    }

    @Test
    public void testAcceptContextNoActiveRepository() {
        when( context.getActiveRepository() ).thenReturn( null );

        final Callback<Boolean, Void> callback = mock( Callback.class );
        handler.acceptContext( context,
                               callback );

        verify( callback,
                times( 1 ) ).onSuccess( eq( false ) );
    }

    @Test
    public void testAcceptContextWithUnmanagedActiveRepository() {
        final Repository repository = mock( Repository.class );
        final RepositoryStructureModel model = mock( RepositoryStructureModel.class );
        when( context.getActiveRepository() ).thenReturn( repository );
        when( repoStructureService.load( any( Repository.class ) ) ).thenReturn( model );
        when( model.isManaged() ).thenReturn( false );

        final Callback<Boolean, Void> callback = mock( Callback.class );
        handler.acceptContext( context,
                               callback );

        verify( callback,
                times( 1 ) ).onSuccess( eq( true ) );
    }

    @Test
    public void testAcceptContextWithManagedActiveRepositoryIsMultiModule() {
        final Repository repository = mock( Repository.class );
        final RepositoryStructureModel model = mock( RepositoryStructureModel.class );
        when( context.getActiveRepository() ).thenReturn( repository );
        when( repoStructureService.load( any( Repository.class ) ) ).thenReturn( model );
        when( model.isManaged() ).thenReturn( true );
        when( model.isMultiModule() ).thenReturn( true );

        final Callback<Boolean, Void> callback = mock( Callback.class );
        handler.acceptContext( context,
                               callback );

        verify( callback,
                times( 1 ) ).onSuccess( eq( true ) );
    }

    @Test
    public void testAcceptContextWithManagedActiveRepositoryIsNotMultiModule() {
        final Repository repository = mock( Repository.class );
        final RepositoryStructureModel model = mock( RepositoryStructureModel.class );
        when( context.getActiveRepository() ).thenReturn( repository );
        when( repoStructureService.load( any( Repository.class ) ) ).thenReturn( model );
        when( model.isManaged() ).thenReturn( true );
        when( model.isMultiModule() ).thenReturn( false );

        final Callback<Boolean, Void> callback = mock( Callback.class );
        handler.acceptContext( context,
                               callback );

        verify( callback,
                times( 1 ) ).onSuccess( eq( false ) );
    }

    @Test
    public void testGetCommandWithUnmanagedActiveRepository() {
        final Repository repository = mock( Repository.class );
        final RepositoryStructureModel model = mock( RepositoryStructureModel.class );
        when( context.getActiveRepository() ).thenReturn( repository );
        when( repoStructureService.load( any( Repository.class ) ) ).thenReturn( model );
        when( model.isManaged() ).thenReturn( false );

        final Command command = handler.getCommand( newResourcePresenter );
        assertNotNull( command );

        command.execute();

        verify( wizard,
                times( 1 ) ).initialise();
        verify( wizard,
                times( 1 ) ).start();
    }

    @Test
    public void testGetCommandWithManagedActiveRepository() {
        final Repository repository = mock( Repository.class );
        final RepositoryStructureModel model = mock( RepositoryStructureModel.class );
        when( context.getActiveRepository() ).thenReturn( repository );
        when( repoStructureService.load( any( Repository.class ) ) ).thenReturn( model );
        when( model.isManaged() ).thenReturn( true );

        final GAV gav = new GAV( "groupID",
                                 "",
                                 "version" );

        final POM pom = new POM( gav );
        when( model.getPOM() ).thenReturn( pom );

        final Command command = handler.getCommand( newResourcePresenter );
        assertNotNull( command );

        command.execute();

        verify( wizard,
                times( 1 ) ).initialise( eq( gav ) );
        verify( wizard,
                times( 1 ) ).start();
    }

}
