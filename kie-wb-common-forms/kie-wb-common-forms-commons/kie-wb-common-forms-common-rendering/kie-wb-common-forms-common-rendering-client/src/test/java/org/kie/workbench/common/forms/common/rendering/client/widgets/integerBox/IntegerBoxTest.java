/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class IntegerBoxTest {

    public static final int ARROW_LEFT_KEYCODE = 37;
    public static final int ARROW_RIGHT_KEYCODE = 39;
    public static final int PERIOD_KEYCODE = 190;
    public static final Long TEST_VALUE_LONG = 27l;
    public static final String TEST_VALUE_STRING = TEST_VALUE_LONG.toString();

    protected IntegerBoxView view;

    @GwtMock
    protected GwtEvent<?> event;

    @GwtMock
    protected Widget viewWidget;

    protected IntegerBox integerBox;

    @Before
    public void setup() {
        view = mock(IntegerBoxView.class);

        when(view.asWidget()).thenReturn(viewWidget);

        integerBox = new IntegerBox(view);

        verify(view).setPresenter(integerBox);

        integerBox.asWidget();

        verify(view).asWidget();
    }

    @Test
    public void testSetValueWithoutEvents() {

        integerBox.setValue(TEST_VALUE_LONG);

        verify(view).setValue(TEST_VALUE_STRING);
    }

    @Test
    public void testSetValueWithEvents() {

        integerBox.setValue(TEST_VALUE_LONG,
                            true);

        verify(view).setValue(TEST_VALUE_STRING);
    }

    @Test
    public void testKeyCodeLetter() {
        testKeyCode(KeyCodes.KEY_A,
                    false,
                    true);
    }

    @Test
    public void testKeyCodeSpace() {
        testKeyCode(KeyCodes.KEY_SPACE,
                    false,
                    true);
    }

    @Test
    public void testKeyCodePeriod() {
        testKeyCode(PERIOD_KEYCODE,
                    false,
                    true);
    }

    @Test
    public void testKeyCodeNumPadPeriod() {
        testKeyCode(KeyCodes.KEY_NUM_PERIOD,
                    false,
                    true);
    }

    @Test
    public void testKeyCodeDigit() {
        testKeyCode(KeyCodes.KEY_ONE,
                    false,
                    false);
    }

    @Test
    public void testKeyCodeNumPadDigit() {
        testKeyCode(KeyCodes.KEY_NUM_ONE,
                    false,
                    false);
    }

    @Test
    public void testKeyCodeBackSpace() {
        testKeyCode(KeyCodes.KEY_BACKSPACE,
                    false,
                    false);
    }

    @Test
    public void testKeyCodeLeftArrow() {
        testKeyCode(ARROW_LEFT_KEYCODE,
                    false,
                    false);
    }

    @Test
    public void testKeyCodeRightArrow() {
        testKeyCode(ARROW_RIGHT_KEYCODE,
                    false,
                    false);
    }

    private void testKeyCode(int keyCode,
                             boolean isShiftPressed,
                             boolean expectInvalid) {
        boolean result = integerBox.isInvalidKeyCode(keyCode,
                                                     isShiftPressed);
        assertEquals(result,
                     expectInvalid);
    }

    @Test
    public void testEvents() {
        ValueChangeHandler handler = mock(ValueChangeHandler.class);
        integerBox.addValueChangeHandler(handler);
        verify(view,
               atLeast(1)).asWidget();
        verify(viewWidget).addHandler(any(),
                                      any());

        integerBox.fireEvent(event);
        verify(view,
               atLeast(2)).asWidget();
        verify(viewWidget).fireEvent(event);
    }

    @Test
    public void testEnableTrue() {
        testEnable(true);
    }

    @Test
    public void testEnableFalse() {
        testEnable(false);
    }

    private void testEnable(boolean enable) {
        integerBox.setEnabled(enable);
        verify(view).setEnabled(enable);
    }
}
