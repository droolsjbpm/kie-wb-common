/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultAdminPageHelperTest {

    @Mock
    private AdminPage adminPage;

    @Mock
    private TranslationService translationService;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private SessionInfo sessionInfo;

    @InjectMocks
    private DefaultAdminPageHelper defaultAdminPageHelper;

    @Before
    public void setup() {
        mockConstants();
    }

    @Test
    public void securityShortcutsAreAddedWhenUserHasPermission() {
        doReturn(true).when(authorizationManager).authorize(any(ResourceRef.class),
                                                            any(User.class));

        defaultAdminPageHelper.setup();

        final String roles = defaultAdminPageHelper.constants.Roles();
        verify(adminPage).addTool(eq("root"),
                                  eq(roles),
                                  any(),
                                  any(),
                                  any(),
                                  any());

        final String groups = defaultAdminPageHelper.constants.Groups();
        verify(adminPage).addTool(eq("root"),
                                  eq(groups),
                                  any(),
                                  any(),
                                  any(),
                                  any());

        final String users = defaultAdminPageHelper.constants.Users();
        verify(adminPage).addTool(eq("root"),
                                  eq(users),
                                  any(),
                                  any(),
                                  any(),
                                  any());
    }

    @Test
    public void securityShortcutsAreNotAddedWhenUserHasNoPermission() {
        doReturn(false).when(authorizationManager).authorize(any(ResourceRef.class),
                                                             any(User.class));

        defaultAdminPageHelper.setup();

        final String roles = defaultAdminPageHelper.constants.Roles();
        verify(adminPage,
               never()).addTool(eq("root"),
                                eq(roles),
                                any(),
                                any(),
                                any(),
                                any());

        final String groups = defaultAdminPageHelper.constants.Groups();
        verify(adminPage,
               never()).addTool(eq("root"),
                                eq(groups),
                                any(),
                                any(),
                                any(),
                                any());

        final String users = defaultAdminPageHelper.constants.Users();
        verify(adminPage,
               never()).addTool(eq("root"),
                                eq(users),
                                any(),
                                any(),
                                any(),
                                any());
    }

    private void mockConstants() {
        defaultAdminPageHelper.constants = mock(DefaultWorkbenchConstants.class,
                                                (Answer) invocation -> {
                                                    if (String.class.equals(invocation.getMethod().getReturnType())) {
                                                        return invocation.getMethod().getName();
                                                    } else {
                                                        return RETURNS_DEFAULTS.answer(invocation);
                                                    }
                                                });
    }
}
