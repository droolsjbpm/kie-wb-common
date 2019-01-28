/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextBoundsWrap;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresTextDecoratorTest {

    @Mock
    private Supplier<ViewEventHandlerManager> eventHandlerManager;

    @Mock
    private ViewEventHandlerManager manager;

    private final BoundingBox bb = new BoundingBox(new Point2D(0, 0),
                                                   new Point2D(100, 100));

    @Before
    public void setup() {
        when(eventHandlerManager.get()).thenReturn(manager);
    }

    @Test
    public void ensureThatWrapBoundariesAreSet() {

        final WiresTextDecorator decorator = new WiresTextDecorator(eventHandlerManager, bb);
        final Text text = (Text) decorator.getView().asGroup().getChildNodes().get(0);
        final BoundingBox wrapBoundaries = ((TextBoundsWrap) text.getWrapper()).getWrapBoundaries();

        assertEquals(bb.getWidth(), wrapBoundaries.getWidth(), 0.01d);
        assertEquals(bb.getHeight(), wrapBoundaries.getHeight(), 0.01d);
        assertNotEquals(wrapBoundaries.getWidth(), 0.0d, 0.01d);
        assertNotEquals(wrapBoundaries.getHeight(), 0.0d, 0.01d);
    }

    @Test
    public void ensureThatResizeUpdatesTheNode() {
        final WiresTextDecorator decorator = mock(WiresTextDecorator.class);
        doCallRealMethod().when(decorator).resize(anyDouble(), anyDouble());

        decorator.resize(0, 0);
        verify(decorator).update();
    }

    @Test
    public void ensureThatUpdateRefreshTextBoundaries() {
        final WiresTextDecorator decorator = mock(WiresTextDecorator.class);
        doCallRealMethod().when(decorator).update();

        decorator.update();
        verify(decorator).updateTextBoundaries();
    }
}