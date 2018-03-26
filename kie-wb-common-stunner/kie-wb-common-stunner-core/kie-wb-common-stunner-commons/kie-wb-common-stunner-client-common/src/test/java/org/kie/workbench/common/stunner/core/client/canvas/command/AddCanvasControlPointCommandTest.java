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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddCanvasControlPointCommandTest extends AbstractCanvasControlPointCommandTest {

    private AddCanvasControlPointCommand addCanvasControlPointCommand;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        addCanvasControlPointCommand = spy(new AddCanvasControlPointCommand(edge, controlPoint1));
    }

    @Test
    public void testAllowSuccess() {
        CommandResult<CanvasViolation> result = addCanvasControlPointCommand.allow(canvasHandler);
        verify(shape).addControlPoints(controlPoint1);
        assertFalse(CommandUtils.isError(result));
    }

    @Test
    public void testAllowError() {
        when(shape.addControlPoints(controlPoint1)).thenReturn(controlPointList);
        CommandResult<CanvasViolation> result = addCanvasControlPointCommand.allow(canvasHandler);
        verify(shape).addControlPoints(controlPoint1);
        assertTrue(CommandUtils.isError(result));
    }

    @Test
    public void execute() {
        CommandResult<CanvasViolation> result = addCanvasControlPointCommand.execute(canvasHandler);
        verify(addCanvasControlPointCommand).allow(canvasHandler);
        assertFalse(CommandUtils.isError(result));
    }
}