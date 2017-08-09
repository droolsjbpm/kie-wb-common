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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.commands.SetExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbar;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.mvp.Command;

@Dependent
public class ExpressionEditor implements ExpressionEditorView.Presenter {

    private ExpressionEditorView view;
    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private Optional<Command> exitCommand;

    private Optional<HasName> hasName;
    private HasExpression hasExpression;

    private ToolbarCommandStateHandler toolbarCommandStateHandler;

    private List<ExpressionType<Expression>> expressionTypes = new ArrayList<>();

    public ExpressionEditor() {
        //CDI proxy
    }

    @Inject
    @SuppressWarnings("unchecked")
    public ExpressionEditor(final ExpressionEditorView view,
                            final SessionManager sessionManager,
                            final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final Instance<ExpressionType> expressionTypeBeans) {
        this.view = view;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.view.init(this);

        expressionTypeBeans.forEach(t -> this.expressionTypes.add(t));
        this.expressionTypes.sort((o1, o2) -> o1.getIndex() - o2.getIndex());
        this.view.setExpressionTypes(expressionTypes);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void init(final SessionPresenter<AbstractClientFullSession, ?, Diagram> presenter) {
        this.toolbarCommandStateHandler = new ToolbarCommandStateHandler((EditorToolbar) presenter.getToolbar());
    }

    @Override
    public void setHasName(final Optional<HasName> hasName) {
        this.hasName = hasName;
    }

    @Override
    public void setHasExpression(final HasExpression hasExpression) {
        this.hasExpression = hasExpression;
        final Expression e = hasExpression.getExpression();
        setExpression(Optional.ofNullable(e));
        toolbarCommandStateHandler.enter();
    }

    @Override
    public void setExpression(final Optional<Expression> expression) {
        OptionalInt index = OptionalInt.empty();

        if (!expression.isPresent()) {
            index = IntStream.range(0,
                                    expressionTypes.size())
                    .filter(i -> !expressionTypes.get(i).getModelClass().isPresent())
                    .findFirst();

            index.ifPresent(i -> {
                view.selectExpressionType(i);
                view.setSubEditor(expressionTypes.get(i).getEditor().getView());
            });
        } else {
            index = IntStream.range(0,
                                    expressionTypes.size())
                    .filter(i -> expressionTypes.get(i).getModelClass().isPresent())
                    .filter(i -> expressionTypes.get(i).getModelClass().get().getClass().equals(expression.get().getClass()))
                    .findFirst();

            index.ifPresent(i -> {
                view.selectExpressionType(i);
                final BaseExpressionEditorView.Editor<Expression> editor = expressionTypes.get(i).getEditor();
                view.setSubEditor(editor.getView());
                editor.setHasName(hasName);
                editor.setExpression(expression.get());
            });
        }
    }

    @Override
    public void setExitCommand(final Command exitCommand) {
        this.exitCommand = Optional.ofNullable(exitCommand);
    }

    @Override
    public void exit() {
        exitCommand.ifPresent(c -> {
            toolbarCommandStateHandler.exit();
            c.execute();
        });
    }

    @Override
    public void onExpressionTypeChanged(final String className) {
        final Optional<Expression> expression = expressionTypes
                .stream()
                .map(ExpressionType::getModelClass)
                .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
                .filter(e -> e.getClass().getName().equals(className))
                .findFirst();

        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new SetExpressionTypeCommand(hasExpression,
                                                                   expression,
                                                                   this));
    }

    //Package-protected for Unit Tests
    ToolbarCommandStateHandler getToolbarCommandStateHandler() {
        return toolbarCommandStateHandler;
    }

    @SuppressWarnings("unchecked")
    static class ToolbarCommandStateHandler {

        private EditorToolbar toolbar;

        //Package-protected for Unit Tests
        boolean visitGraphToolbarCommandEnabled = false;
        boolean clearToolbarCommandEnabled = false;
        boolean clearStatesToolbarCommandEnabled = false;
        boolean deleteSelectionToolbarCommandEnabled = false;
        boolean switchGridToolbarCommandEnabled = false;
        boolean undoToolbarCommandEnabled = false;
        boolean redoToolbarCommandEnabled = false;
        boolean validateToolbarCommandEnabled = false;
        boolean exportToPngToolbarCommandEnabled = false;
        boolean exportToJpgToolbarCommandEnabled = false;
        boolean exportToPdfToolbarCommandEnabled = false;

        private ToolbarCommandStateHandler(final EditorToolbar toolbar) {
            this.toolbar = toolbar;
        }

        private void enter() {
            this.visitGraphToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getVisitGraphToolbarCommand());
            this.clearToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getClearToolbarCommand());
            this.clearStatesToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getClearStatesToolbarCommand());
            this.deleteSelectionToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getDeleteSelectionToolbarCommand());
            this.switchGridToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getSwitchGridToolbarCommand());
            this.undoToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getUndoToolbarCommand());
            this.redoToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getRedoToolbarCommand());
            this.validateToolbarCommandEnabled = toolbar.isEnabled(toolbar.getValidateToolbarCommand());
            this.exportToPngToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getExportToPngToolbarCommand());
            this.exportToJpgToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getExportToJpgToolbarCommand());
            this.exportToPdfToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getExportToPdfToolbarCommand());

            enableToolbarCommand(toolbar.getVisitGraphToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getClearToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getClearStatesToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getDeleteSelectionToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getSwitchGridToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getUndoToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getRedoToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getValidateToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getExportToPngToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getExportToJpgToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getExportToPdfToolbarCommand(),
                                 false);
        }

        private void exit() {
            enableToolbarCommand(toolbar.getVisitGraphToolbarCommand(),
                                 visitGraphToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getClearToolbarCommand(),
                                 clearToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getClearStatesToolbarCommand(),
                                 clearStatesToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getDeleteSelectionToolbarCommand(),
                                 deleteSelectionToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getSwitchGridToolbarCommand(),
                                 switchGridToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getUndoToolbarCommand(),
                                 undoToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getRedoToolbarCommand(),
                                 redoToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getValidateToolbarCommand(),
                                 validateToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getExportToPngToolbarCommand(),
                                 exportToPngToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getExportToJpgToolbarCommand(),
                                 exportToJpgToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getExportToPdfToolbarCommand(),
                                 exportToPdfToolbarCommandEnabled);
        }

        private void enableToolbarCommand(final ToolbarCommand command,
                                          final boolean enabled) {
            if (enabled) {
                toolbar.enable(command);
            } else {
                toolbar.disable(command);
            }
        }
    }
}
