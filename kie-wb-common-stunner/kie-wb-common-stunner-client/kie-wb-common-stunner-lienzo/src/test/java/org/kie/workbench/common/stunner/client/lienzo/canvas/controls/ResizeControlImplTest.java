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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResizeControlImplTest {

    private static final String ROOT_UUID = "root-uuid1";
    private static final String ELEMENT_UUID = "element-uuid1";
    private static final String DEF_ID = "def-id";
    private static final String W_PROPERTY_ID = "w-property-id";
    private static final String H_PROPERTY_ID = "h-property-id";
    private static final String R_PROPERTY_ID = "r-property-id";
    private static final BoundsImpl GRAPH_BOUNDS = new BoundsImpl(
            new BoundImpl(1d,
                          2d),
            new BoundImpl(3000d,
                          4000d)
    );
    private static final BoundsImpl ELEMENT_BOUNDS = new BoundsImpl(
            new BoundImpl(10d,
                          20d),
            new BoundImpl(30d,
                          40d)
    );

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Layer layer;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private DefinitionSet graphContent;

    @Mock
    private Metadata metadata;

    @Mock
    private Node element;

    @Mock
    private View elementContent;

    @Mock
    private Shape<?> shape;

    @Mock
    private HasEventHandlers<ShapeViewExtStub, Object> shapeEventHandler;

    @Mock
    private HasControlPoints<ShapeViewExtStub> hasControlPoints;

    @Mock
    private Object definition;

    @Mock
    private Object wProperty;

    @Mock
    private Object hProperty;

    @Mock
    private Object rProperty;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private AdapterRegistry adapterRegistry;

    @Mock
    private DefinitionAdapter<Object> definitionAdapter;

    @Mock
    private PropertyAdapter propertyAdapter;

    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private ShapeViewExtStub shapeView;

    private ResizeControlImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.canvasCommandFactory = new DefaultCanvasCommandFactory(null, null);
        this.shapeView = new ShapeViewExtStub(shapeEventHandler,
                                              hasControlPoints);
        when(canvasHandler.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertyAdapter(anyObject())).thenReturn(propertyAdapter);
        when(definitionAdapter.getId(eq(definition))).thenReturn(DEF_ID);
        when(propertyAdapter.getId(eq(wProperty))).thenReturn(W_PROPERTY_ID);
        when(propertyAdapter.getId(eq(hProperty))).thenReturn(H_PROPERTY_ID);
        when(propertyAdapter.getId(eq(rProperty))).thenReturn(R_PROPERTY_ID);
        when(definitionAdapter.getMetaProperty(eq(PropertyMetaTypes.WIDTH),
                                               eq(definition))).thenReturn(wProperty);
        when(definitionAdapter.getMetaProperty(eq(PropertyMetaTypes.HEIGHT),
                                               eq(definition))).thenReturn(hProperty);
        when(definitionAdapter.getMetaProperty(eq(PropertyMetaTypes.RADIUS),
                                               eq(definition))).thenReturn(rProperty);
        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(definition);
        when(elementContent.getBounds()).thenReturn(ELEMENT_BOUNDS);
        when(graph.getContent()).thenReturn(graphContent);
        when(graphContent.getBounds()).thenReturn(GRAPH_BOUNDS);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getLayer()).thenReturn(layer);
        when(canvas.getShape(eq(ELEMENT_UUID))).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(shape.getShapeView()).thenReturn(shapeView);
        when(shapeEventHandler.supports(eq(ViewEventType.RESIZE))).thenReturn(true);
        this.tested = new ResizeControlImpl(canvasCommandFactory);
        tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testRegister() {
        tested.enable(canvasHandler);
        assertFalse(tested.isRegistered(element));
        tested.register(element);
        verify(shapeEventHandler,
               times(1)).supports(eq(ViewEventType.RESIZE));
        verify(shapeEventHandler,
               times(1)).addHandler(eq(ViewEventType.RESIZE),
                                    any(ResizeHandler.class));
        assertTrue(tested.isRegistered(element));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeregister() {
        tested.enable(canvasHandler);
        tested.register(element);
        tested.deregister(element);
        verify(shapeEventHandler,
               times(1)).removeHandler(any(ViewHandler.class));
        assertFalse(tested.isRegistered(element));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResize() {
        when(commandManager.execute(eq(canvasHandler),
                                    any(Command.class))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        tested.enable(canvasHandler);
        assertFalse(tested.isRegistered(element));
        tested.register(element);
        verify(shapeEventHandler,
               times(1)).supports(eq(ViewEventType.RESIZE));
        final ArgumentCaptor<ResizeHandler> resizeHandlerArgumentCaptor =
                ArgumentCaptor.forClass(ResizeHandler.class);
        verify(shapeEventHandler,
               times(1)).addHandler(eq(ViewEventType.RESIZE),
                                    resizeHandlerArgumentCaptor.capture());
        final ResizeHandler resizeHandler = resizeHandlerArgumentCaptor.getValue();
        final double x = 121.45d;
        final double y = 23.456d;
        final double width = 100d;
        final double height = 200d;
        final ResizeEvent event = new ResizeEvent(x,
                                                  y,
                                                  x,
                                                  y,
                                                  width,
                                                  height);
        resizeHandler.end(event);
        final ArgumentCaptor<Command> commandArgumentCaptor =
                ArgumentCaptor.forClass(Command.class);
        verify(commandManager,
               times(1)).execute(eq(canvasHandler),
                                 commandArgumentCaptor.capture());
        final Command command = commandArgumentCaptor.getValue();
        assertNotNull(command);
        assertTrue(command instanceof AbstractCompositeCommand);
        final List commands = ((AbstractCompositeCommand) command).getCommands();
        assertNotNull(commands);
        assertEquals(4,
                     commands.size());
        assertTrue(commands.get(0) instanceof UpdateElementPositionCommand);
        final UpdateElementPositionCommand positionCommand = (UpdateElementPositionCommand) commands.get(0);
        assertEquals(element,
                     positionCommand.getElement());
        assertEquals(x,
                     positionCommand.getX(),
                     0d);
        assertEquals(y,
                     positionCommand.getY(),
                     0d);
        assertTrue(commands.get(1) instanceof UpdateElementPropertyCommand);
        final UpdateElementPropertyCommand wPropertyCommand = (UpdateElementPropertyCommand) commands.get(1);
        assertEquals(element,
                     wPropertyCommand.getElement());
        assertEquals(W_PROPERTY_ID,
                     wPropertyCommand.getPropertyId());
        assertEquals(width,
                     wPropertyCommand.getValue());
        assertTrue(commands.get(2) instanceof UpdateElementPropertyCommand);
        final UpdateElementPropertyCommand hPropertyCommand = (UpdateElementPropertyCommand) commands.get(2);
        assertEquals(element,
                     hPropertyCommand.getElement());
        assertEquals(H_PROPERTY_ID,
                     hPropertyCommand.getPropertyId());
        assertEquals(height,
                     hPropertyCommand.getValue());
        assertTrue(commands.get(3) instanceof UpdateElementPropertyCommand);
        final UpdateElementPropertyCommand rPropertyCommand = (UpdateElementPropertyCommand) commands.get(3);
        assertEquals(element,
                     rPropertyCommand.getElement());
        assertEquals(R_PROPERTY_ID,
                     rPropertyCommand.getPropertyId());
        assertEquals(50d,
                     rPropertyCommand.getValue());
    }
}
