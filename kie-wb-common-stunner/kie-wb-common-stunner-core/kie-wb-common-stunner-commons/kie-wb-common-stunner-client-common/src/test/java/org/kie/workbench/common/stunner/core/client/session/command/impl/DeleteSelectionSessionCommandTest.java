/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KeyShortcutCallback;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeleteSelectionSessionCommandTest {

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private KeyboardControl<AbstractCanvas, ClientSession> keyboardControl;

    @Mock
    private ClientFullSession session;

    @Captor
    private ArgumentCaptor<KeyShortcutCallback> keyShortcutCallbackCaptor;

    private DeleteSelectionSessionCommand command;

    @Before
    public void setup() {
        this.command = spy(new DeleteSelectionSessionCommand(sessionCommandManager,
                                                             canvasCommandFactory));
        when(session.getKeyboardControl()).thenReturn(keyboardControl);
    }

    @Test
    public void checkBindAttachesKeyHandler() {
        command.bind(session);

        verify(keyboardControl,
               times(1)).addKeyShortcutCallback(any(KeyShortcutCallback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRespondsToDeleteKey() {
        command.bind(session);

        verify(keyboardControl,
               times(1)).addKeyShortcutCallback(keyShortcutCallbackCaptor.capture());

        final KeyShortcutCallback keyShortcutCallback = keyShortcutCallbackCaptor.getValue();
        keyShortcutCallback.onKeyShortcut(KeyboardEvent.Key.DELETE);

        verify(command,
               times(1)).execute(any(ClientSessionCommand.Callback.class));
    }
}
