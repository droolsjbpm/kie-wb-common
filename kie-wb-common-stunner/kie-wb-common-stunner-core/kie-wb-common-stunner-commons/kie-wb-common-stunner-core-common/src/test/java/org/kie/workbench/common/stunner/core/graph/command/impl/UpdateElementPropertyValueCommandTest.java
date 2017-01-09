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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BadCommandArgumentsException;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class UpdateElementPropertyValueCommandTest extends AbstractGraphCommandTest {

    private static final String UUID = "testUUID";
    private static final String DEF_ID = "defId";
    private static final String PROPERTY_ID = "pId";
    private static final String PROPERTY_VALUE = "testValue1";
    private static final String PROPERTY_OLD_VALUE = "testOldValue1";

    @Mock private Node candidate;
    private View content;
    @Mock private Object definition;
    private Object property = new PropertyStub( PROPERTY_ID );
    private UpdateElementPropertyValueCommand tested;

    @Before
    @SuppressWarnings( "unchecked" )
    public void setup() throws Exception {
        super.init( 500, 500 );
        content = mockView( 10, 10, 50, 50 );
        when( candidate.getContent() ).thenReturn( content );
        when( content.getDefinition() ).thenReturn( definition );
        Set<Object> properties = new HashSet<Object>( 1 ) {{
            add( property );
        }};
        when( definitionAdapter.getProperties( eq( definition ) )).thenReturn( properties );
        when( definitionAdapter.getId( eq( definition ) ) ).thenReturn( DEF_ID );
        when( propertyAdapter.getId( eq( property ) )).thenReturn( PROPERTY_ID );
        when( propertyAdapter.getValue( eq( property ) )).thenReturn( PROPERTY_OLD_VALUE );
        when( graphIndex.getNode( eq( UUID ) )).thenReturn( candidate );
        this.tested = new UpdateElementPropertyValueCommand( UUID, PROPERTY_ID, PROPERTY_VALUE );
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
    public void testAllowNodeNotFound() {
        when( graphIndex.getNode( eq( UUID ) )).thenReturn( null );
        tested.allow( graphCommandExecutionContext );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute( graphCommandExecutionContext );
        ArgumentCaptor<Bounds> bounds = ArgumentCaptor.forClass( Bounds.class );
        assertEquals( CommandResult.Type.INFO, result.getType() );
        assertEquals( PROPERTY_OLD_VALUE, tested.getOldValue() );
        verify( propertyAdapter, times( 1 ) ).getValue( eq( property ) );
        verify( propertyAdapter, times( 1 ) ).setValue( eq( property ), eq( PROPERTY_VALUE ) );
    }

    @Test( expected = BadCommandArgumentsException.class )
    public void testExecuteNodeNotFound() {
        when( graphIndex.getNode( eq( UUID ) )).thenReturn( null );
        tested.execute( graphCommandExecutionContext );
    }

    private class PropertyStub {
        private final String uuid;
        private PropertyStub( String uuid ) {
            this.uuid = uuid;
        }
    }
}
