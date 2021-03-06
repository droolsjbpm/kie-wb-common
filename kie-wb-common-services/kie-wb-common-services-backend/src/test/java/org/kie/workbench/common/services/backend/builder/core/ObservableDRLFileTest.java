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
package org.kie.workbench.common.services.backend.builder.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.NoSuchFileException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class ObservableDRLFileTest {

    @Mock
    private IOService ioService;

    @Mock
    private Path path;

    @Mock
    private org.uberfire.java.nio.file.Path nioPath;

    private ObservableDRLFile observer;

    @Before
    public void setup() {
        this.observer = spy(new ObservableDRLFile(ioService));
        doReturn(nioPath).when(observer).convert(any(Path.class));
    }

    @Test
    public void testAcceptWithoutDRLFile() {
        doReturn("cheese.txt").when(path).getFileName();
        assertFalse(observer.accept(path));
    }

    @Test
    public void testAcceptWithDRLFileDeclaringTypeSingleLine() {
        doReturn("cheese." + ObservableDRLFile.EXTENSION).when(path).getFileName();
        doReturn("declare MyType end").when(ioService).readAllString(eq(nioPath));
        assertTrue(observer.accept(path));
    }

    @Test
    public void testAcceptWithDRLFileDeclaringTypeMultipleLines() {
        doReturn("cheese." + ObservableDRLFile.EXTENSION).when(path).getFileName();
        doReturn("declare MyType\nend").when(ioService).readAllString(eq(nioPath));
        assertTrue(observer.accept(path));
    }

    @Test
    public void testAcceptWithDRLFileDeclaringTypeMidFile() {
        doReturn("cheese." + ObservableDRLFile.EXTENSION).when(path).getFileName();
        doReturn("MyType declaration \ndeclare MyType\nend").when(ioService).readAllString(eq(nioPath));
        assertTrue(observer.accept(path));
    }

    @Test
    public void testAcceptWithDRLFileNotDeclaringType() {
        doReturn("cheese." + ObservableDRLFile.EXTENSION).when(path).getFileName();
        doReturn("rule test when then end").when(ioService).readAllString(eq(nioPath));
        assertFalse(observer.accept(path));
    }

    @Test
    public void testAcceptWithNoSuchFileException() {
        doThrow(new NoSuchFileException()).when(path).getFileName();
        assertFalse(observer.accept(path));
    }
}
