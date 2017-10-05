/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.toolbar.command;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToJpgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SessionCommandFactory;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExportToJpgToolbarCommandTest {

    @Mock
    private SessionCommandFactory sessionCommandFactory;

    @Mock
    private ExportToJpgSessionCommand sessionCommand;

    @Test
    public void testExport() {
        when(sessionCommandFactory.newExportToJpgSessionCommand()).thenReturn(sessionCommand);
        final ExportToJpgToolbarCommand tested = new ExportToJpgToolbarCommand(sessionCommandFactory);
        verify(sessionCommandFactory,
               times(1)).newExportToJpgSessionCommand();
        assertEquals(IconType.FILE_IMAGE_O,
                     tested.getIcon());
        assertNull(tested.getCaption());
    }
}
