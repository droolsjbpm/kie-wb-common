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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;

@RunWith(GwtMockitoTestRunner.class)
public class BaseSupplementaryFunctionEditorDefinitionTest {

    @Mock
    private BaseSupplementaryFunctionEditorDefinition baseSupplementaryFunctionEditorDefinition;

    @Before
    public void setup() {
        doCallRealMethod().when(baseSupplementaryFunctionEditorDefinition).createVariable("variable");
    }

    @Test
    public void testDefaultVariableType() {
        final InformationItem variable = baseSupplementaryFunctionEditorDefinition.createVariable("variable");
        assertEquals(variable.getTypeRef().getLocalPart(), BuiltInType.STRING.getName());
    }
}