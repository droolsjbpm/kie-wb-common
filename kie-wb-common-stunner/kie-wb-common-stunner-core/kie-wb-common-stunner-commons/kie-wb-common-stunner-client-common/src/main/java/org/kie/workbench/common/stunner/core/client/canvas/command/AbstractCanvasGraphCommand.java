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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.Objects;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.HasGraphCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * Base type for commands which update both graph status/structure and canvas.
 */
public abstract class AbstractCanvasGraphCommand
        extends AbstractCanvasCommand
        implements HasGraphCommand<AbstractCanvasHandler> {

    private boolean canvasCommandFirst = false;

    public AbstractCanvasGraphCommand() {
    }

    public AbstractCanvasGraphCommand(boolean canvasCommandFirst) {
        this.canvasCommandFirst = canvasCommandFirst;
    }

    /**
     * The private instance of the graph command.
     * It's a private stateful command instance - will be used for undoing the operation on the graph.
     */
    private Command<GraphCommandExecutionContext, RuleViolation> graphCommand;

    /**
     * The private instance of the canvas command.
     * It's a private stateful command instance - will be used for undoing the operation on the graph.
     */
    private Command<AbstractCanvasHandler, CanvasViolation> canvasCommand;

    /**
     * Creates a new command instance for the graph context.
     */
    protected abstract Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context);

    /**
     * Creates a new command instance for the canvas context.
     */
    protected abstract Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context);

    @Override
    public Command<GraphCommandExecutionContext, RuleViolation> getGraphCommand(final AbstractCanvasHandler context) {
        if (null == graphCommand) {
            graphCommand = newGraphCommand(context);
        }
        return graphCommand;
    }

    public Command<AbstractCanvasHandler, CanvasViolation> getCanvasCommand(final AbstractCanvasHandler context) {
        if (null == canvasCommand) {
            canvasCommand = newCanvasCommand(context);
        }
        return canvasCommand;
    }

    @Override
    public CommandResult<CanvasViolation> allow(final AbstractCanvasHandler context) {
        final CommandResult<CanvasViolation> result = canvasCommandFirst ?
                performOperationOnCanvas(context, CommandOperation.ALLOW) :
                performOperationOnGraph(context, CommandOperation.ALLOW);

        if (canDoNexOperation(result)) {
            return canvasCommandFirst ? performOperationOnGraph(context, CommandOperation.ALLOW) :
                    performOperationOnCanvas(context, CommandOperation.ALLOW);
        }
        return result;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        CommandResult<CanvasViolation> result = canvasCommandFirst ?
                performOperationOnCanvas(context, CommandOperation.EXECUTE) :
                performOperationOnGraph(context, CommandOperation.EXECUTE);

        boolean revertGraphCommand = false;
        boolean revertCanvasCommand = false;

        if (canDoNexOperation(result)) {
            final CommandResult<CanvasViolation> lastResult = canvasCommandFirst ?
                    performOperationOnGraph(context, CommandOperation.EXECUTE) :
                    performOperationOnCanvas(context, CommandOperation.EXECUTE);
            if (!canDoNexOperation(lastResult)) {
                revertGraphCommand = true;
                revertCanvasCommand = true;
                result = lastResult;
            }
        } else {
            revertGraphCommand = true;
        }

        if (revertGraphCommand) {
            performOperationOnGraph(context, CommandOperation.UNDO);
        }

        if (revertCanvasCommand) {
            performOperationOnCanvas(context, CommandOperation.UNDO);
        }

        return result;
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        final CommandResult<CanvasViolation> result = canvasCommandFirst ?
                performOperationOnCanvas(context, CommandOperation.UNDO) :
                performOperationOnGraph(context, CommandOperation.UNDO);

        if (canDoNexOperation(result)) {
            return canvasCommandFirst ?
                    performOperationOnGraph(context, CommandOperation.UNDO) :
                    performOperationOnCanvas(context, CommandOperation.UNDO);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected Node<?, Edge> getNode(final AbstractCanvasHandler context,
                                    final String uuid) {
        return context.getGraphIndex().getNode(uuid);
    }

    private enum CommandOperation {
        ALLOW,
        EXECUTE,
        UNDO
    }

    private CommandResult<CanvasViolation> performOperationOnGraph(final AbstractCanvasHandler context,
                                                                   final CommandOperation op) {
        // Ensure the canvas command is initialized before updating the element on the graph side.
        getCanvasCommand(context);
        // Obtain the graph execution context and execute the graph command updates.
        final GraphCommandExecutionContext graphContext = context.getGraphExecutionContext();
        if (Objects.isNull(graphContext)) {
            //skipping command in case there is no graph execution context
            return CanvasCommandResultBuilder.SUCCESS;
        }

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = getGraphCommand(context);
        CommandResult<RuleViolation> graphResult = null;
        switch (op) {
            case ALLOW:
                graphResult = graphCommand.allow(graphContext);
                break;
            case EXECUTE:
                graphResult = graphCommand.execute(graphContext);
                break;
            case UNDO:
                graphResult = graphCommand.undo(graphContext);
                break;
        }
        return new CanvasCommandResultBuilder(graphResult).build();
    }

    private CommandResult<CanvasViolation> performOperationOnCanvas(final AbstractCanvasHandler context,
                                                                    final CommandOperation op) {
        // Ensure the graph command is initialized
        getGraphCommand(context);

        final Command<AbstractCanvasHandler, CanvasViolation> command = getCanvasCommand(context);
        switch (op) {
            case ALLOW:
                return command.allow(context);
            case EXECUTE:
                return command.execute(context);
            case UNDO:
                return command.undo(context);
        }
        return CanvasCommandResultBuilder.FAILED;
    }

    private boolean canDoNexOperation(CommandResult<CanvasViolation> result) {
        return null == result || !CommandUtils.isError(result);
    }

    @Override
    public String toString() {
        return "[" +
                this.getClass().getName() +
                "]" +
                " [canvasCommand=" +
                (null != canvasCommand ? canvasCommand.toString() : "null") +
                " [graphCommand=" +
                (null != graphCommand ? graphCommand.toString() : null) +
                "]";
    }
}
