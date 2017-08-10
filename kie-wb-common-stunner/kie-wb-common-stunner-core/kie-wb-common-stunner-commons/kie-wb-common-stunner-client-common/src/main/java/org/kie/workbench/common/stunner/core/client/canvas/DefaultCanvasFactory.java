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

package org.kie.workbench.common.stunner.core.client.canvas;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;

/**
 * The Stunner's @Default Canvas Factory implementation.
 * It produces:
 * - the @Default canvas type resolved for <code>AbstractCanvas</code>
 * - the @Default canvas handler type resolved for <code>AbstractCanvasHandler</code>
 * - the @Default canvas control types resolved for the types specified in the
 * singleton <code>CONTROL_TYPES</code> internal map.
 */
@ApplicationScoped
@Default
public class DefaultCanvasFactory
        extends AbstractCanvasFactory<DefaultCanvasFactory> {

    private final ManagedInstance<ResizeControl> resizeControls;
    private final ManagedInstance<ConnectionAcceptorControl> connectionAcceptorControls;
    private final ManagedInstance<ContainmentAcceptorControl> containmentAcceptorControls;
    private final ManagedInstance<DockingAcceptorControl> dockingAcceptorControls;
    private final ManagedInstance<CanvasInPlaceTextEditorControl> inPlaceTextEditorControls;
    private final ManagedInstance<SelectionControl> selectionControls;
    private final ManagedInstance<DragControl> dragControls;
    private final ManagedInstance<ToolboxControl> toolboxControls;
    private final ManagedInstance<ElementBuilderControl> elementBuilderControls;
    private final ManagedInstance<NodeBuilderControl> nodeBuilderControls;
    private final ManagedInstance<EdgeBuilderControl> edgeBuilderControls;
    private final ManagedInstance<ZoomControl> zoomControls;
    private final ManagedInstance<PanControl> panControls;
    private final ManagedInstance<AbstractCanvas> canvasInstances;
    private final ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances;

    // Required by CDI proxies.
    protected DefaultCanvasFactory() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public DefaultCanvasFactory(final ManagedInstance<ResizeControl> resizeControls,
                                final ManagedInstance<ConnectionAcceptorControl> connectionAcceptorControls,
                                final ManagedInstance<ContainmentAcceptorControl> containmentAcceptorControls,
                                final ManagedInstance<DockingAcceptorControl> dockingAcceptorControls,
                                final ManagedInstance<CanvasInPlaceTextEditorControl> inPlaceTextEditorControls,
                                final ManagedInstance<SelectionControl> selectionControls,
                                final ManagedInstance<DragControl> dragControls,
                                final @Default ManagedInstance<ToolboxControl> toolboxControls,
                                final @Default @Observer ManagedInstance<ElementBuilderControl> elementBuilderControls,
                                final ManagedInstance<NodeBuilderControl> nodeBuilderControls,
                                final ManagedInstance<EdgeBuilderControl> edgeBuilderControls,
                                final ManagedInstance<ZoomControl> zoomControls,
                                final ManagedInstance<PanControl> panControls,
                                final @Default ManagedInstance<AbstractCanvas> canvasInstances,
                                final @Default ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances) {
        this.resizeControls = resizeControls;
        this.connectionAcceptorControls = connectionAcceptorControls;
        this.containmentAcceptorControls = containmentAcceptorControls;
        this.dockingAcceptorControls = dockingAcceptorControls;
        this.inPlaceTextEditorControls = inPlaceTextEditorControls;
        this.selectionControls = selectionControls;
        this.dragControls = dragControls;
        this.toolboxControls = toolboxControls;
        this.elementBuilderControls = elementBuilderControls;
        this.nodeBuilderControls = nodeBuilderControls;
        this.edgeBuilderControls = edgeBuilderControls;
        this.zoomControls = zoomControls;
        this.panControls = panControls;
        this.canvasInstances = canvasInstances;
        this.canvasHandlerInstances = canvasHandlerInstances;
    }

    @PostConstruct
    public void init() {
        this
                .register(ResizeControl.class,
                          resizeControls)
                .register(ConnectionAcceptorControl.class,
                          connectionAcceptorControls)
                .register(ContainmentAcceptorControl.class,
                          containmentAcceptorControls)
                .register(DockingAcceptorControl.class,
                          dockingAcceptorControls)
                .register(CanvasInPlaceTextEditorControl.class,
                          inPlaceTextEditorControls)
                .register(SelectionControl.class,
                          selectionControls)
                .register(DragControl.class,
                          dragControls)
                .register(ToolboxControl.class,
                          toolboxControls)
                .register(ElementBuilderControl.class,
                          elementBuilderControls)
                .register(NodeBuilderControl.class,
                          nodeBuilderControls)
                .register(EdgeBuilderControl.class,
                          edgeBuilderControls)
                .register(ZoomControl.class,
                          zoomControls)
                .register(PanControl.class,
                          panControls);
    }

    @Override
    public AbstractCanvas newCanvas() {
        return canvasInstances.get();
    }

    @Override
    public AbstractCanvasHandler newCanvasHandler() {
        return canvasHandlerInstances.get();
    }
}
