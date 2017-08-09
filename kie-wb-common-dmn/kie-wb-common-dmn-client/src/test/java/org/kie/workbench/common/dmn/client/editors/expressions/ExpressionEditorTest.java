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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.enterprise.inject.Instance;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.commands.SetExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearStatesToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.DeleteSelectionToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.RedoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ToolbarCommandFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.UndoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ValidateToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.AbstractToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.MockInstanceImpl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExpressionEditorTest {

    @Mock
    private ExpressionEditorView view;

    @Mock
    private SessionPresenter<AbstractClientFullSession, ?, Diagram> sessionPresenter;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private BaseExpressionEditorView.Editor<Expression> undefinedExpressionEditor;

    @Mock
    private IsElement undefinedExpressionEditorView;

    @Mock
    private BaseExpressionEditorView.Editor<LiteralExpression> literalExpressionEditor;

    @Mock
    private IsElement literalExpressionEditorView;

    @Mock
    private ToolbarCommandFactory toolbarCommandFactory;

    @Mock
    private ManagedInstance<AbstractToolbarItem<AbstractClientFullSession>> toolbarItemInstances;

    @Mock
    private AbstractToolbarItem toolbarItem;

    @Mock
    private ManagedInstance<ToolbarView<AbstractToolbar>> toolbarViewInstances;

    @Mock
    private ToolbarView toolbarView;

    @Mock
    private VisitGraphToolbarCommand visitGraphToolbarCommand;

    @Mock
    private ClearToolbarCommand clearToolbarCommand;

    @Mock
    private ClearStatesToolbarCommand clearStatesToolbarCommand;

    @Mock
    private DeleteSelectionToolbarCommand deleteSelectionToolbarCommand;

    @Mock
    private SwitchGridToolbarCommand switchGridToolbarCommand;

    @Mock
    private UndoToolbarCommand undoToolbarCommand;

    @Mock
    private RedoToolbarCommand redoToolbarCommand;

    @Mock
    private ValidateToolbarCommand validateToolbarCommand;

    @Mock
    private ExportToPngToolbarCommand exportToPngToolbarCommand;

    @Mock
    private ExportToJpgToolbarCommand exportToJpgToolbarCommand;

    @Mock
    private ExportToPdfToolbarCommand exportToPdfToolbarCommand;

    @Mock
    private HasExpression hasExpression;

    @Captor
    private ArgumentCaptor<List<ExpressionType<Expression>>> expressionTypesArgumentCaptor;

    @Captor
    private ArgumentCaptor<SetExpressionTypeCommand> setExpressionTypeCommandArgumentCaptor;

    @Captor
    private ArgumentCaptor<Optional<Expression>> setExpressionArgumentCaptor;

    private ExpressionEditor editor;

    private EditorToolbar editorToolbar;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        //These are deliberately added in reverse order to test sorting before passing to the View
        final Instance<ExpressionType> expressionTypeBeans = new MockInstanceImpl<>(new ExpressionEditorType<>(1,
                                                                                                               LiteralExpression.class.getName(),
                                                                                                               Optional.of(new LiteralExpression()),
                                                                                                               literalExpressionEditor),
                                                                                    new ExpressionEditorType<>(0,
                                                                                                               Expression.class.getName(),
                                                                                                               Optional.empty(),
                                                                                                               undefinedExpressionEditor));
        editor = spy(new ExpressionEditor(view,
                                          sessionManager,
                                          sessionCommandManager,
                                          expressionTypeBeans));

        when(toolbarItemInstances.get()).thenReturn(toolbarItem);
        when(toolbarViewInstances.get()).thenReturn(toolbarView);
        when(toolbarCommandFactory.newVisitGraphCommand()).thenReturn(visitGraphToolbarCommand);
        when(toolbarCommandFactory.newClearCommand()).thenReturn(clearToolbarCommand);
        when(toolbarCommandFactory.newClearStatesCommand()).thenReturn(clearStatesToolbarCommand);
        when(toolbarCommandFactory.newDeleteSelectedElementsCommand()).thenReturn(deleteSelectionToolbarCommand);
        when(toolbarCommandFactory.newSwitchGridCommand()).thenReturn(switchGridToolbarCommand);
        when(toolbarCommandFactory.newUndoCommand()).thenReturn(undoToolbarCommand);
        when(toolbarCommandFactory.newRedoCommand()).thenReturn(redoToolbarCommand);
        when(toolbarCommandFactory.newValidateCommand()).thenReturn(validateToolbarCommand);
        when(toolbarCommandFactory.newExportToPngToolbarCommand()).thenReturn(exportToPngToolbarCommand);
        when(toolbarCommandFactory.newExportToJpgToolbarCommand()).thenReturn(exportToJpgToolbarCommand);
        when(toolbarCommandFactory.newExportToPdfToolbarCommand()).thenReturn(exportToPdfToolbarCommand);
        when(toolbarItem.getUUID()).thenReturn("uuid");

        final EditorToolbarFactory editorToolbarFactory = new EditorToolbarFactory(toolbarCommandFactory,
                                                                                   toolbarItemInstances,
                                                                                   toolbarViewInstances);
        editorToolbar = spy((EditorToolbar) editorToolbarFactory.build(mock(AbstractClientFullSession.class)));

        when(sessionPresenter.getToolbar()).thenReturn(editorToolbar);
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(undefinedExpressionEditor.getView()).thenReturn(undefinedExpressionEditorView);
        when(literalExpressionEditor.getView()).thenReturn(literalExpressionEditorView);

        editor.init(sessionPresenter);
    }

    @Test
    public void checkViewIsPopulatedWithOrderedExpressionTypes() {
        verify(view).setExpressionTypes(expressionTypesArgumentCaptor.capture());

        final List<ExpressionType<Expression>> expressionTypes = expressionTypesArgumentCaptor.getValue();
        assertEquals(2,
                     expressionTypes.size());
        assertEquals(0,
                     expressionTypes.get(0).getIndex());
        assertEquals(undefinedExpressionEditor,
                     expressionTypes.get(0).getEditor());
        assertEquals(1,
                     expressionTypes.get(1).getIndex());
        assertEquals(literalExpressionEditor,
                     expressionTypes.get(1).getEditor());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkSetHasExpressionStoresCurrentToolbarState() {
        //Stub setExpression method for this test. It is covered in other tests.
        doNothing().when(editor).setExpression(any(Optional.class));

        //All Toolbar items are enabled
        when(editorToolbar.isEnabled((ToolbarCommand) visitGraphToolbarCommand)).thenReturn(true);

        editor.setHasExpression(hasExpression);

        final ExpressionEditor.ToolbarCommandStateHandler handler = editor.getToolbarCommandStateHandler();

        //Check saved state
        assertTrue(handler.visitGraphToolbarCommandEnabled);
        assertTrue(handler.clearToolbarCommandEnabled);
        assertTrue(handler.clearStatesToolbarCommandEnabled);
        assertTrue(handler.deleteSelectionToolbarCommandEnabled);
        assertTrue(handler.switchGridToolbarCommandEnabled);
        assertTrue(handler.undoToolbarCommandEnabled);
        assertTrue(handler.redoToolbarCommandEnabled);
        assertTrue(handler.validateToolbarCommandEnabled);
        assertTrue(handler.exportToPngToolbarCommandEnabled);
        assertTrue(handler.exportToJpgToolbarCommandEnabled);
        assertTrue(handler.exportToPdfToolbarCommandEnabled);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkSetHasExpressionDisablesToolbar() {
        //Stub setExpression method for this test. It is covered in other tests.
        doNothing().when(editor).setExpression(any(Optional.class));

        //All Toolbar items are enabled
        when(editorToolbar.isEnabled((ToolbarCommand) visitGraphToolbarCommand)).thenReturn(true);

        editor.setHasExpression(hasExpression);

        //Check all Toolbar items have been disabled
        verify(editorToolbar).disable(eq((ToolbarCommand) visitGraphToolbarCommand));
        verify(editorToolbar).disable(eq((ToolbarCommand) clearToolbarCommand));
        verify(editorToolbar).disable(eq((ToolbarCommand) clearStatesToolbarCommand));
        verify(editorToolbar).disable(eq((ToolbarCommand) deleteSelectionToolbarCommand));
        verify(editorToolbar).disable(eq((ToolbarCommand) switchGridToolbarCommand));
        verify(editorToolbar).disable(eq((ToolbarCommand) undoToolbarCommand));
        verify(editorToolbar).disable(eq((ToolbarCommand) redoToolbarCommand));
        verify(editorToolbar).disable(eq(validateToolbarCommand));
        verify(editorToolbar).disable(eq((ToolbarCommand) exportToPngToolbarCommand));
        verify(editorToolbar).disable(eq((ToolbarCommand) exportToJpgToolbarCommand));
        verify(editorToolbar).disable(eq((ToolbarCommand) exportToPdfToolbarCommand));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkExitRestoresToolbarState() {
        //Stub setExpression method for this test. It is covered in other tests.
        doNothing().when(editor).setExpression(any(Optional.class));

        //All Toolbar items are enabled
        when(editorToolbar.isEnabled((ToolbarCommand) visitGraphToolbarCommand)).thenReturn(true);

        editor.setHasExpression(hasExpression);

        editor.setExitCommand(() -> {/*Nothing*/});
        editor.exit();

        //Check all Toolbar items have been disabled
        verify(editorToolbar).enable(eq((ToolbarCommand) visitGraphToolbarCommand));
        verify(editorToolbar).enable(eq((ToolbarCommand) clearToolbarCommand));
        verify(editorToolbar).enable(eq((ToolbarCommand) clearStatesToolbarCommand));
        verify(editorToolbar).enable(eq((ToolbarCommand) deleteSelectionToolbarCommand));
        verify(editorToolbar).enable(eq((ToolbarCommand) switchGridToolbarCommand));
        verify(editorToolbar).enable(eq((ToolbarCommand) undoToolbarCommand));
        verify(editorToolbar).enable(eq((ToolbarCommand) redoToolbarCommand));
        verify(editorToolbar).enable(eq(validateToolbarCommand));
        verify(editorToolbar).enable(eq((ToolbarCommand) exportToPngToolbarCommand));
        verify(editorToolbar).enable(eq((ToolbarCommand) exportToJpgToolbarCommand));
        verify(editorToolbar).enable(eq((ToolbarCommand) exportToPdfToolbarCommand));
    }

    @Test
    public void checkSetExpressionForUndefined() {
        editor.setExpression(Optional.empty());

        verify(view).selectExpressionType(eq(0));
        verify(view).setSubEditor(eq(undefinedExpressionEditorView));
    }

    @Test
    public void checkSetExpressionForLiteralExpression() {
        final LiteralExpression expression = new LiteralExpression();
        editor.setExpression(Optional.of(expression));

        verify(view).selectExpressionType(eq(1));
        verify(view).setSubEditor(eq(literalExpressionEditorView));
        verify(literalExpressionEditor).setExpression(eq(expression));
    }

    @Test
    public void checkOnExpressionTypeChangedForUnknown() {
        verifyOnExpressionChange(String.class,
                                 e -> !e.isPresent());
    }

    @Test
    public void checkOnExpressionTypeChangedForUndefined() {
        verifyOnExpressionChange(Expression.class,
                                 e -> !e.isPresent());
    }

    @Test
    public void checkOnExpressionTypeChangedForLiteralExpression() {
        verifyOnExpressionChange(LiteralExpression.class,
                                 Optional::isPresent);
    }

    private void verifyOnExpressionChange(final Class<?> clazz,
                                          final Function<Optional<Expression>, Boolean> assertion) {
        editor.setHasExpression(hasExpression);

        //setHasExpression() also calls setExpression() so reset mock for test
        reset(editor);

        editor.onExpressionTypeChanged(clazz.getName());

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setExpressionTypeCommandArgumentCaptor.capture());

        final SetExpressionTypeCommand setExpressionTypeCommand = setExpressionTypeCommandArgumentCaptor.getValue();
        assertNotNull(setExpressionTypeCommand);

        setExpressionTypeCommand.execute(canvasHandler);

        verify(editor).setExpression(setExpressionArgumentCaptor.capture());

        final Optional<Expression> setExpressionArgument = setExpressionArgumentCaptor.getValue();
        assertNotNull(setExpressionArgument);
        assertTrue(assertion.apply(setExpressionArgument));
    }

    private static class ExpressionEditorType<T extends Expression> implements ExpressionType<T> {

        private int index;
        private String name;
        private Optional<T> modelClass;
        private BaseExpressionEditorView.Editor<T> editor;

        public ExpressionEditorType(final int index,
                                    final String name,
                                    final Optional<T> modelClass,
                                    final BaseExpressionEditorView.Editor<T> editor) {
            this.index = index;
            this.name = name;
            this.modelClass = modelClass;
            this.editor = editor;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Optional<T> getModelClass() {
            return modelClass;
        }

        @Override
        public BaseExpressionEditorView.Editor<T> getEditor() {
            return editor;
        }
    }
}
