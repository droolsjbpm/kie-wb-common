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

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHeaderValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHeaderValueCommand;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderGridWidgetMouseDoubleClickHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.HasExpressionEditorControls;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

public abstract class BaseExpressionGrid<E extends Expression, M extends BaseUIModelMapper<E>> extends BaseGridWidget implements HasExpressionEditorControls {

    protected final GridCellTuple parent;

    protected final HasExpression hasExpression;
    protected final Optional<E> expression;
    protected final Optional<HasName> hasName;

    protected final DMNGridPanel gridPanel;
    protected final DMNGridLayer gridLayer;
    protected final SessionManager sessionManager;
    protected final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    protected final Event<ExpressionEditorSelectedEvent> editorSelectedEvent;
    protected final CellEditorControls cellEditorControls;

    protected M uiModelMapper;

    private final Supplier<Boolean> isHeaderHidden;

    public BaseExpressionGrid(final GridCellTuple parent,
                              final HasExpression hasExpression,
                              final Optional<E> expression,
                              final Optional<HasName> hasName,
                              final DMNGridPanel gridPanel,
                              final DMNGridLayer gridLayer,
                              final GridRenderer gridRenderer,
                              final SessionManager sessionManager,
                              final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                              final CellEditorControls cellEditorControls,
                              final boolean isHeaderHidden) {
        this(parent,
             hasExpression,
             expression,
             hasName,
             gridPanel,
             gridLayer,
             gridRenderer,
             sessionManager,
             sessionCommandManager,
             editorSelectedEvent,
             cellEditorControls,
             () -> isHeaderHidden);
    }

    public BaseExpressionGrid(final GridCellTuple parent,
                              final HasExpression hasExpression,
                              final Optional<E> expression,
                              final Optional<HasName> hasName,
                              final DMNGridPanel gridPanel,
                              final DMNGridLayer gridLayer,
                              final GridData gridData,
                              final GridRenderer gridRenderer,
                              final SessionManager sessionManager,
                              final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                              final CellEditorControls cellEditorControls,
                              final boolean isHeaderHidden) {
        this(parent,
             hasExpression,
             expression,
             hasName,
             gridPanel,
             gridLayer,
             gridData,
             gridRenderer,
             sessionManager,
             sessionCommandManager,
             editorSelectedEvent,
             cellEditorControls,
             () -> isHeaderHidden);
    }

    // Constructor used for Unit Testing with a Supplier<Boolean> for isHeaderHidden
    BaseExpressionGrid(final GridCellTuple parent,
                       final HasExpression hasExpression,
                       final Optional<E> expression,
                       final Optional<HasName> hasName,
                       final DMNGridPanel gridPanel,
                       final DMNGridLayer gridLayer,
                       final GridRenderer gridRenderer,
                       final SessionManager sessionManager,
                       final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                       final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                       final CellEditorControls cellEditorControls,
                       final Supplier<Boolean> isHeaderHidden) {
        this(parent,
             hasExpression,
             expression,
             hasName,
             gridPanel,
             gridLayer,
             new DMNGridData(gridLayer),
             gridRenderer,
             sessionManager,
             sessionCommandManager,
             editorSelectedEvent,
             cellEditorControls,
             isHeaderHidden);
    }

    BaseExpressionGrid(final GridCellTuple parent,
                       final HasExpression hasExpression,
                       final Optional<E> expression,
                       final Optional<HasName> hasName,
                       final DMNGridPanel gridPanel,
                       final DMNGridLayer gridLayer,
                       final GridData gridData,
                       final GridRenderer gridRenderer,
                       final SessionManager sessionManager,
                       final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                       final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                       final CellEditorControls cellEditorControls,
                       final Supplier<Boolean> isHeaderHidden) {
        super(gridData,
              gridLayer,
              gridLayer,
              gridRenderer);
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.parent = parent;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.editorSelectedEvent = editorSelectedEvent;
        this.cellEditorControls = cellEditorControls;

        this.hasExpression = hasExpression;
        this.expression = expression;
        this.hasName = hasName;
        this.isHeaderHidden = isHeaderHidden;

        doInitialisation();
    }

    protected void doInitialisation() {
        this.uiModelMapper = makeUiModelMapper();

        initialiseUiColumns();
        initialiseUiModel();
    }

    protected abstract M makeUiModelMapper();

    protected abstract void initialiseUiColumns();

    protected abstract void initialiseUiModel();

    protected Function<GridCellTuple, AbstractCanvasGraphCommand> newCellHasNoValueCommand() {
        return (gc) -> new DeleteCellValueCommand(gc,
                                                  () -> uiModelMapper,
                                                  gridLayer::batch);
    }

    protected Function<GridCellValueTuple, AbstractCanvasGraphCommand> newCellHasValueCommand() {
        return (gcv) -> new SetCellValueCommand(gcv,
                                                () -> uiModelMapper,
                                                gridLayer::batch);
    }

    protected Function<GridCellTuple, AbstractCanvasGraphCommand> newHeaderHasNoValueCommand() {
        return (gc) -> new DeleteHeaderValueCommand(extractEditableHeaderMetaData(gc),
                                                    gridLayer::batch);
    }

    protected Function<GridCellValueTuple, AbstractCanvasGraphCommand> newHeaderHasValueCommand() {
        return (gcv) -> {
            final String title = gcv.getValue().getValue().toString();
            return new SetHeaderValueCommand(title,
                                             extractEditableHeaderMetaData(gcv),
                                             gridLayer::batch);
        };
    }

    protected EditableHeaderMetaData extractEditableHeaderMetaData(final GridCellTuple gc) {
        final int headerRowIndex = gc.getRowIndex();
        final int headerColumnIndex = gc.getColumnIndex();
        final GridColumn.HeaderMetaData headerMetaData = uiModelMapper.getUiModel().get()
                .getColumns().get(headerColumnIndex)
                .getHeaderMetaData().get(headerRowIndex);
        if (headerMetaData instanceof EditableHeaderMetaData) {
            return (EditableHeaderMetaData) headerMetaData;
        }
        throw new IllegalArgumentException("Header (" + headerColumnIndex + ", " + headerRowIndex + ") was not an instanceof EditableHeaderMetaData");
    }

    @Override
    protected NodeMouseDoubleClickHandler getGridMouseDoubleClickHandler(final GridSelectionManager selectionManager,
                                                                         final GridPinnedModeManager pinnedModeManager) {
        return new EditableHeaderGridWidgetMouseDoubleClickHandler(this,
                                                                   selectionManager,
                                                                   pinnedModeManager,
                                                                   renderer);
    }

    @Override
    public boolean onDragHandle(final INodeXYEvent event) {
        return false;
    }

    @Override
    public Viewport getViewport() {
        // A GridWidget's Viewport may not have been set IF the grid has not been attached to a Layer.
        // This is possible when a nested Expression Editor is on a newly created non-visible row as the
        // GridRenderer ignores rows/cells outside of the Layer's visible extents.
        Viewport viewport = super.getViewport();
        if (viewport == null) {
            viewport = gridLayer.getViewport();
        }
        return viewport;
    }

    @Override
    public Layer getLayer() {
        // A GridWidget's Layer may not have been set IF the grid has not been attached to a Layer.
        // This is possible when a nested Expression Editor is on a newly created non-visible row as the
        // GridRenderer ignores rows/cells outside of the Layer's visible extents.
        Layer layer = super.getLayer();
        if (layer == null) {
            layer = gridLayer;
        }
        return layer;
    }

    @Override
    public void select() {
        fireExpressionEditorSelectedEvent();
        super.select();
    }

    protected void fireExpressionEditorSelectedEvent() {
        editorSelectedEvent.fire(new ExpressionEditorSelectedEvent(sessionManager.getCurrentSession(),
                                                                   Optional.of(this)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean selectCell(final Point2D ap,
                              final boolean isShiftKeyDown,
                              final boolean isControlKeyDown) {
        final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(this,
                                                                     ap.getY());
        final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex(this,
                                                                           ap.getX());

        if (!(uiRowIndex == null || uiColumnIndex == null)) {
            final GridCell<?> cell = getModel().getCell(uiRowIndex, uiColumnIndex);
            if (cell instanceof HasCellEditorControls) {
                final HasCellEditorControls hasControls = (HasCellEditorControls) cell;
                final Optional<HasCellEditorControls.Editor> editor = hasControls.getEditor();
                editor.ifPresent(e -> {
                    e.bind(this);
                    cellEditorControls.show(e,
                                            (int) (ap.getX() + getAbsoluteX()),
                                            (int) (ap.getY() + getAbsoluteY()));
                });
            }
        }

        return super.selectCell(ap,
                                isShiftKeyDown,
                                isControlKeyDown);
    }

    @Override
    protected void executeRenderQueueCommands() {
        final List<Pair<Group, GridRenderer.RendererCommand>> gridLineCommands = new ArrayList<>();
        final List<Pair<Group, GridRenderer.RendererCommand>> allOtherCommands = new ArrayList<>();
        final List<Pair<Group, GridRenderer.RendererCommand>> selectedCellsCommands = new ArrayList<>();
        for (Pair<Group, List<GridRenderer.RendererCommand>> p : renderQueue) {
            final Group parent = p.getK1();
            final List<GridRenderer.RendererCommand> commands = p.getK2();
            for (GridRenderer.RendererCommand command : commands) {
                if (command instanceof GridRenderer.RenderSelectedCellsCommand) {
                    selectedCellsCommands.add(new Pair<>(parent, command));
                } else if (command instanceof GridRenderer.RenderHeaderGridLinesCommand) {
                    gridLineCommands.add(new Pair<>(parent, command));
                } else if (command instanceof GridRenderer.RenderBodyGridLinesCommand) {
                    gridLineCommands.add(new Pair<>(parent, command));
                } else {
                    allOtherCommands.add(new Pair<>(parent, command));
                }
            }
        }

        final Predicate<Pair<Group, GridRenderer.RendererCommand>> renderHeader = (p) -> {
            final GridRenderer.RendererCommand command = p.getK2();
            if (command instanceof GridRenderer.RenderGridBoundaryCommand) {
                return false;
            } else if (isHeaderHidden.get()) {
                return !(command instanceof GridRenderer.RendererHeaderCommand);
            }
            return true;
        };

        allOtherCommands.stream().filter(renderHeader).forEach(p -> p.getK2().execute(p.getK1()));
        gridLineCommands.stream().filter(renderHeader).forEach(p -> p.getK2().execute(p.getK1()));
        selectedCellsCommands.stream().filter(renderHeader).forEach(p -> p.getK2().execute(p.getK1()));
    }

    public GridCellTuple getParentInformation() {
        return parent;
    }

    public Optional<E> getExpression() {
        return expression;
    }

    public double getMinimumWidth() {
        double minimumWidth = 0;
        final int columnCount = model.getColumnCount();
        final List<GridColumn<?>> uiColumns = model.getColumns();
        for (int columnIndex = 0; columnIndex < columnCount - 1; columnIndex++) {
            final GridColumn editorColumn = uiColumns.get(columnIndex);
            minimumWidth = minimumWidth + editorColumn.getWidth();
        }
        if (columnCount > 0) {
            minimumWidth = minimumWidth + uiColumns.get(columnCount - 1).getMinimumWidth();
        }
        return minimumWidth;
    }

    protected void synchroniseViewWhenExpressionEditorChanged(final Optional<BaseExpressionGrid> oEditor) {
        parent.onResize();
        gridPanel.refreshScrollPosition();
        gridPanel.updatePanelSize();

        oEditor.ifPresent(BaseExpressionGrid::selectFirstCell);

        gridLayer.batch(new GridLayerRedrawManager.PrioritizedCommand(0) {
            @Override
            public void execute() {
                gridLayer.draw();
                gridLayer.select(oEditor.get());
            }
        });
    }

    public void selectFirstCell() {
        final GridData uiModel = getModel();
        if (uiModel.getRowCount() == 0 || uiModel.getColumnCount() == 0) {
            gridLayer.clearAllSelections();
        }
        uiModel.getColumns()
                .stream()
                .filter(c -> !(c instanceof RowNumberColumn))
                .map(c -> uiModel.getColumns().indexOf(c))
                .findFirst()
                .ifPresent(index -> uiModel.selectCell(0, index));
    }
}
