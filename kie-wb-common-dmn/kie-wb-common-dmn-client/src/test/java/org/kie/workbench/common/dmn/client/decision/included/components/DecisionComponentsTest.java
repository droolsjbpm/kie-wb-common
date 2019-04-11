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

package org.kie.workbench.common.dmn.client.decision.included.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludedNode;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionComponentsTest {

    @Mock
    private DecisionComponents.View view;

    @Mock
    private DMNGraphUtils graphUtils;

    @Mock
    private DMNIncludeModelsClient client;

    @Mock
    private ManagedInstance<DecisionComponentsItem> itemManagedInstance;

    private DecisionComponents decisionComponents;

    @Before
    public void setup() {
        decisionComponents = spy(new DecisionComponents(view, graphUtils, client, itemManagedInstance));
    }

    @Test
    public void testInit() {
        decisionComponents.init();
        verify(view).init(decisionComponents);
    }

    @Test
    public void testGetView() {
        assertEquals(view, decisionComponents.getView());
    }

    @Test
    public void testRefresh() {

        final Diagram diagram = mock(Diagram.class);
        final List<Import> imports = new ArrayList<>();
        final Consumer<List<DMNIncludedNode>> listConsumer = (list) -> {/* Nothing. */};

        doReturn(imports).when(decisionComponents).getImports(diagram);
        doReturn(listConsumer).when(decisionComponents).getNodesConsumer();

        decisionComponents.refresh(diagram);

        verify(decisionComponents).clearDecisionComponents();
        verify(decisionComponents).enableLoading();
        verify(client).loadNodesFromImports(imports, listConsumer);
    }

    @Test
    public void testApplyTermFilter() {

        final String value = "value";
        final DecisionComponentFilter filter = mock(DecisionComponentFilter.class);

        doReturn(filter).when(decisionComponents).getFilter();
        doNothing().when(decisionComponents).applyFilter();

        decisionComponents.applyTermFilter(value);

        verify(filter).setTerm(value);
        verify(decisionComponents).applyFilter();
    }

    @Test
    public void testApplyDrgElementFilterFilter() {

        final String value = "value";
        final DecisionComponentFilter filter = mock(DecisionComponentFilter.class);

        doReturn(filter).when(decisionComponents).getFilter();
        doNothing().when(decisionComponents).applyFilter();

        decisionComponents.applyDrgElementFilterFilter(value);

        verify(filter).setDrgElement(value);
        verify(decisionComponents).applyFilter();
    }

    @Test
    public void testApplyFilter() {

        final DecisionComponentsItem item1 = mock(DecisionComponentsItem.class);
        final DecisionComponentsItem item2 = mock(DecisionComponentsItem.class);
        final DecisionComponentsItem item3 = mock(DecisionComponentsItem.class);
        final DecisionComponent component1 = mock(DecisionComponent.class);
        final DecisionComponent component2 = mock(DecisionComponent.class);
        final DecisionComponent component3 = mock(DecisionComponent.class);
        final List<DecisionComponentsItem> decisionComponentsItems = asList(item1, item2, item3);

        doReturn(decisionComponentsItems).when(decisionComponents).getDecisionComponentsItems();
        when(item1.getDecisionComponent()).thenReturn(component1);
        when(item2.getDecisionComponent()).thenReturn(component2);
        when(item3.getDecisionComponent()).thenReturn(component3);
        when(component1.getName()).thenReturn("name1");
        when(component2.getName()).thenReturn("nome!!!");
        when(component3.getName()).thenReturn("name3");

        decisionComponents.getFilter().setTerm("name");
        decisionComponents.applyFilter();

        verify(item1).hide();
        verify(item2).hide();
        verify(item3).hide();
        verify(item1).show();
        verify(item3).show();
    }

    @Test
    public void testRemoveAllItems() {
        decisionComponents.removeAllItems();
        verify(decisionComponents).clearDecisionComponents();
    }

    @Test
    public void testGetNodesConsumerWhenNodeListIsEmpty() {

        final List<DMNIncludedNode> list = emptyList();

        decisionComponents.getNodesConsumer().accept(list);

        verify(view).setComponentsCounter(0);
        verify(view).hideLoading();
        verify(view).showEmptyState();
    }

    @Test
    public void testGetNodesConsumerWhenNodeListIsNotEmpty() {

        final DMNIncludedNode dmnIncludedNode1 = mock(DMNIncludedNode.class);
        final DMNIncludedNode dmnIncludedNode2 = mock(DMNIncludedNode.class);
        final List<DMNIncludedNode> list = asList(dmnIncludedNode1, dmnIncludedNode2);

        doNothing().when(decisionComponents).addComponent(any());

        decisionComponents.getNodesConsumer().accept(list);

        verify(view).setComponentsCounter(2);
        verify(view).hideLoading();
        verify(view).enableFilterInputs();
        verify(decisionComponents).addComponent(dmnIncludedNode1);
        verify(decisionComponents).addComponent(dmnIncludedNode2);
    }

    @Test
    public void testAddComponent() {

        final DMNIncludedNode node = mock(DMNIncludedNode.class);
        final DecisionComponentsItem item = mock(DecisionComponentsItem.class);
        final List<DecisionComponentsItem> decisionComponentsItems = spy(new ArrayList<>());
        final DecisionComponentsItem.View decisionComponentsView = mock(DecisionComponentsItem.View.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);

        when(decisionComponentsView.getElement()).thenReturn(htmlElement);
        when(itemManagedInstance.get()).thenReturn(item);
        when(item.getView()).thenReturn(decisionComponentsView);
        doReturn(decisionComponentsItems).when(decisionComponents).getDecisionComponentsItems();

        decisionComponents.addComponent(node);

        verify(item).setDecisionComponent(any(DecisionComponent.class));
        verify(decisionComponentsItems).add(item);
        verify(view).addListItem(htmlElement);
    }
}
