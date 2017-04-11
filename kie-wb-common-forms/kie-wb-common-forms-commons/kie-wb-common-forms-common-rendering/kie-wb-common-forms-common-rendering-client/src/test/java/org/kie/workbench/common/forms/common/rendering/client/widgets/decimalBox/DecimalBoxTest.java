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

package org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox;

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
public class DecimalBoxTest {

    public static final String VALUE_NO_SEPARATOR = "10";
    public static final String VALUE_WITH_SEPARATOR = "10.87";
    public static final int ARROW_LEFT_KEYCODE = 37;
    public static final int ARROW_RIGHT_KEYCODE = 39;
    public static final int PERIOD_KEYCODE = 190;
    public static final Double TEST_VALUE_DOUBLE = 27.89d;
    public static final String TEST_VALUE_STRING = TEST_VALUE_DOUBLE.toString();

    protected DecimalBoxView view;

    @GwtMock
    protected GwtEvent<?> event;

    @GwtMock
    protected Widget viewWidget;

    protected DecimalBox decimalBox;

    @Before
    public void setup() {
        view = mock(DecimalBoxView.class);

        when(view.asWidget()).thenReturn(viewWidget);

        decimalBox = new DecimalBox(view);

        verify(view).setPresenter(decimalBox);

        decimalBox.asWidget();

        verify(view).asWidget();
    }

    @Test
    public void testSetValueWithoutEvents() {

        decimalBox.setValue(TEST_VALUE_DOUBLE);

        verify(view).setValue(TEST_VALUE_STRING);
    }

    @Test
    public void testSetValueWithEvents() {

        decimalBox.setValue(TEST_VALUE_DOUBLE,
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

    @Test
    public void testDecimalSeparatorWhenNotPresent() {

        when(view.getTextValue()).thenReturn(VALUE_NO_SEPARATOR);

        testKeyCode(PERIOD_KEYCODE,
                    false,
                    false);
        testKeyCode(KeyCodes.KEY_NUM_PERIOD,
                    false,
                    false);
    }

    @Test
    public void testDecimalSeparatorWhenPresent() {

        when(view.getTextValue()).thenReturn(VALUE_WITH_SEPARATOR);

        testKeyCode(PERIOD_KEYCODE,
                    false,
                    true);
        testKeyCode(KeyCodes.KEY_NUM_PERIOD,
                    false,
                    true);
    }

    private void testKeyCode(int keyCode,
                             boolean isShiftPressed,
                             boolean expectInvalid) {
        boolean result = decimalBox.isInvalidKeyCode(keyCode,
                                                     isShiftPressed);
        assertEquals(result,
                     expectInvalid);
    }

    @Test
    public void testEvents() {
        ValueChangeHandler handler = mock(ValueChangeHandler.class);
        decimalBox.addValueChangeHandler(handler);
        verify(view,
               atLeast(1)).asWidget();
        verify(viewWidget).addHandler(any(),
                                      any());

        decimalBox.fireEvent(event);
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
        decimalBox.setEnabled(enable);
        verify(view).setEnabled(enable);
    }
}
