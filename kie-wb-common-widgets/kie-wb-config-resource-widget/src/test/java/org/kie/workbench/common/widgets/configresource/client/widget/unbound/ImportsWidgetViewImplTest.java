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
package org.kie.workbench.common.widgets.configresource.client.widget.unbound;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.project.datamodel.imports.Import;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ImportsWidgetViewImplTest {

    @Mock
    private AddImportPopup addImportPopup;

    @Mock
    private ImportsWidgetView.Presenter presenter;

    @Mock
    private EventSourceMock<LockRequiredEvent> lockRequired;

    private List<Import> imports = new ArrayList<>();

    private Import import1 = new Import("zImport");
    private Import import2 = new Import("yImport");
    private Import import3 = new Import("xImport");

    private ImportsWidgetViewImpl view;

    @Before
    public void setup() {
        this.view = new ImportsWidgetViewImpl(addImportPopup,
                                              lockRequired);
        this.view.init(presenter);

        imports.add(import1);
        imports.add(import2);
        imports.add(import3);

        view.setContent(imports,
                        false);
    }

    @Test
    public void setContentSortsAlphabetically() {
        final List<Import> imports = view.getDataProvider().getList();
        assertEquals(3,
                     imports.size());

        assertEquals(import3,
                     imports.get(0));
        assertEquals(import2,
                     imports.get(1));
        assertEquals(import1,
                     imports.get(2));
    }

    @Test
    public void checkAddImportSortsAlphabetically() {
        final Import newImport = new Import("new1");
        when(addImportPopup.getImportType()).thenReturn(newImport.getType());

        view.makeAddImportCommand().execute();

        verify(lockRequired).fire(any(LockRequiredEvent.class));

        final List<Import> imports = view.getDataProvider().getList();
        assertEquals(4,
                     imports.size());

        assertEquals(newImport,
                     imports.get(0));
        assertEquals(import3,
                     imports.get(1));
        assertEquals(import2,
                     imports.get(2));
        assertEquals(import1,
                     imports.get(3));
    }

    @Test
    public void checkRemoveImportSortsAlphabetically() {
        view.makeRemoveImportCommand().execute(import2);

        verify(lockRequired).fire(any(LockRequiredEvent.class));

        final List<Import> imports = view.getDataProvider().getList();
        assertEquals(2,
                     imports.size());

        assertEquals(import3,
                     imports.get(0));
        assertEquals(import1,
                     imports.get(1));
    }

    @Test
    public void checkAddPopupInitialisation() {
        view.onClickAddImportButton(mock(ClickEvent.class));

        verify(addImportPopup).setCommand(eq(view.getAddImportCommand()));
        verify(addImportPopup).show();
    }
}
