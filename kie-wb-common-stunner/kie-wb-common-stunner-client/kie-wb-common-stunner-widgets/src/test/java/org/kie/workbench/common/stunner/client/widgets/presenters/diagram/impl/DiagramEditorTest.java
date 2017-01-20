/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.AbstractCanvasHandlerViewerTest;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasValidationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DiagramEditorTest extends AbstractCanvasHandlerViewerTest {

    private final static Bounds GRAPH_BOUNDS = new BoundsImpl(new BoundImpl(0d,
                                                                            0d),
                                                              new BoundImpl(100d,
                                                                            100d));

    @Mock
    DiagramViewer viewer;
    @Mock
    CanvasCommandManager<AbstractCanvasHandler> commandManager;
    @Mock
    CanvasValidationControl<AbstractCanvasHandler> validationControl;
    @Mock
    ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl;
    @Mock
    ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl;
    @Mock
    DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl;
    @Mock
    DragControl<AbstractCanvasHandler, Element> dragControl;
    @Mock
    DiagramViewer.DiagramViewerCallback<Diagram> callback;

    private DiagramEditorImpl<Diagram, AbstractCanvasHandler> tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        when(viewer.getHandler()).thenReturn(canvasHandler);
        doAnswer(invocationOnMock -> {
            when(viewer.getInstance()).thenReturn(diagram);
            final DiagramViewer.DiagramViewerCallback c = (DiagramViewer.DiagramViewerCallback) invocationOnMock.getArguments()[1];
            c.afterCanvasInitialized();
            c.onSuccess();
            return diagram;
        }).when(viewer).open(any(Diagram.class),
                             any(DiagramViewer.DiagramViewerCallback.class));
        doAnswer(invocationOnMock -> {
            when(viewer.getInstance()).thenReturn(null);
            return null;
        }).when(viewer).clear();
        doAnswer(invocationOnMock -> {
            when(viewer.getInstance()).thenReturn(null);
            return null;
        }).when(viewer).destroy();
        this.tested =
                new DiagramEditorImpl<Diagram, AbstractCanvasHandler>(viewer,
                                                                      commandManager,
                                                                      validationControl,
                                                                      connectionAcceptorControl,
                                                                      containmentAcceptorControl,
                                                                      dockingAcceptorControl,
                                                                      dragControl);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpen() {
        tested.open(diagram,
                    callback);
        assertEquals(diagram,
                     tested.getInstance());
        verify(viewer,
               times(1)).open(eq(diagram),
                              any(DiagramViewer.DiagramViewerCallback.class));
        verify(validationControl,
               times(1)).enable(eq(canvasHandler));
        verify(connectionAcceptorControl,
               times(1)).enable(eq(canvasHandler));
        verify(containmentAcceptorControl,
               times(1)).enable(eq(canvasHandler));
        verify(dockingAcceptorControl,
               times(1)).enable(eq(canvasHandler));
        verify(dragControl,
               times(1)).enable(eq(canvasHandler));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScale() {
        tested.open(diagram,
                    callback);
        tested.scale(50,
                     50);
        assertEquals(diagram,
                     tested.getInstance());
        verify(viewer,
               times(1)).scale(50,
                               50);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        tested.open(diagram,
                    callback);
        tested.clear();
        assertNull(tested.getInstance());
        verify(viewer,
               times(1)).clear();
        verify(validationControl,
               times(1)).disable();
        verify(connectionAcceptorControl,
               times(1)).disable();
        verify(containmentAcceptorControl,
               times(1)).disable();
        verify(dockingAcceptorControl,
               times(1)).disable();
        verify(dragControl,
               times(1)).disable();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.open(diagram,
                    callback);
        tested.destroy();
        assertNull(tested.getInstance());
        verify(viewer,
               times(1)).destroy();
        verify(validationControl,
               times(1)).disable();
        verify(connectionAcceptorControl,
               times(1)).disable();
        verify(containmentAcceptorControl,
               times(1)).disable();
        verify(dockingAcceptorControl,
               times(1)).disable();
        verify(dragControl,
               times(1)).disable();
    }
}
