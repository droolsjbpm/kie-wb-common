/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteAccessImplTest {

    private RemoteAccessImpl remoteAccess;
    private KieServicesClient kieServicesClientMock;
    private ServiceResponse<KieServerInfo> serviceResponseMock;
    private ServiceResponse<KieContainerResourceList> containerResourcesResponseMock;

    @Before
    public void setUp() throws Exception {
        remoteAccess = new RemoteAccessImpl() {
            @Override
            KieServicesClient getKieServicesClient( String username,
                                                    String password,
                                                    String _endpoint ) {

                return kieServicesClientMock;
            }
        };
        kieServicesClientMock = mock( KieServicesClient.class );
        serviceResponseMock = mock( ServiceResponse.class );
        containerResourcesResponseMock = mock( ServiceResponse.class );

        when( kieServicesClientMock.listContainers() ).thenReturn( containerResourcesResponseMock );
    }

    private Server createServer( final String id,
                                 final String endpoint,
                                 final String name,
                                 final String username,
                                 final String password,
                                 final ContainerStatus containerStatus,
                                 final ConnectionType connectionType,
                                 Collection<Container> containers,
                                 Map<String, String> properties,
                                 KieContainerResourceList containersConfig ) {
        Collection<ContainerRef> containersList = new ArrayList<ContainerRef>();
        for ( KieContainerResource container : containersConfig.getContainers() ) {
            containersList.add( remoteAccess.toContainer( id, container ) );
        }
        return new ServerImpl( id, endpoint, name, username, password, containerStatus, connectionType, containers, properties, containersList );
    }

    private ServerRefImpl createServerRef( final String id,
                                           final String endpoint,
                                           final String name,
                                           final String username,
                                           final String password,
                                           final ContainerStatus containerStatus,
                                           final ConnectionType connectionType,
                                           Collection<ContainerRef> containers,
                                           Map<String, String> properties ) {
        return new ServerRefImpl( id, endpoint, name, username, password, containerStatus, connectionType, properties, containers );
    }

    private Collection<Container> getContainers( String endPointCleaned,
                                                 KieContainerResourceList containersConfig ) {
        Collection<Container> containersList = new ArrayList<Container>(  );
        for ( KieContainerResource container : containersConfig.getContainers() ) {
            containersList.add( remoteAccess.toContainer( endPointCleaned, container ) );
        }
        return containersList;
    }

    private KieContainerResourceList generateContainers() {
        List<KieContainerResource> containers = new ArrayList<KieContainerResource>();
        containers.add( new KieContainerResource( "1", new ReleaseId( "groupId", "artifact", "version" ), KieContainerStatus.CREATING ) );
        containers.add( new KieContainerResource( "2", new ReleaseId( "groupId", "artifact", "version" ), KieContainerStatus.CREATING ) );

        KieContainerResourceList kieContainerResource = new KieContainerResourceList( containers );
        return kieContainerResource;
    }

    @Test
    public void testToServerRef() throws Exception {
        String endpoint = "http://uberfire.org/s/rest/";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        final String name = "name";
        final String username = "username";
        final String password = "password";
        final ConnectionType connectionType = ConnectionType.REMOTE;
        Collection<ContainerRef> containerRefs = new ArrayList<ContainerRef>();
        Map<String, String> properties = new HashMap<String, String>();
        String expectedId = "serverId";
        String version = "version";
        KieServerInfo serverInfo = new KieServerInfo( expectedId, version );
        properties.put( "version", version );

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( serviceResponseMock.getResult() ).thenReturn( serverInfo );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );

        ServerRef actual = remoteAccess.toServerRef( endpoint, name, username, password, connectionType, containerRefs );
        ServerRef expected = createServerRef( expectedId, endPointCleaned, name, username, password, ContainerStatus.LOADING, connectionType, containerRefs, properties );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServerRefWithoutServiceResponseSuccess() throws Exception {
        String endpoint = "http://uberfire.org/s/rest/";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        final String name = "name";
        final String username = "username";
        final String password = "password";
        final ConnectionType connectionType = ConnectionType.REMOTE;
        Collection<ContainerRef> containerRefs = new ArrayList<ContainerRef>();
        Map<String, String> properties = new HashMap<String, String>();
        String expectedId = endPointCleaned;
        String version = "version";
        properties.put( "version", null );

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );

        ServerRef actual = remoteAccess.toServerRef( endpoint, name, username, password, connectionType, containerRefs );
        ServerRef expected = createServerRef( expectedId, endPointCleaned, name, username, password, ContainerStatus.LOADING, connectionType, containerRefs, properties );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServerRefWithoutServiceResponseException() throws Exception {
        String endpoint = "http://uberfire.org/s/rest/";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        final String name = "name";
        final String username = "username";
        final String password = "password";
        final ConnectionType connectionType = ConnectionType.REMOTE;
        Collection<ContainerRef> containerRefs = new ArrayList<ContainerRef>();
        Map<String, String> properties = new HashMap<String, String>();
        String expectedId = endPointCleaned;
        String expectedEndpoint = remoteAccess.addBaseURIToEndpoint( endPointCleaned );
        String version = "version";
        KieServerInfo serverInfo = new KieServerInfo( expectedId, version );
        properties.put( "version", version );

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( serviceResponseMock.getResult() ).thenReturn( serverInfo );
        when( kieServicesClientMock.getServerInfo() ).thenThrow( Exception.class ).thenReturn( serviceResponseMock );

        ServerRef actual = remoteAccess.toServerRef( endpoint, name, username, password, connectionType, containerRefs );
        ServerRef expected = createServerRef( expectedId, expectedEndpoint, name, username, password, ContainerStatus.LOADING, connectionType, containerRefs, properties );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServer() throws Exception {
        String endpoint = "http://uberfire.org/s/rest/";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        final String name = "name";
        final String username = "username";
        final String password = "password";
        final ConnectionType connectionType = ConnectionType.REMOTE;
        Collection<ContainerRef> containerRefs = new ArrayList<ContainerRef>();
        Map<String, String> properties = new HashMap<String, String>();
        String expectedId = endPointCleaned;
        properties.put( "version", null );

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        KieContainerResourceList containersConfig = generateContainers();

        when( containerResourcesResponseMock.getResult() ).thenReturn( containersConfig );

        ServerRef serverRef = createServerRef( expectedId, endPointCleaned, name, username, password, ContainerStatus.LOADING, connectionType, containerRefs, properties );


        Server actual = remoteAccess.toServer( serverRef );
        Collection<Container> containersList = getContainers( endPointCleaned, containersConfig );

        Server expected = createServer( endPointCleaned, endPointCleaned, name, username, password, ContainerStatus.STARTED, connectionType, containersList, properties, containersConfig );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServerViaServerRef() throws Exception {
        String endpoint = "http://uberfire.org/s/rest/";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        final String name = "name";
        final String username = "username";
        final String password = "password";
        final ConnectionType connectionType = ConnectionType.REMOTE;
        Collection<ContainerRef> containerRefs = new ArrayList<ContainerRef>();
        Map<String, String> properties = new HashMap<String, String>();
        String expectedId = endPointCleaned;
        String version = "version";
        properties.put( "version", null );

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        KieContainerResourceList containersConfig = generateContainers();

        when( containerResourcesResponseMock.getResult() ).thenReturn( containersConfig );

        Server actual = remoteAccess.toServer( endpoint, name, username, password, connectionType, containerRefs );
        Collection<Container> containersList = getContainers( endPointCleaned, containersConfig );

        Server expected = createServer( endPointCleaned, endPointCleaned, name, username, password, ContainerStatus.STARTED, connectionType, containersList, properties, containersConfig );

        assertEquals( expected, actual );
    }

    @Test
    public void testInstall() throws Exception {
        //TODO
    }
}