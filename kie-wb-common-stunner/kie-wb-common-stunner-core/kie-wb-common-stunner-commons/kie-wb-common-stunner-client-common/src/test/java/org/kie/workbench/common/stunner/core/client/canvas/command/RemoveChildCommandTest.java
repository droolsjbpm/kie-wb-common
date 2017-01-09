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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class RemoveChildCommandTest extends AbstractCanvasCommandTest {

    @Mock private Node parent;
    @Mock private Node candidate;

    private RemoveChildCommand tested;

    @Before
    public void setup() throws Exception {
        super.setup();
        when( parent.getUUID() ).thenReturn( "uuid1" );
        when( candidate.getUUID() ).thenReturn( "uuid2" );
        this.tested = new RemoveChildCommand( parent, candidate );
    }

    @Test
    public void testGetGraphCommand() {
        final org.kie.workbench.common.stunner.core.graph.command.impl.RemoveChildCommand graphCommand =
                ( org.kie.workbench.common.stunner.core.graph.command.impl.RemoveChildCommand ) tested.newGraphCommand( canvasHandler );
        assertNotNull( graphCommand );
        assertEquals( parent, graphCommand.getParent() );
        assertEquals( candidate, graphCommand.getCandidate() );
    }


    @Test
    public void testGetCanvasCommand() {
        final RemoveCanvasChildCommand canvasCommand =
                ( RemoveCanvasChildCommand ) tested.newCanvasCommand( canvasHandler );
        assertNotNull( canvasCommand );
        assertEquals( parent, canvasCommand.getParent() );
        assertEquals( candidate, canvasCommand.getChild() );
    }

}
