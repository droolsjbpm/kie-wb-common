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

package org.kie.workbench.common.dmn.client.editors.types.shortcuts;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeListShortcutsTest {

    @Mock
    private DataTypeList dataTypeList;

    @Mock
    private DataTypeListShortcutsView view;

    private DataTypeListShortcuts shortcuts;

    @Before
    public void setup() {
        shortcuts = spy(new DataTypeListShortcuts(view));
        shortcuts.init(dataTypeList);
    }

    @Test
    public void testInit() {

        final Consumer<DataTypeListItem> consumer = (e) -> { /* Nothing. */ };
        doReturn(consumer).when(shortcuts).getDataTypeListItemConsumer();

        shortcuts.init(dataTypeList);

        final DataTypeList actualDataTypeList = shortcuts.getDataTypeList();
        final DataTypeList expectedDataTypeList = shortcuts.getDataTypeList();

        assertEquals(expectedDataTypeList, actualDataTypeList);
        verify(expectedDataTypeList).registerDataTypeListItemUpdateCallback(eq(consumer));
    }

    @Test
    public void testOnArrowDown() {

        final Element nextDataTypeRow = mock(Element.class);
        when(view.getNextDataTypeRow()).thenReturn(Optional.of(nextDataTypeRow));

        shortcuts.onArrowDown();

        verify(view).highlight(nextDataTypeRow);
    }

    @Test
    public void testOnArrowUp() {

        final Element prevDataTypeRow = mock(Element.class);
        when(view.getPrevDataTypeRow()).thenReturn(Optional.of(prevDataTypeRow));

        shortcuts.onArrowUp();

        verify(view).highlight(prevDataTypeRow);
    }

    @Test
    public void testOnTab() {

        final Element firstDataTypeRow = mock(Element.class);
        when(view.getFirstDataTypeRow()).thenReturn(Optional.of(firstDataTypeRow));

        shortcuts.onTab();

        verify(view).highlight(firstDataTypeRow);
    }

    @Test
    public void testOnArrowLeft() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        when(view.getCurrentDataTypeListItem()).thenReturn(Optional.of(listItem));

        shortcuts.onArrowLeft();

        verify(listItem).collapse();
    }

    @Test
    public void testOnArrowRight() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        when(view.getCurrentDataTypeListItem()).thenReturn(Optional.of(listItem));

        shortcuts.onArrowRight();

        verify(listItem).expand();
    }

    @Test
    public void testOnCtrlE() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        when(view.getCurrentDataTypeListItem()).thenReturn(Optional.of(listItem));

        shortcuts.onCtrlE();

        verify(listItem).enableEditMode();
    }

    @Test
    public void testOnEscapeWhenCurrentDataTypeListItemIsPresent() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        when(view.getCurrentDataTypeListItem()).thenReturn(Optional.of(listItem));

        shortcuts.onEscape();

        verify(listItem).disableEditMode();
    }

    @Test
    public void testOnEscapeWhenCurrentDataTypeListItemIsNotPresent() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> items = asList(listItem1, listItem2);
        when(view.getCurrentDataTypeListItem()).thenReturn(Optional.empty());
        when(view.getVisibleDataTypeListItems()).thenReturn(items);

        shortcuts.onEscape();

        verify(listItem1).disableEditMode();
        verify(listItem2).disableEditMode();
    }

    @Test
    public void testOnBackspace() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        when(view.getCurrentDataTypeListItem()).thenReturn(Optional.of(listItem));

        shortcuts.onCtrlBackspace();

        verify(listItem).remove();
    }

    @Test
    public void testOnCtrlS() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        when(view.getFocusedDataTypeListItem()).thenReturn(Optional.of(listItem));

        shortcuts.onCtrlS();

        verify(listItem).saveAndCloseEditMode();
    }

    @Test
    public void testOnCtrlB() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        when(view.getCurrentDataTypeListItem()).thenReturn(Optional.of(listItem));

        shortcuts.onCtrlB();

        verify(listItem).insertNestedField();
    }

    @Test
    public void testOnCtrlU() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        when(view.getCurrentDataTypeListItem()).thenReturn(Optional.of(listItem));

        shortcuts.onCtrlU();

        verify(listItem).insertFieldAbove();
    }

    @Test
    public void testOnCtrlD() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        when(view.getCurrentDataTypeListItem()).thenReturn(Optional.of(listItem));

        shortcuts.onCtrlD();

        verify(listItem).insertFieldBelow();
    }

    @Test
    public void testGetDataTypeListItemConsumer() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);

        when(listItem.getElement()).thenReturn(htmlElement);

        shortcuts.getDataTypeListItemConsumer().accept(listItem);

        verify(view).highlight(htmlElement);
    }
}
