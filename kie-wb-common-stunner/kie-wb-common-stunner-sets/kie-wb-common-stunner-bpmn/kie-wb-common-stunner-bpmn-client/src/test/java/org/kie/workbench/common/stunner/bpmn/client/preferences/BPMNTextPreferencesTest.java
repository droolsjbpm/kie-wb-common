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

package org.kie.workbench.common.stunner.bpmn.client.preferences;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BPMNTextPreferencesTest {

    private BPMNTextPreferences tested;

    @Before
    public void setUp() throws Exception {
        tested = new BPMNTextPreferences();
    }

    @Test
    public void testAttributes() {
        //values from CSS -> BPMNSVGViewFactory#PATH_CSS
        assertEquals(tested.getTextAlpha(), 1, 0);
        assertEquals(tested.getTextFillColor(), "#000000");
        assertEquals(tested.getTextFontFamily(), "Open Sans");
        assertEquals(tested.getTextStrokeColor(), "#393f44");
        assertEquals(tested.getTextStrokeWidth(), 1, 0);
        assertEquals(tested.getTextFontSize(), 12, 0);
    }
}