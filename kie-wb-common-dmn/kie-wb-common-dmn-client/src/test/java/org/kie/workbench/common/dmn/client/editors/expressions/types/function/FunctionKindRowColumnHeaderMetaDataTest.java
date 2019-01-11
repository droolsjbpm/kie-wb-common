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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.KindPopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

public class FunctionKindRowColumnHeaderMetaDataTest {

    private FunctionKindRowColumnHeaderMetaData functionKindRow;

    private FunctionDefinition function;

    private Supplier<FunctionDefinition> functionSupplier;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private KindPopoverView.Presenter editor;

    @Mock
    private Optional<String> editorTitle;

    @Mock
    private FunctionGrid gridWidget;

    @Before
    public void setup() {
        this.function = new FunctionDefinition();
        this.functionSupplier = () -> function;

        this.functionKindRow = new FunctionKindRowColumnHeaderMetaData(functionSupplier,
                                                                       cellEditorControls,
                                                                       editor,
                                                                       editorTitle,
                                                                       gridWidget);
    }

    @Test
    public void testGetTitle() {
        final String expected = functionSupplier.get().getKind().code();
        assertEquals(expected, functionKindRow.getTitle());
    }
}