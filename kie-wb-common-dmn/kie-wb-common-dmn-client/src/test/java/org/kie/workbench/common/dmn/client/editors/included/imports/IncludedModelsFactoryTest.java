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

package org.kie.workbench.common.dmn.client.editors.included.imports;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IncludedModelsFactoryTest {

    @Mock
    private ImportRecordEngine recordEngine;

    @Mock
    private IncludedModelsIndex includedModelsIndex;

    private IncludedModelsFactory factory;

    @Before
    public void setup() {
        factory = spy(new IncludedModelsFactory(recordEngine, includedModelsIndex));
    }

    @Test
    public void testMakeIncludedModels() {

        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final Name nameMock1 = mock(Name.class);
        final Name nameMock2 = mock(Name.class);
        final List<Import> imports = asList(import1, import2);
        final String name1 = "name1";
        final String name2 = "name2";
        final String path1 = "path1";
        final String path2 = "path2";
        final String uuid1 = "123";
        final String uuid2 = "456";
        final String[] uuids = {uuid1, uuid2};
        final String uri1 = "/src/main/kie/dmn/1";
        final String uri2 = "/src/main/kie/dmn/2";
        final Integer drgElementsCount1 = 2;
        final Integer drgElementsCount2 = 8;
        final Integer itemDefinitionsCount1 = 4;
        final Integer itemDefinitionsCount2 = 16;

        when(nameMock1.getValue()).thenReturn(name1);
        when(nameMock2.getValue()).thenReturn(name2);
        when(import1.getName()).thenReturn(nameMock1);
        when(import2.getName()).thenReturn(nameMock2);
        when(import1.getNamespace()).thenReturn(path1);
        when(import2.getNamespace()).thenReturn(path2);
        when(import1.getLocationURI()).thenReturn(new LocationURI(uri1));
        when(import2.getLocationURI()).thenReturn(new LocationURI(uri2));
        when(import1.getDrgElementsCount()).thenReturn(drgElementsCount1);
        when(import2.getDrgElementsCount()).thenReturn(drgElementsCount2);
        when(import1.getItemDefinitionsCount()).thenReturn(itemDefinitionsCount1);
        when(import2.getItemDefinitionsCount()).thenReturn(itemDefinitionsCount2);
        doAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                return uuids[count++];
            }
        }).when(factory).uuidWrapper();

        final List<IncludedModel> includedModels = factory.makeIncludedModels(imports);
        final IncludedModel includedModel1 = includedModels.get(0);
        final IncludedModel includedModel2 = includedModels.get(1);

        verify(includedModelsIndex).clear();
        verify(includedModelsIndex).index(includedModel1, import1);
        verify(includedModelsIndex).index(includedModel2, import2);
        assertEquals(2, includedModels.size());
        assertEquals(uuid1, includedModel1.getUUID());
        assertEquals(uuid2, includedModel2.getUUID());
        assertEquals(name1, includedModel1.getName());
        assertEquals(name2, includedModel2.getName());
        assertEquals(path1, includedModel1.getNamespace());
        assertEquals(path2, includedModel2.getNamespace());
        assertEquals(uri1, includedModel1.getPath());
        assertEquals(uri2, includedModel2.getPath());
        assertEquals(itemDefinitionsCount1, includedModel1.getDataTypesCount());
        assertEquals(itemDefinitionsCount2, includedModel2.getDataTypesCount());
        assertEquals(drgElementsCount1, includedModel1.getDrgElementsCount());
        assertEquals(drgElementsCount2, includedModel2.getDrgElementsCount());
        assertEquals(recordEngine, includedModel1.getRecordEngine());
        assertEquals(recordEngine, includedModel2.getRecordEngine());
    }
}
