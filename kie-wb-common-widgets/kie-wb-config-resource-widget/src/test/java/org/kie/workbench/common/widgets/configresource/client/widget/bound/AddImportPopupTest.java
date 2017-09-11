/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.configresource.client.widget.bound;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.imports.Import;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.configresource.client.resources.i18n.ImportConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class})
public class AddImportPopupTest {

    private List<Import> externalFactTypes = new ArrayList<>();

    private AddImportPopup popup;

    @GwtMock
    private ListBox importTypeListBox;

    @Mock
    private Element importTypeListBoxElement;

    @Captor
    private ArgumentCaptor<String> importTypesCaptor;

    private Import external1 = new Import("zExternal");
    private Import external2 = new Import("yExternal");

    @Before
    public void setup() {
        when(importTypeListBox.getElement()).thenReturn(importTypeListBoxElement);
        when(importTypeListBoxElement.getStyle()).thenReturn(mock(Style.class));

        this.popup = new AddImportPopup();
    }

    @Test
    public void setContentEmpty() {
        popup.setContent(mock(Command.class),
                         externalFactTypes);

        verify(importTypeListBox).addItem(importTypesCaptor.capture());

        final List<String> importTypes = importTypesCaptor.getAllValues();

        assertEquals(1,
                     importTypes.size());
        assertEquals(ImportConstants.INSTANCE.noTypesAvailable(),
                     importTypes.get(0));
    }

    @Test
    public void setContentOrdersImportsAlphabetically() {
        externalFactTypes.add(external1);
        externalFactTypes.add(external2);

        popup.setContent(mock(Command.class),
                         externalFactTypes);

        verify(importTypeListBox,
               times(3)).addItem(importTypesCaptor.capture());

        final List<String> importTypes = importTypesCaptor.getAllValues();

        assertEquals(3,
                     importTypes.size());
        assertEquals(ImportConstants.INSTANCE.ChooseAFactType(),
                     importTypes.get(0));
        assertEquals(external2.getType(),
                     importTypes.get(1));
        assertEquals(external1.getType(),
                     importTypes.get(2));
    }
}
