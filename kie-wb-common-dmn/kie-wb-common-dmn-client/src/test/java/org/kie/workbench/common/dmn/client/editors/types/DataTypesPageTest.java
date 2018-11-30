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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.List;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManagerStackStore;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessages;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub({RootPanel.class})
@RunWith(GwtMockitoTestRunner.class)
public class DataTypesPageTest {

    @Mock
    private DataTypeList treeList;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private ItemDefinitionStore definitionStore;

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private DataTypeManagerStackStore stackIndex;

    @Mock
    private DataTypeFlashMessages flashMessages;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private TranslationService translationService;

    @Mock
    private HTMLDivElement pageView;

    @Captor
    private ArgumentCaptor<List<DataType>> dataTypesCaptor;

    private DataTypesPage page;

    @Before
    public void setup() {
        page = spy(new DataTypesPage(treeList,
                                     itemDefinitionUtils,
                                     definitionStore,
                                     dataTypeStore,
                                     dataTypeManager,
                                     stackIndex,
                                     flashMessages,
                                     dmnGraphUtils,
                                     translationService,
                                     pageView));
    }

    @Test
    public void testOnFocusWhenPageIsLoaded() {

        doReturn(true).when(page).isLoaded();

        page.onFocus();

        verify(page, never()).reload();
        verify(page).refreshPageView();
    }

    @Test
    public void testOnFocusWhenPageIsNotLoaded() {

        doReturn(false).when(page).isLoaded();

        page.onFocus();

        verify(page).reload();
        verify(page).refreshPageView();
    }

    @Test
    public void testOnLostFocus() {
        page.onLostFocus();

        verify(flashMessages).hideMessages();
    }

    @Test
    public void testReload() {

        final String expected = "dmnModelNamespace";

        doReturn(expected).when(page).currentDMNModelNamespace();

        page.reload();

        final String actual = page.getLoadedDMNModelNamespace();

        verify(page).cleanDataTypeStore();
        verify(page).loadDataTypes();

        assertEquals(expected, actual);
    }

    @Test
    public void testRefreshPageView() {

        final HTMLElement flashMessagesElement = mock(HTMLElement.class);
        final HTMLElement treeListElement = mock(HTMLElement.class);

        pageView.innerHTML = "something";
        when(flashMessages.getElement()).thenReturn(flashMessagesElement);
        when(treeList.getElement()).thenReturn(treeListElement);

        page.refreshPageView();

        final String actual = pageView.innerHTML;
        final String expected = "";

        assertEquals(expected, actual);
        verify(pageView).appendChild(flashMessagesElement);
        verify(pageView).appendChild(treeListElement);
    }

    @Test
    public void testIsLoadedWhenItIsNotLoaded() {

        doReturn("dmnModelNamespace1").when(page).currentDMNModelNamespace();
        doReturn("dmnModelNamespace2").when(page).getLoadedDMNModelNamespace();

        assertFalse(page.isLoaded());
    }

    @Test
    public void testIsLoadedWhenItIsLoaded() {

        doReturn("dmnModelNamespace1").when(page).currentDMNModelNamespace();
        doReturn("dmnModelNamespace1").when(page).getLoadedDMNModelNamespace();

        assertTrue(page.isLoaded());
    }

    @Test
    public void testCurrentDMNModelNamespaceWhenDefinitionsIsNull() {

        when(dmnGraphUtils.getDefinitions()).thenReturn(null);

        final String actual = page.currentDMNModelNamespace();
        final String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    public void testCurrentDMNModelNamespaceWhenNamespaceIsNull() {

        final Definitions definitions = mock(Definitions.class);

        when(definitions.getNamespace()).thenReturn(null);
        when(dmnGraphUtils.getDefinitions()).thenReturn(definitions);

        final String actual = page.currentDMNModelNamespace();
        final String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    public void testCurrentDMNModelNamespace() {

        final Definitions definitions = mock(Definitions.class);
        final Text text = mock(Text.class);
        final String expected = "currentDMNModelNamespace";

        when(text.getValue()).thenReturn(expected);
        when(definitions.getNamespace()).thenReturn(text);
        when(dmnGraphUtils.getDefinitions()).thenReturn(definitions);

        final String actual = page.currentDMNModelNamespace();

        assertEquals(expected, actual);
    }

    @Test
    public void testCleanDataTypeStore() {
        page.cleanDataTypeStore();

        verify(definitionStore).clear();
        verify(dataTypeStore).clear();
        verify(stackIndex).clear();
    }

    @Test
    public void testLoadDataTypes() {

        final ItemDefinition itemDefinition1 = makeItem("itemDefinition1");
        final ItemDefinition itemDefinition2 = makeItem("itemDefinition2");
        final ItemDefinition itemDefinition3 = makeItem("itemDefinition3");
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);

        final List<ItemDefinition> itemDefinitions = asList(itemDefinition1, itemDefinition2, itemDefinition3);

        when(itemDefinitionUtils.all()).thenReturn(itemDefinitions);
        doReturn(dataType1).when(page).makeDataType(itemDefinition1);
        doReturn(dataType2).when(page).makeDataType(itemDefinition2);
        doReturn(dataType3).when(page).makeDataType(itemDefinition3);

        page.loadDataTypes();

        verify(treeList).setupItems(dataTypesCaptor.capture());

        final List<DataType> dataTypes = dataTypesCaptor.getValue();

        assertThat(dataTypes).containsExactly(dataType1, dataType2, dataType3);
    }

    @Test
    public void testMakeDataType() {

        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final DataType expectedDataType = mock(DataType.class);

        when(dataTypeManager.from(itemDefinition)).thenReturn(dataTypeManager);
        when(dataTypeManager.get()).thenReturn(expectedDataType);

        final DataType actualDataType = page.makeDataType(itemDefinition);

        assertEquals(expectedDataType, actualDataType);
    }

    @Test
    public void testOnDataTypePageNavTabActiveEvent() {

        page.onDataTypePageNavTabActiveEvent(mock(DataTypePageTabActiveEvent.class));

        verify(page).onFocus();
    }

    private ItemDefinition makeItem(final String itemName) {
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name name = mock(Name.class);

        when(name.getValue()).thenReturn(itemName);
        when(itemDefinition.getName()).thenReturn(name);

        return itemDefinition;
    }
}
