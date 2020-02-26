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

package org.kie.workbench.common.dmn.api.property.dmn;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class NameHolderTest {

    private static final String NAME = "name";

    private NameHolder holder1;

    private NameHolder holder2;

    private NameHolder holder3;

    @Before
    public void setup() {
        this.holder1 = new NameHolder(new Name(NAME));
        this.holder2 = new NameHolder(new Name(NAME));
        this.holder3 = new NameHolder(new Name());
    }

    @Test
    public void testGetValue() {
        assertEquals(NAME, holder1.getValue().getValue());
    }

    @Test
    public void testSetValue() {
        holder1.setValue(new Name());
        assertEquals("", holder1.getValue().getValue());
    }

    @Test
    public void testEquals() {
        assertEquals(holder1, holder2);
        assertNotEquals(holder1, holder3);
        assertNotEquals(holder2, holder3);
    }

    @Test
    public void testHashCode() {
        assertEquals(holder1.hashCode(), holder2.hashCode());
        assertNotEquals(holder1.hashCode(), holder3.hashCode());
        assertNotEquals(holder2.hashCode(), holder3.hashCode());
    }
}
