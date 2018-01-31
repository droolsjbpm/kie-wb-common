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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BadCommandArgumentsException;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateElementPositionCommandTest extends AbstractGraphCommandTest {

    private static final String UUID = "testUUID";
    private static final Point2D PREVIOUS_LOCATION = new Point2D(100d, 100d);
    private static final Point2D LOCATION = new Point2D(200d, 200d);
    private static final Double W = 50d;
    private static final Double H = 50d;

    @Mock
    Node candidate;
    private View content;
    private UpdateElementPositionCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init(500,
                   500);
        content = mockView(PREVIOUS_LOCATION.getX(),
                           PREVIOUS_LOCATION.getY(),
                           W,
                           H);
        when(candidate.getUUID()).thenReturn(UUID);
        when(candidate.getContent()).thenReturn(content);
        when(graphIndex.getNode(eq(UUID))).thenReturn(candidate);
        this.tested = new UpdateElementPositionCommand(candidate,
                                                       LOCATION,
                                                       false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               times(0)).evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class));
    }

    @Test(expected = BadCommandArgumentsException.class)
    public void testAllowNodeNotFound() {
        this.tested = new UpdateElementPositionCommand(UUID,
                                                       LOCATION,
                                                       PREVIOUS_LOCATION,
                                                       false);
        when(graphIndex.getNode(eq(UUID))).thenReturn(null);
        tested.allow(graphCommandExecutionContext);
    }

    @Test
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<Bounds> bounds = ArgumentCaptor.forClass(Bounds.class);
        verify(content,
               times(1)).setBounds(bounds.capture());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        Bounds b = bounds.getValue();
        assertEquals(UUID,
                     tested.getUuid());
        assertEquals(LOCATION,
                     tested.getLocation());
        assertEquals(PREVIOUS_LOCATION,
                     tested.getPreviousLocation());
        assertEquals(PREVIOUS_LOCATION.getY(),
                     tested.getPreviousLocation().getY()
                , 0d);
        assertEquals(Double.valueOf(LOCATION.getX() + W),
                     b.getLowerRight().getX());
        assertEquals(Double.valueOf(LOCATION.getY() + H),
                     b.getLowerRight().getY());
    }

    @Test(expected = BadCommandArgumentsException.class)
    public void testExecuteNodeNotFound() {
        this.tested = new UpdateElementPositionCommand(UUID,
                                                       LOCATION,
                                                       PREVIOUS_LOCATION,
                                                       false);
        when(graphIndex.getNode(eq(UUID))).thenReturn(null);
        tested.execute(graphCommandExecutionContext);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllowBadBounds() {
        this.tested = new UpdateElementPositionCommand(candidate,
                                                       new Point2D(600d, 600d),
                                                       false);
        final CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        verify(content,
               never()).setBounds(any(Bounds.class));
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteBadBounds() {
        this.tested = new UpdateElementPositionCommand(candidate,
                                                       new Point2D(600d, 600d),
                                                       false);
        final CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        verify(content,
               never()).setBounds(any(Bounds.class));
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
    }
}
