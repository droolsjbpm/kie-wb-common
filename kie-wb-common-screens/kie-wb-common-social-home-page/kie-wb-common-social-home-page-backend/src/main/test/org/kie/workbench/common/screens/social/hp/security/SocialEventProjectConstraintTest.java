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
package org.kie.workbench.common.screens.social.hp.security;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.backend.repositories.RepositoryServiceImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.social.OrganizationalUnitEventType;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.authz.AuthorizationManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class SocialEventProjectConstraintTest {

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private RepositoryServiceImpl repositoryService;

    @Mock
    private KieProjectService projectService;

    @Mock
    private User identity;

    private SocialEventProjectConstraint socialEventProjectConstraint;

    private SocialUser socialUser = new SocialUser( "dora" );
    private GitRepository repository;
    private Project eventProject;

    @Before
    public void setUp() throws Exception {
        Collection<OrganizationalUnit> ous = new ArrayList<OrganizationalUnit>();
        final OrganizationalUnitImpl ou = new OrganizationalUnitImpl( "ouname",
                                                                      "owner",
                                                                      "groupid" );
        final OrganizationalUnitImpl ouSpy = spy( ou );
        Collection<Repository> repositories = new ArrayList<Repository>();
        repository = new GitRepository( "repo" );
        repositories.add( repository );
        ous.add( ouSpy );

        when( ouSpy.getRepositories() ).thenReturn( repositories );
        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( ous );
        when( authorizationManager.authorize( ou,
                                              identity ) ).thenReturn( true );
        when( authorizationManager.authorize( repository,
                                              identity ) ).thenReturn( true );

        socialEventProjectConstraint = createSocialEventProjectConstraint();
    }

    @Test
    public void hasRestrictionsTest() throws Exception {
        final Project project = mock( Project.class );
        when( authorizationManager.authorize( project,
                                              identity ) ).thenReturn( false );
        eventProject = project;

        final SocialActivitiesEvent event = new SocialActivitiesEvent( socialUser,
                                                                       OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT,
                                                                       new Date() ).withLink( "otherName",
                                                                                              "otherName",
                                                                                              SocialActivitiesEvent.LINK_TYPE.VFS );

        socialEventProjectConstraint.init();

        assertTrue( socialEventProjectConstraint.hasRestrictions( event ) );
    }

    @Test
    public void hasNoRestrictionsTest() throws Exception {
        final Project project = mock( Project.class );
        when( authorizationManager.authorize( project,
                                              identity ) ).thenReturn( true );
        eventProject = project;

        final SocialActivitiesEvent vsfEvent = new SocialActivitiesEvent( socialUser,
                                                                          "type",
                                                                          new Date() );
        final SocialActivitiesEvent projectEvent = new SocialActivitiesEvent( socialUser,
                                                                              OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT,
                                                                              new Date() ).withLink( "otherName",
                                                                                                     "otherName",
                                                                                                     SocialActivitiesEvent.LINK_TYPE.CUSTOM );

        socialEventProjectConstraint.init();

        assertFalse( socialEventProjectConstraint.hasRestrictions( vsfEvent ) );
        assertFalse( socialEventProjectConstraint.hasRestrictions( projectEvent ) );
    }

    @Test
    public void nullProjecthasNoRestrictionsTest() throws Exception {

        eventProject = null;

        final SocialActivitiesEvent vsfEvent = new SocialActivitiesEvent( socialUser,
                                                                          "type",
                                                                          new Date() );
        final SocialActivitiesEvent projectEvent = new SocialActivitiesEvent( socialUser,
                                                                              OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT,
                                                                              new Date() ).withLink( "otherName",
                                                                                                     "otherName",
                                                                                                     SocialActivitiesEvent.LINK_TYPE.CUSTOM );

        socialEventProjectConstraint.init();

        assertFalse( socialEventProjectConstraint.hasRestrictions( vsfEvent ) );
        assertFalse( socialEventProjectConstraint.hasRestrictions( projectEvent ) );

        verify( authorizationManager, never() ).authorize( null, identity );
    }

    @Test
    public void hasNoRestrictionsForOtherSocialEventsTest() throws Exception {
        final Project project = mock( Project.class );
        eventProject = project;

        final SocialActivitiesEvent customEventOtherType = new SocialActivitiesEvent( socialUser,
                                                                                      "type",
                                                                                      new Date() ).withLink( "link",
                                                                                                             "link",
                                                                                                             SocialActivitiesEvent.LINK_TYPE.CUSTOM );

        assertFalse( socialEventProjectConstraint.hasRestrictions( customEventOtherType ) );
    }

    private SocialEventProjectConstraint createSocialEventProjectConstraint() {
        final SocialEventRepositoryConstraint delegate = new SocialEventRepositoryConstraint( organizationalUnitService,
                                                                                              authorizationManager,
                                                                                              repositoryService,
                                                                                              identity ) {
            @Override
            Repository getEventRepository( final SocialActivitiesEvent event ) {
                return repository;
            }
        };
        return new SocialEventProjectConstraint( delegate,
                                                 authorizationManager,
                                                 projectService,
                                                 identity ) {

            @Override
            Project getEventProject( final SocialActivitiesEvent event ) {
                return eventProject;
            }
        };
    }

}