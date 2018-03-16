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

package org.kie.workbench.common.dmn.client.commands.expressions.types.dtable;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DescriptionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeleteOutputClauseCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private OutputClauseColumn uiOutputClauseColumn;

    @Mock
    private DescriptionColumn uiDescriptionColumn;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    private DecisionTable dtable;

    private OutputClause outputClause;

    private GridData uiModel;

    private DecisionTableUIModelMapper uiModelMapper;

    private DeleteOutputClauseCommand command;

    @Before
    public void setup() {
        this.dtable = new DecisionTable();
        this.outputClause = new OutputClause();
        this.dtable.getOutput().add(outputClause);

        this.uiModel = new DMNGridData();
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiOutputClauseColumn);
        this.uiModel.appendColumn(uiDescriptionColumn);

        this.uiModelMapper = new DecisionTableUIModelMapper(() -> uiModel,
                                                            () -> Optional.of(dtable),
                                                            listSelector);

        this.command = new DeleteOutputClauseCommand(dtable,
                                                     uiModel,
                                                     DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT + dtable.getInput().size(),
                                                     uiModelMapper,
                                                     canvasOperation);

        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiOutputClauseColumn).getIndex();
        doReturn(2).when(uiDescriptionColumn).getIndex();
    }

    @Test
    public void testGraphCommandAllow() throws Exception {
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandCheck() throws Exception {
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandExecute() {
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));
        assertEquals(0,
                     dtable.getOutput().size());
    }

    @Test
    public void testGraphCommandExecuteAndThenUndo() {
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);

        assertEquals(0,
                     dtable.getOutput().size());

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));

        assertEquals(1,
                     dtable.getOutput().size());
        assertEquals(outputClause,
                     dtable.getOutput().get(0));
    }

    @Test
    public void testCanvasCommandAllow() throws Exception {
        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.allow(canvasHandler));
    }

    @Test
    public void testCanvasCommandExecute() throws Exception {
        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddRuleCommand = command.newCanvasCommand(canvasHandler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddRuleCommand.execute(canvasHandler));

        assertThat(uiModel.getColumns()).containsOnly(uiRowNumberColumn,
                                                      uiDescriptionColumn);

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteAndThenUndo() throws Exception {
        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddRuleCommand = command.newCanvasCommand(canvasHandler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddRuleCommand.execute(canvasHandler));

        assertThat(uiModel.getColumns()).containsOnly(uiRowNumberColumn,
                                                      uiDescriptionColumn);

        reset(canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddRuleCommand.undo(canvasHandler));

        assertThat(uiModel.getColumns()).containsOnly(uiRowNumberColumn,
                                                      uiOutputClauseColumn,
                                                      uiDescriptionColumn);

        verify(canvasOperation).execute();
    }
}
