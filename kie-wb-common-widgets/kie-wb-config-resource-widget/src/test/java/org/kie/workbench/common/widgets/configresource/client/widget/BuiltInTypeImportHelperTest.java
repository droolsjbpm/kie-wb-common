/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.configresource.client.widget;

import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BuiltInTypeImportHelperTest {

    @Test
    public void testIsImportRemovableJavaLang() {
        assertFalse("can not remove java.lang.*",
                    BuiltInTypeImportHelper.isImportRemovable(new Import("java.lang.Number")));
    }

    @Test
    public void testIsImportRemovableJavaUtil() {
        assertFalse("can not remove java.util.*",
                    BuiltInTypeImportHelper.isImportRemovable(new Import("java.util.ArrayList")));
    }

    @Test
    public void testIsImportRemovableRegular() {
        assertTrue("regular import can be removed*",
                   BuiltInTypeImportHelper.isImportRemovable(new Import("com.sample.Person")));
    }
}
