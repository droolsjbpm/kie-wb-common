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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.types.DMNParseService;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintEnumerationTest {

    @Mock
    private DataTypeConstraintEnumeration.View view;

    @Mock
    private Caller<DMNParseService> serviceCaller;

    @Mock
    private DMNParseService service;

    @Mock
    private ScrollHelper scrollHelper;

    @Mock
    private EventSourceMock<DataTypeConstraintParserWarningEvent> parserWarningEvent;

    @Mock
    private ManagedInstance<DataTypeConstraintEnumerationItem> enumerationItemInstances;

    private DataTypeConstraintEnumeration constraintEnumeration;

    @Before
    public void setup() {
        constraintEnumeration = spy(new DataTypeConstraintEnumeration(view, serviceCaller, scrollHelper, parserWarningEvent, enumerationItemInstances));
    }

    @Test
    public void testSetup() {
        constraintEnumeration.setup();

        verify(view).init(constraintEnumeration);
    }

    @Test
    public void testGetValue() {

        final DataTypeConstraintEnumerationItem item1 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item2 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item3 = mock(DataTypeConstraintEnumerationItem.class);

        when(item1.getValue()).thenReturn("123");
        when(item2.getValue()).thenReturn("456");
        when(item3.getValue()).thenReturn("");

        doReturn(asList(item1, item2)).when(constraintEnumeration).getEnumerationItems();

        final String actualValue = constraintEnumeration.getValue();
        final String expectedValue = "123, 456";

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSetValue() {

        final RemoteCallback<List<String>> successCallback = (c) -> { /* Nothing. */ };
        final ErrorCallback<Object> errorCallback = (m, t) -> true;
        final String value = "value";

        doReturn(successCallback).when(constraintEnumeration).getSuccessCallback();
        doReturn(errorCallback).when(constraintEnumeration).getErrorCallback();
        when(serviceCaller.call(successCallback, errorCallback)).thenReturn(service);

        constraintEnumeration.setValue(value);

        verify(service).parseFEELList(value);
    }

    @Test
    public void testRefreshView() {

        final String value = "1, 2, 3";

        doNothing().when(constraintEnumeration).setValue(anyString());
        doReturn(value).when(constraintEnumeration).getValue();

        constraintEnumeration.refreshView();

        verify(constraintEnumeration).setValue(value);
        verify(constraintEnumeration).render();
    }

    @Test
    public void testGetSuccessCallback() {

        final DataTypeConstraintEnumerationItem item1 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item2 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item3 = mock(DataTypeConstraintEnumerationItem.class);

        doReturn(item1).when(constraintEnumeration).makeEnumerationItem("12");
        doReturn(item2).when(constraintEnumeration).makeEnumerationItem("34");
        doReturn(item3).when(constraintEnumeration).makeEnumerationItem("56");

        constraintEnumeration.getSuccessCallback().callback(asList("12", "34", "56"));

        verify(constraintEnumeration).setEnumerationItems(asList(item1, item2, item3));
        verify(constraintEnumeration).render();
        verify(constraintEnumeration, never()).addEnumerationItem();
    }

    @Test
    public void testGetErrorCallback() {

        doNothing().when(constraintEnumeration).addEnumerationItem();

        constraintEnumeration.getErrorCallback().error(null, null);

        verify(parserWarningEvent).fire(any(DataTypeConstraintParserWarningEvent.class));
        verify(constraintEnumeration).setEnumerationItems(emptyList());
        verify(constraintEnumeration).render();
        verify(constraintEnumeration).addEnumerationItem();
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedElement);

        final Element actualElement = constraintEnumeration.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testRender() {

        final DataTypeConstraintEnumerationItem item1 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item2 = mock(DataTypeConstraintEnumerationItem.class);
        final Element element1 = mock(Element.class);
        final Element element2 = mock(Element.class);

        when(item1.getElement()).thenReturn(element1);
        when(item2.getElement()).thenReturn(element2);
        doReturn(asList(item1, item2)).when(constraintEnumeration).getEnumerationItems();

        constraintEnumeration.render();

        verify(view).clear();
        verify(view).addItem(element1);
        verify(view).addItem(element2);
    }

    @Test
    public void testAddEnumerationItem() {

        final DataTypeConstraintEnumerationItem item = mock(DataTypeConstraintEnumerationItem.class);
        final List<DataTypeConstraintEnumerationItem> items = spy(new ArrayList<>());
        final Element element = mock(Element.class);

        doReturn(items).when(constraintEnumeration).getEnumerationItems();
        doReturn(item).when(constraintEnumeration).makeEnumerationItem("");
        doReturn(element).when(constraintEnumeration).getElement();

        constraintEnumeration.addEnumerationItem();

        verify(items).add(item);
        verify(constraintEnumeration).render();
        verify(scrollHelper).scrollToBottom(element);
        verify(item).enableEditMode();
    }

    @Test
    public void testMakeEnumerationItem() {

        final String value = "123";
        final DataTypeConstraintEnumerationItem expectedItem = mock(DataTypeConstraintEnumerationItem.class);

        when(enumerationItemInstances.get()).thenReturn(expectedItem);

        final DataTypeConstraintEnumerationItem actualItem = constraintEnumeration.makeEnumerationItem(value);

        verify(expectedItem).setValue(value);
        verify(expectedItem).setDataTypeConstraintEnumeration(constraintEnumeration);
        assertEquals(expectedItem, actualItem);
    }
}
