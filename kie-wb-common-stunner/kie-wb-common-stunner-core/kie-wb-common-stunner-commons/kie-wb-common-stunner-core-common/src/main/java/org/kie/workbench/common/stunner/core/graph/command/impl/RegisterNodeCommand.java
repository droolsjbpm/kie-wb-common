/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A Command to register and node into the graph storage.
 * Cardinality rule evaluations for the graph required.
 *
 * This command should be used as aggregate for composite commands,
 * but it's recommended to provide on the factory only public commands
 * that do really update the graph structure:
 * - <a>org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand</a>
 * - <a>org.kie.workbench.common.stunner.core.graph.command.impl.AddChildNodeCommand</a>
 * - <a>org.kie.workbench.common.stunner.core.graph.command.impl.AddDockedNodeCommand</a>
 */
@Portable
public class RegisterNodeCommand extends AbstractGraphCommand {

    private final Node candidate;

    public RegisterNodeCommand( @MapsTo( "candidate" ) Node candidate ) {
        this.candidate = PortablePreconditions.checkNotNull( "candidate",
                candidate );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public CommandResult<RuleViolation> execute( final GraphCommandExecutionContext context ) {
        final CommandResult<RuleViolation> results = allow( context );
        if ( !results.getType().equals( CommandResult.Type.ERROR ) ) {
            final Graph<?, Node> graph = getGraph( context );
            graph.addNode( candidate );
            getMutableIndex( context ).addNode( candidate );
        }
        return results;
    }

    @SuppressWarnings( "unchecked" )
    protected CommandResult<RuleViolation> check( final GraphCommandExecutionContext context ) {
        final Graph<?, Node> graph = getGraph( context );
        final Collection<RuleViolation> cardinalityRuleViolations =
                ( Collection<RuleViolation> ) context.getRulesManager()
                        .cardinality()
                        .evaluate( graph, getCandidate(), RuleManager.Operation.ADD ).violations();
        return new GraphCommandResultBuilder( new ArrayList<RuleViolation>( 1 ) {{
            addAll( cardinalityRuleViolations );
        }} ).build();

    }

    @Override
    @SuppressWarnings( "unchecked" )
    public CommandResult<RuleViolation> undo( GraphCommandExecutionContext context ) {
        final DeregisterNodeCommand undoCommand = new DeregisterNodeCommand( candidate );
        return undoCommand.execute( context );
    }

    public Node getCandidate() {
        return candidate;
    }

    @Override
    public String toString() {
        return "RegisterNodeCommand [candidate=" + candidate.getUUID() + "]";
    }
}
