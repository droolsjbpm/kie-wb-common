/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.library.client.perspective;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.PerspectiveDefinitionOption;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class LibraryPerspectiveTest {

    @Mock
    private LibraryPlaces libraryPlaces;

    private LibraryPerspective perspective;

    @Before
    public void setup() {
        perspective = new LibraryPerspective(libraryPlaces);
    }

    @Test
    public void libraryRefreshesPlacesOnStartupTest() {
        perspective.onOpen();

        verify(libraryPlaces).refresh(any());
    }

    @Test
    public void libraryHidesDocksOnCloseTest() {
        perspective.onClose();

        verify(libraryPlaces).hideDocks();
    }

    @Test
    public void maximizationIsDisabledForLibrary() {
        final PerspectiveDefinition perspectiveDefinition = perspective.buildPerspective();

        assertTrue(perspectiveDefinition.hasOption(PerspectiveDefinitionOption.MAXIMIZATION_DISABLED));
    }
}