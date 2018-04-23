/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
import org.uberfire.ext.security.management.impl.SearchResponseImpl;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchCallback;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchEntry;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchResults;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssigneeLiveSearchServiceTest {

    @Mock
    private UserManager userManager;

    @Mock
    private GroupManager groupManager;

    @Mock
    private ClientUserSystemManager userSystemManager;

    @Mock
    private AssigneeLiveSearchEntryCreationEditor editor;

    @Mock
    private LiveSearchCallback<String> callback;

    private AssigneeLiveSearchService assigneeLiveSearchService;

    @Before
    public void init() {
        when(userSystemManager.users(any(), any())).thenReturn(userManager);
        when(userSystemManager.groups(any(), any())).thenReturn(groupManager);

        assigneeLiveSearchService = new AssigneeLiveSearchService(userSystemManager, editor);

        verify(editor).setCustomEntryCommand(any());
    }

    @Test
    public void testSearchUsers() {
        assigneeLiveSearchService.init(AssigneeType.USER);

        assigneeLiveSearchService.search("admin", 10, callback);

        ArgumentCaptor<SearchRequestImpl> requestArgumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        verify(userManager).search(requestArgumentCaptor.capture());

        SearchRequestImpl request = requestArgumentCaptor.getValue();

        assertEquals("admin", request.getSearchPattern());
        assertEquals(10, request.getPageSize());

        ArgumentCaptor<RemoteCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RemoteCallback.class);

        verify(userSystemManager).users(callbackArgumentCaptor.capture(), any());

        RemoteCallback<AbstractEntityManager.SearchResponse<?>> successCallback = callbackArgumentCaptor.getValue();

        successCallback.callback(prepareUsersResponse());

        ArgumentCaptor<LiveSearchResults> resultsArgumentCaptor = ArgumentCaptor.forClass(LiveSearchResults.class);

        verify(callback).afterSearch(resultsArgumentCaptor.capture());

        LiveSearchResults result = resultsArgumentCaptor.getValue();

        assertEquals(3, result.size());
    }

    @Test
    public void testSearchUsersIncludeCustomEntries() {
        assigneeLiveSearchService.init(AssigneeType.USER);

        assigneeLiveSearchService.addCustomEntry("custom_admin");

        assigneeLiveSearchService.search("admin", 10, callback);

        ArgumentCaptor<SearchRequestImpl> requestArgumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        verify(userManager).search(requestArgumentCaptor.capture());

        SearchRequestImpl request = requestArgumentCaptor.getValue();

        assertEquals("admin", request.getSearchPattern());
        assertEquals(10, request.getPageSize());

        ArgumentCaptor<RemoteCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RemoteCallback.class);

        verify(userSystemManager).users(callbackArgumentCaptor.capture(), any());

        RemoteCallback<AbstractEntityManager.SearchResponse<?>> successCallback = callbackArgumentCaptor.getValue();

        successCallback.callback(prepareUsersResponse());

        ArgumentCaptor<LiveSearchResults> resultsArgumentCaptor = ArgumentCaptor.forClass(LiveSearchResults.class);

        verify(callback).afterSearch(resultsArgumentCaptor.capture());

        LiveSearchResults result = resultsArgumentCaptor.getValue();

        assertEquals(4, result.size());
    }

    @Test
    public void testSearchUsersIncludeCustomEntriesNotMatchSearchPattern() {
        assigneeLiveSearchService.init(AssigneeType.USER);

        assigneeLiveSearchService.addCustomEntry("custom_entry");

        assigneeLiveSearchService.search("admin", 10, callback);

        ArgumentCaptor<SearchRequestImpl> requestArgumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        verify(userManager).search(requestArgumentCaptor.capture());

        SearchRequestImpl request = requestArgumentCaptor.getValue();

        assertEquals("admin", request.getSearchPattern());
        assertEquals(10, request.getPageSize());

        ArgumentCaptor<RemoteCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RemoteCallback.class);

        verify(userSystemManager).users(callbackArgumentCaptor.capture(), any());

        RemoteCallback<AbstractEntityManager.SearchResponse<?>> successCallback = callbackArgumentCaptor.getValue();

        successCallback.callback(prepareUsersResponse());

        ArgumentCaptor<LiveSearchResults> resultsArgumentCaptor = ArgumentCaptor.forClass(LiveSearchResults.class);

        verify(callback).afterSearch(resultsArgumentCaptor.capture());

        LiveSearchResults result = resultsArgumentCaptor.getValue();

        assertEquals(3, result.size());
    }

    @Test
    public void testSearchGroups() {
        assigneeLiveSearchService.init(AssigneeType.GROUP);

        assigneeLiveSearchService.search("it", 10, callback);

        ArgumentCaptor<SearchRequestImpl> requestArgumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        verify(groupManager).search(requestArgumentCaptor.capture());

        SearchRequestImpl request = requestArgumentCaptor.getValue();

        assertEquals("it", request.getSearchPattern());
        assertEquals(10, request.getPageSize());

        ArgumentCaptor<RemoteCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RemoteCallback.class);

        verify(userSystemManager).groups(callbackArgumentCaptor.capture(), any());

        RemoteCallback<AbstractEntityManager.SearchResponse<?>> successCallback = callbackArgumentCaptor.getValue();

        successCallback.callback(prepareGroupsResponse());

        ArgumentCaptor<LiveSearchResults> resultsArgumentCaptor = ArgumentCaptor.forClass(LiveSearchResults.class);

        verify(callback).afterSearch(resultsArgumentCaptor.capture());

        LiveSearchResults result = resultsArgumentCaptor.getValue();

        assertEquals(3, result.size());
    }

    @Test
    public void testSearchGroupsIncludeCustomEntries() {
        assigneeLiveSearchService.init(AssigneeType.GROUP);

        assigneeLiveSearchService.addCustomEntry("custom_it");

        assigneeLiveSearchService.search("it", 10, callback);

        ArgumentCaptor<SearchRequestImpl> requestArgumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        verify(groupManager).search(requestArgumentCaptor.capture());

        SearchRequestImpl request = requestArgumentCaptor.getValue();

        assertEquals("it", request.getSearchPattern());
        assertEquals(10, request.getPageSize());

        ArgumentCaptor<RemoteCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RemoteCallback.class);

        verify(userSystemManager).groups(callbackArgumentCaptor.capture(), any());

        RemoteCallback<AbstractEntityManager.SearchResponse<?>> successCallback = callbackArgumentCaptor.getValue();

        successCallback.callback(prepareGroupsResponse());

        ArgumentCaptor<LiveSearchResults> resultsArgumentCaptor = ArgumentCaptor.forClass(LiveSearchResults.class);

        verify(callback).afterSearch(resultsArgumentCaptor.capture());

        LiveSearchResults result = resultsArgumentCaptor.getValue();

        assertEquals(4, result.size());
    }

    @Test
    public void testSearchGroupsIncludeCustomEntriesNotMatchSearchPattern() {
        assigneeLiveSearchService.init(AssigneeType.GROUP);

        assigneeLiveSearchService.addCustomEntry("custom_entry");

        assigneeLiveSearchService.search("it", 10, callback);

        ArgumentCaptor<SearchRequestImpl> requestArgumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        verify(groupManager).search(requestArgumentCaptor.capture());

        SearchRequestImpl request = requestArgumentCaptor.getValue();

        assertEquals("it", request.getSearchPattern());
        assertEquals(10, request.getPageSize());

        ArgumentCaptor<RemoteCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RemoteCallback.class);

        verify(userSystemManager).groups(callbackArgumentCaptor.capture(), any());

        RemoteCallback<AbstractEntityManager.SearchResponse<?>> successCallback = callbackArgumentCaptor.getValue();

        successCallback.callback(prepareGroupsResponse());

        ArgumentCaptor<LiveSearchResults> resultsArgumentCaptor = ArgumentCaptor.forClass(LiveSearchResults.class);

        verify(callback).afterSearch(resultsArgumentCaptor.capture());

        LiveSearchResults result = resultsArgumentCaptor.getValue();

        assertEquals(3, result.size());
    }

    @Test
    public void testSearchSingleUser() {
        assigneeLiveSearchService.init(AssigneeType.USER);

        assigneeLiveSearchService.searchEntry("user", callback);

        ArgumentCaptor<SearchRequestImpl> requestArgumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        verify(userManager).search(requestArgumentCaptor.capture());

        SearchRequestImpl request = requestArgumentCaptor.getValue();

        assertEquals("user", request.getSearchPattern());
        assertEquals(1, request.getPageSize());

        ArgumentCaptor<RemoteCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RemoteCallback.class);

        verify(userSystemManager).users(callbackArgumentCaptor.capture(), any());

        RemoteCallback<AbstractEntityManager.SearchResponse<?>> successCallback = callbackArgumentCaptor.getValue();

        successCallback.callback(prepareSingleUserResponse());

        ArgumentCaptor<LiveSearchResults> resultsArgumentCaptor = ArgumentCaptor.forClass(LiveSearchResults.class);

        verify(callback).afterSearch(resultsArgumentCaptor.capture());

        LiveSearchResults<LiveSearchEntry<String>> result = resultsArgumentCaptor.getValue();

        assertEquals(1, result.size());
        assertEquals("user", result.get(0).getValue());
    }

    @Test
    public void testSearchSingleGroup() {
        assigneeLiveSearchService.init(AssigneeType.GROUP);

        assigneeLiveSearchService.searchEntry("it", callback);

        ArgumentCaptor<SearchRequestImpl> requestArgumentCaptor = ArgumentCaptor.forClass(SearchRequestImpl.class);

        verify(groupManager).search(requestArgumentCaptor.capture());

        SearchRequestImpl request = requestArgumentCaptor.getValue();

        assertEquals("it", request.getSearchPattern());
        assertEquals(1, request.getPageSize());

        ArgumentCaptor<RemoteCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RemoteCallback.class);

        verify(userSystemManager).groups(callbackArgumentCaptor.capture(), any());

        RemoteCallback<AbstractEntityManager.SearchResponse<?>> successCallback = callbackArgumentCaptor.getValue();

        successCallback.callback(prepareSingleGroupResponse());

        ArgumentCaptor<LiveSearchResults> resultsArgumentCaptor = ArgumentCaptor.forClass(LiveSearchResults.class);

        verify(callback).afterSearch(resultsArgumentCaptor.capture());

        LiveSearchResults<LiveSearchEntry<String>> result = resultsArgumentCaptor.getValue();

        assertEquals(1, result.size());
        assertEquals("it", result.get(0).getValue());
    }

    private AbstractEntityManager.SearchResponse<?> prepareUsersResponse() {

        List result = new ArrayList();

        result.add(new UserImpl("admin"));
        result.add(new UserImpl("user"));
        result.add(new UserImpl("developer"));

        return new SearchResponseImpl(result, 1, 1, 1, true);
    }

    private AbstractEntityManager.SearchResponse<?> prepareSingleUserResponse() {

        List result = new ArrayList();

        result.add(new UserImpl("user"));

        return new SearchResponseImpl(result, 1, 1, 1, true);
    }

    private AbstractEntityManager.SearchResponse<?> prepareGroupsResponse() {

        List result = new ArrayList();

        result.add(new GroupImpl("it"));
        result.add(new GroupImpl("hr"));
        result.add(new GroupImpl("qe"));

        return new SearchResponseImpl(result, 1, 1, 1, true);
    }

    private AbstractEntityManager.SearchResponse<?> prepareSingleGroupResponse() {

        List result = new ArrayList();

        result.add(new GroupImpl("it"));

        return new SearchResponseImpl(result, 1, 1, 1, true);
    }
}

