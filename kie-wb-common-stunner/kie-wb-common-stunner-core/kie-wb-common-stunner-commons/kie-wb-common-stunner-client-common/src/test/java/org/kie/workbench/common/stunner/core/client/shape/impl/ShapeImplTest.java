/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.shape.impl;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShapeImplTest {

    @Mock
    private ShapeStateHelper shapeStateHelper;

    private ShapeViewExtStub view;
    private ShapeImpl<ShapeView> tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(shapeStateHelper.save(any(Predicate.class))).thenReturn(shapeStateHelper);
        this.view = spy(new ShapeViewExtStub());
        this.tested = new ShapeImpl<ShapeView>(view,
                                               shapeStateHelper);
        verify(shapeStateHelper,
               times(1)).forShape(eq(tested));
    }

    @Test
    public void testGetters() {
        assertEquals(view,
                     tested.getShapeView());
        assertEquals(shapeStateHelper,
                     tested.getShapeStateHelper());
    }

    @Test
    public void testUUID() {
        tested.setUUID("uuid1");
        assertEquals("uuid1",
                     tested.getUUID());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyState() {
        tested.applyState(ShapeState.NONE);
        verify(shapeStateHelper,
               times(1)).save(any(Predicate.class));
        verify(shapeStateHelper,
               times(1)).applyState(eq(ShapeState.NONE));
    }

    @Test
    public void testAfterDraw() {
        tested.afterDraw();
        verify(view,
               times(1)).moveTitleToTop();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(view,
               times(1)).destroy();
    }
}
