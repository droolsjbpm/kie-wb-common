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

package org.kie.workbench.common.screens.projecteditor.client.wizard;

import org.guvnor.common.services.project.client.POMEditorPanel;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

public class GAVWizardPageTest {

    private POMEditorPanel pomEditor;
    private GAVWizardPage page;
    private GAVWizardPageView view;

    private ProjectScreenService projectScreenService;
    private ValidationService validationService;

    @Before
    public void setUp() throws Exception {
        projectScreenService = mock( ProjectScreenService.class );
        validationService = mock( ValidationService.class );
        pomEditor = mock( POMEditorPanel.class );
        view = mock( GAVWizardPageView.class );
        page = spy( new GAVWizardPage( pomEditor,
                                       view,
                                       new EventSourceMock<WizardPageStatusChangeEvent>(),
                                       new CallerMock<ProjectScreenService>( projectScreenService ),
                                       new CallerMock<ValidationService>( validationService ) ) );
    }

    @Test
    public void testInvalidPOMWithParent() throws Exception {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( false );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( false );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( false );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( false );
        page.setPom( new POM(),
                     true );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                never() ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                never() ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( false ) );
        verify( pomEditor,
                never() ).setValidGroupID( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( false ) );
        verify( pomEditor,
                never() ).setValidVersion( eq( false ) );
    }

    @Test
    public void testInvalidPOMWithoutParent() throws Exception {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( false );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( false );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( false );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( false );
        page.setPom( new POM(),
                     false );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                times( 1 ) ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidGroupID( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidVersion( eq( false ) );
    }

    @Test
    public void testValidPOMWithParent() throws Exception {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( true );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( true );
        page.setPom( new POM(),
                     true );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                never() ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                never() ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( true ) );
        verify( pomEditor,
                never() ).setValidGroupID( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( true ) );
        verify( pomEditor,
                never() ).setValidVersion( eq( true ) );
    }

    @Test
    public void testValidPOMWithoutParent() throws Exception {
        when( projectScreenService.validateGroupId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateArtifactId( any( String.class ) ) ).thenReturn( true );
        when( projectScreenService.validateVersion( any( String.class ) ) ).thenReturn( true );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( true );
        page.setPom( new POM(),
                     false );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                times( 1 ) ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidGroupID( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidVersion( eq( true ) );
    }

    @Test
    public void testPomsWithParentDataDisableFieldsParentNotSet() throws Exception {
        page.setPom( new POM(), false );

        verify( pomEditor, never() ).disableGroupID( anyString() );
        verify( pomEditor, never() ).disableVersion( anyString() );
    }

    @Test
    public void testPomsWithParentDataDisableFieldsParentSet() throws Exception {
        when( view.InheritedFromAParentPOM() ).thenReturn( "InheritedFromAParentPOM" );
        POM pom = new POM();
        pom.getGav().setGroupId( "supergroup" );
        page.setPom( pom, true );

        verify( pomEditor ).disableGroupID( "InheritedFromAParentPOM" );
        verify( pomEditor ).disableVersion( "InheritedFromAParentPOM" );
    }

}
