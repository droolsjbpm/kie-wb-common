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

package org.kie.workbench.common.screens.server.management.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.KieServerControllerAdmin;
import org.kie.server.controller.api.storage.KieServerControllerStorage;
import org.kie.workbench.common.screens.server.management.events.ContainerCreated;
import org.kie.workbench.common.screens.server.management.events.ContainerDeleted;
import org.kie.workbench.common.screens.server.management.events.ContainerOnError;
import org.kie.workbench.common.screens.server.management.events.ContainerStarted;
import org.kie.workbench.common.screens.server.management.events.ContainerStopped;
import org.kie.workbench.common.screens.server.management.events.ContainerUpdated;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerDeleted;
import org.kie.workbench.common.screens.server.management.events.ServerDisconnected;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ServerManagementServiceImplTest {

    @Mock
    private EventSourceMock<ServerConnected> serverConnectedEvent;

    @Mock
    private EventSourceMock<ServerDisconnected> serverDisonnectedEvent;

    @Mock
    private EventSourceMock<ServerOnError> serverOnErrorEvent;

    @Mock
    private EventSourceMock<ServerDeleted> serverDeletedEvent;

    @Mock
    private EventSourceMock<ContainerCreated> containerCreatedEvent;

    @Mock
    private EventSourceMock<ContainerStarted> containerStartedEvent;

    @Mock
    private EventSourceMock<ContainerStopped> containerStoppedEvent;

    @Mock
    private EventSourceMock<ContainerDeleted> containerDeletedEvent;

    @Mock
    private EventSourceMock<ContainerUpdated> containerUpdatedEvent;

    @Mock
    private EventSourceMock<ContainerOnError> containerOnErrorEvent;

    @Mock
    private ServerReferenceStorageImpl storage;

    @Mock
    private RemoteAccessImpl remoteAccess;

    @Mock
    private KieServerControllerAdmin controllerAdmin;

    @Mock
    private KieServerControllerStorage controllerStorage;

    @Mock
    private Executor executor;

    private ServerManagementServiceImpl serverManagementService;

    @Before
    public void setUp() throws Exception {
        serverManagementService = new ServerManagementServiceImpl( serverConnectedEvent,
                                                                   serverOnErrorEvent,
                                                                   serverDeletedEvent,
                                                                   containerCreatedEvent,
                                                                   containerStartedEvent,
                                                                   containerStoppedEvent,
                                                                   containerDeletedEvent,
                                                                   containerUpdatedEvent,
                                                                   containerOnErrorEvent,
                                                                   serverDisonnectedEvent,
                                                                   storage, remoteAccess, controllerAdmin,controllerStorage, executor );
    }

    @Test
    public void testEmptyListServers() throws Exception {
        when( storage.listRegisteredServers() ).thenReturn( Collections.<ServerRef>emptyList() );

        final Collection<ServerRef> serverRefs = serverManagementService.listServers();

        verify( storage, times( 1 ) ).listRegisteredServers();
        assertNotNull( serverRefs );
        assertEquals( 0, serverRefs.size() );
    }

    @Test
    public void testListServers() throws Exception {

        final ArgumentCaptor<Runnable> selectedRunnable = ArgumentCaptor.forClass( Runnable.class );

        doAnswer( new Answer<Void>() {
            public Void answer( InvocationOnMock invocation ) {
                selectedRunnable.getValue().run();
                return null;
            }
        } ).when( executor ).execute( selectedRunnable.capture() );

        final Server serverRef1 = new ServerImpl( "server_id1", "server_url1", "my_server1",
                                                  null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                  Collections.<Container>emptyList(),
                                                  Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        final ServerRef serverRef3 = new ServerRefImpl( "server_id3", "server_url3", "my_server3",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );

        when( storage.listRegisteredServers() ).thenReturn( Arrays.asList( serverRef1, serverRef2, serverRef3 ) );

        when( remoteAccess.toServer( serverRef1 ) ).thenReturn( serverRef1 );
        when( remoteAccess.toServer( serverRef2 ) ).thenReturn( null );
        when( remoteAccess.toServer( serverRef3 ) ).thenThrow( new RuntimeException() );

        final Collection<ServerRef> serverRefs = serverManagementService.listServers();

        verify( storage, times( 1 ) ).listRegisteredServers();

        verify( executor, times( 3 ) ).execute( any( Runnable.class ) );

        verify( serverOnErrorEvent, times( 2 ) ).fire( any( ServerOnError.class ) );

        final ArgumentCaptor<ServerConnected> serverConnectedCaptor = ArgumentCaptor.forClass( ServerConnected.class );

        verify( serverConnectedEvent, times( 1 ) ).fire( serverConnectedCaptor.capture() );

        assertEquals( serverRef1, serverConnectedCaptor.getValue().getServer() );

        assertNotNull( serverRefs );
        assertEquals( 3, serverRefs.size() );
    }

    @Test
    public void testRefresh() throws Exception {

        final ArgumentCaptor<Runnable> selectedRunnable = ArgumentCaptor.forClass( Runnable.class );

        doAnswer( new Answer<Void>() {
            public Void answer( InvocationOnMock invocation ) {
                selectedRunnable.getValue().run();
                return null;
            }
        } ).when( executor ).execute( selectedRunnable.capture() );

        final Server serverRef1 = new ServerImpl( "server_id1", "server_url1", "my_server1",
                                                  null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                  Collections.<Container>emptyList(),
                                                  Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        final ServerRef serverRef3 = new ServerRefImpl( "server_id3", "server_url3", "my_server3",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );

        when( storage.listRegisteredServers() ).thenReturn( Arrays.asList( serverRef1, serverRef2, serverRef3 ) );

        when( remoteAccess.toServer( serverRef1 ) ).thenReturn( serverRef1 );
        when( remoteAccess.toServer( serverRef2 ) ).thenReturn( null );
        when( remoteAccess.toServer( serverRef3 ) ).thenThrow( new RuntimeException() );

        serverManagementService.refresh();

        verify( storage, times( 1 ) ).listRegisteredServers();

        verify( executor, times( 3 ) ).execute( any( Runnable.class ) );

        verify( serverOnErrorEvent, times( 2 ) ).fire( any( ServerOnError.class ) );

        final ArgumentCaptor<ServerConnected> serverConnectedCaptor = ArgumentCaptor.forClass( ServerConnected.class );

        verify( serverConnectedEvent, times( 1 ) ).fire( serverConnectedCaptor.capture() );

        assertEquals( serverRef1, serverConnectedCaptor.getValue().getServer() );
    }



    @Test
    public void testStartContainers() throws Exception {
        final Container container1 = new ContainerImpl( "server_id1", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );

        final ServerRef serverRef1 = new ServerRefImpl( "server_id1", "server_url1", "my_server1",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( (ContainerRef) container1 ) );

        final Container container2 = new ContainerImpl( "server_id2", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( (ContainerRef) container2 ) );

        when( storage.loadServerRef( "server_id1" ) ).thenReturn( serverRef1 );
        when( storage.loadServerRef( "server_id2" ) ).thenReturn( serverRef2 );

        when( remoteAccess.install( container1.getServerId(), "server_url1", container1.getId(), null, null, new GAV( "com.example", "example-artifact", "LATEST" ) ) ).thenReturn( container1 );

        when( remoteAccess.install( container2.getServerId(), "server_url2", container2.getId(), null, null, new GAV( "com.example", "example-artifact", "LATEST" ) ) ).thenReturn( container2 );

        serverManagementService.startContainers( new HashMap<String, List<String>>() {{
            put( "server_id1", new ArrayList<String>() {{
                add( "my_container_id" );
            }} );
            put( "server_id2", new ArrayList<String>() {{
                add( "my_container_id" );
            }} );
        }} );

        final ArgumentCaptor<ContainerStarted> containerStartedCaptor = ArgumentCaptor.forClass( ContainerStarted.class );

        verify( containerStartedEvent, times( 2 ) ).fire( containerStartedCaptor.capture() );

        final List<ContainerStarted> values = containerStartedCaptor.getAllValues();
        assertEquals( 2, values.size() );

        assertTrue( values.get( 0 ).getContainer().equals( container1 ) || values.get( 1 ).getContainer().equals( container1 ) );
        assertTrue( values.get( 0 ).getContainer().equals( container2 ) || values.get( 1 ).getContainer().equals( container2 ) );
    }
}
