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

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BadCommandArgumentsException;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class UnDockNodeCommandTest extends AbstractGraphCommandTest {

    private static final String PARENT_UUID = "parentUUID";
    private static final String CANDIDATE_UUID = "candidateUUID";
    private static final String Edge_UUID = "edgeUUID";

    private Node parent;
    private Node candidate;
    private Edge edge;
    private @Mock Dock edgeContent;
    private final List<Edge> candidateOutEdges = new LinkedList<>();
    private final List<Edge> parentInEdges = new LinkedList<>();

    private UnDockNodeCommand tested;

    @Before
    public void setup() throws Exception {
        super.init( 500, 500 );
        this.parent = mockNode( PARENT_UUID );
        this.candidate = mockNode( CANDIDATE_UUID );
        this.edge = mockEdge( CANDIDATE_UUID );
        when( edge.getContent() ).thenReturn( edgeContent );
        when( edge.getTargetNode() ).thenReturn( parent );
        when( edge.getSourceNode() ).thenReturn( candidate );
        when( graphIndex.getNode( eq( PARENT_UUID ) )).thenReturn( parent );
        when( graphIndex.getNode( eq( CANDIDATE_UUID ) )).thenReturn( candidate );
        when( graphIndex.getEdge( eq( Edge_UUID ) )).thenReturn( edge );
        when( parent.getInEdges() ).thenReturn( parentInEdges );
        when( candidate.getOutEdges() ).thenReturn( candidateOutEdges );
        parentInEdges.add( edge );
        candidateOutEdges.add( edge );
        this.tested = new UnDockNodeCommand( PARENT_UUID, CANDIDATE_UUID );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( containmentRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );
        verify( cardinalityRuleManager, times( 0 ) ).evaluate( any( Graph.class ), any( Node.class ), any( RuleManager.Operation.class ) );
        verify( connectionRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ), any( Node.class ) );
        verify( edgeCardinalityRuleManager, times( 0 ) ).evaluate( any( Edge.class ), any( Node.class ),
                any( List.class ), any( EdgeCardinalityRule.Type.class ), any( RuleManager.Operation.class ) );
        verify( dockingRuleManager, times( 0 ) ).evaluate( any( Element.class ), any( Element.class ) );

    }

    @Test( expected = BadCommandArgumentsException.class )
    @SuppressWarnings( "unchecked" )
    public void testNotAllowed() {
        when( graphIndex.getNode( eq( PARENT_UUID ) )).thenReturn( null );
        CommandResult<RuleViolation> result = tested.allow( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.ERROR, result.getType() );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute( graphCommandExecutionContext );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        assertTrue( parentInEdges.isEmpty() );
        assertTrue( candidateOutEdges.isEmpty() );
        verify( graphIndex, times( 1 ) ).removeEdge( any( Edge.class ) );
        verify( graphIndex, times( 0 ) ).removeNode(  any( Node.class ) );
        verify( graph, times( 0 ) ).addNode( any( Node.class ) );
        verify( graphIndex, times( 0 ) ).addNode( any( Node.class ) );
        verify( graphIndex, times( 0 ) ).addEdge( any( Edge.class ) );
    }

}
