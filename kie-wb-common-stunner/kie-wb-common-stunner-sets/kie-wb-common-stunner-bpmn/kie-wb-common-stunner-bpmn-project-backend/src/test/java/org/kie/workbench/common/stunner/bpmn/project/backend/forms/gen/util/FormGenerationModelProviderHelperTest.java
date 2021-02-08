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

package org.kie.workbench.common.stunner.bpmn.project.backend.forms.gen.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.BaseDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.core.backend.service.AbstractDefinitionSetService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormGenerationModelProviderHelperTest {

    @Mock
    private AbstractDefinitionSetService backendService;

    @Mock
    private BaseDirectDiagramMarshaller newMarshaller;

    @Mock
    private Diagram diagram;

    private FormGenerationModelProviderHelper tested;

    @Before
    public void setUp() {
        tested = new FormGenerationModelProviderHelper(backendService);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerate_masharller() throws Exception {
        when(backendService.getDiagramMarshaller()).thenReturn(newMarshaller);

        tested.generate(diagram);

        verify(newMarshaller, times(1)).marshallToBpmn2Definitions(eq(diagram));
    }
}