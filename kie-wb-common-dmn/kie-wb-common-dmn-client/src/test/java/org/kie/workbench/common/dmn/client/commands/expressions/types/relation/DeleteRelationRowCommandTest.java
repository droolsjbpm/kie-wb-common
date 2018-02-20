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

package org.kie.workbench.common.dmn.client.commands.expressions.types.relation;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.List;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeleteRelationRowCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private RelationColumn uiModelColumn;

    @Mock
    private ListSelector listSelector;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private GraphCommandExecutionContext gce;

    @Mock
    private RuleManager ruleManager;

    private Relation relation;

    private GridData uiModel;

    private RelationUIModelMapper uiModelMapper;

    private DeleteRelationRowCommand command;

    @Before
    public void setup() {
        this.relation = new Relation();
        this.relation.getRow().add(new List());
        this.uiModel = new BaseGridData();
        this.uiModel.appendRow(new DMNGridRow());
        this.uiModel.appendColumn(uiRowNumberColumn);

        this.uiModelMapper = new RelationUIModelMapper(() -> uiModel,
                                                       () -> Optional.of(relation),
                                                       listSelector);

        this.command = spy(new DeleteRelationRowCommand(relation,
                                                        uiModel,
                                                        0,
                                                        uiModelMapper,
                                                        canvasOperation));
        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiModelColumn).getIndex();
    }

    @Test
    public void testGraphCommandAllow() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecuteWithColumns() {
        relation.getColumn().add(new InformationItem());
        relation.getRow().get(0).getExpression().add(new LiteralExpression());

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(0,
                     relation.getRow().size());
        assertEquals(1,
                     relation.getColumn().size());
    }

    @Test
    public void testGraphCommandExecuteWithNoColumns() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(0,
                     relation.getRow().size());
    }

    @Test
    public void testGraphCommandUndoWithColumns() {
        relation.getColumn().add(new InformationItem());
        relation.getRow().get(0).getExpression().add(new LiteralExpression());
        ((LiteralExpression) relation.getRow().get(0).getExpression().get(0)).setText("value");

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Delete row and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(1,
                     relation.getColumn().size());
        assertEquals(1,
                     relation.getRow().size());
        assertEquals(1,
                     relation.getRow().get(0).getExpression().size());
        assertEquals("value",
                     ((LiteralExpression) relation.getRow().get(0).getExpression().get(0)).getText());
    }

    @Test
    public void testGraphCommandUndoWithNoColumns() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Delete row and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(0,
                     relation.getColumn().size());
        assertEquals(1,
                     relation.getRow().size());
    }

    @Test
    public void testCanvasCommandAllow() {
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecuteWithColumns() {
        uiModel.appendColumn(uiModelColumn);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(0,
                     uiModel.getRowCount());
        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(1));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteWithNoColumns() {
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(0,
                     uiModel.getRowCount());
        assertEquals(1,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithColumns() {
        relation.getColumn().add(new InformationItem());
        relation.getRow().get(0).getExpression().add(new LiteralExpression());
        ((LiteralExpression) relation.getRow().get(0).getExpression().get(0)).setText("value");
        uiModel.appendColumn(uiModelColumn);
        uiModelMapper.fromDMNModel(0, 0);
        uiModelMapper.fromDMNModel(0, 1);

        //Delete row and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(1));
        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals("value",
                     uiModel.getCell(0, 1).getValue().getValue());

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithNoColumns() {
        //Delete row and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(1,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }
}
