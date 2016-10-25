/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.admin;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspective;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;

public class DefaultAdminPageHelper {

    DefaultWorkbenchConstants constants = DefaultWorkbenchConstants.INSTANCE;

    @Inject
    private AdminPage adminPage;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ClientUserSystemManager userSystemManager;

    public void setup() {
        adminPage.addScreen( "root", constants.Settings() );

        adminPage.addTool( "root",
                           constants.Roles(),
                           "fa-unlock-alt",
                           "security",
                           () -> {
                               Map<String, String> params = new HashMap<>();
                               params.put( "activeTab", "RolesTab" );
                               placeManager.goTo( new DefaultPlaceRequest( SECURITY_MANAGEMENT, params ) );
                           },
                           command -> userSystemManager.roles( ( AbstractEntityManager.SearchResponse<Role> response ) -> {
                               if ( response != null ) {
                                   command.execute( response.getTotal() );
                               }
                           }, ( o, throwable ) -> false ).search( new SearchRequestImpl( "", 1, 1, null ) ) );

        adminPage.addTool( "root",
                           constants.Groups(),
                           "fa-users",
                           "security",
                           () -> {
                               Map<String, String> params = new HashMap<>();
                               params.put( "activeTab", "GroupsTab" );
                               placeManager.goTo( new DefaultPlaceRequest( SECURITY_MANAGEMENT, params ) );
                           },
                           command -> userSystemManager.groups( ( AbstractEntityManager.SearchResponse<Group> response ) -> {
                               if ( response != null ) {
                                   command.execute( response.getTotal() );
                               }
                           }, ( o, throwable ) -> false ).search( new SearchRequestImpl( "", 1, 1, null ) ) );

        adminPage.addTool( "root",
                           constants.Users(),
                           "fa-user",
                           "security",
                           () -> {
                               Map<String, String> params = new HashMap<>();
                               params.put( "activeTab", "UsersTab" );
                               placeManager.goTo( new DefaultPlaceRequest( SECURITY_MANAGEMENT, params ) );
                           },
                           command -> userSystemManager.users( ( AbstractEntityManager.SearchResponse<User> response ) -> {
                               if ( response != null ) {
                                   command.execute( response.getTotal() );
                               }
                           }, ( o, throwable ) -> false ).search( new SearchRequestImpl( "", 1, 1, null ) ) );
    }

    public Command getAdminToolCommand( final String screen ) {
        return () -> {
            Map<String, String> params = new HashMap<>();
            params.put( "screen", screen );
            placeManager.goTo( new DefaultPlaceRequest( AdminPagePerspective.IDENTIFIER, params ) );
        };
    }
}
