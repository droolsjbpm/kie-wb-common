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
package org.kie.workbench.common.screens.datamodeller.backend.server.indexing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class TestJavaFileIndexer extends JavaFileIndexer implements TestIndexer<JavaResourceTypeDefinition> {

    @Override
    public void setIOService( final IOService ioService ) {
        this.ioService = ioService;
    }

    @Override
    public void setProjectService( final KieProjectService projectService ) {
        this.projectService = projectService;
    }

    @Override
    public void setResourceTypeDefinition( final JavaResourceTypeDefinition type ) {
        this.javaResourceTypeDefinition = type;
    }

    @Override
    protected ClassLoader getProjectClassLoader( final KieProject project ) {
        //for testing purposes
        return this.getClass().getClassLoader();
    }

    @Override
    protected KieProject getProject( final Path path ) {
        final org.uberfire.backend.vfs.Path mockRoot = mock( org.uberfire.backend.vfs.Path.class );
        when( mockRoot.toURI() ).thenReturn( BaseIndexingTest.TEST_PROJECT_ROOT );

        final KieProject mockProject = mock( KieProject.class );
        when( mockProject.getRootPath() ).thenReturn( mockRoot );
        when( mockProject.getProjectName() ).thenReturn( BaseIndexingTest.TEST_PROJECT_NAME );
        return mockProject;
    }

    @Override
    protected Package getPackage( final Path path ) {
        final org.guvnor.common.services.project.model.Package mockPackage = mock( Package.class );
        when( mockPackage.getPackageName() ).thenReturn( BaseIndexingTest.TEST_PACKAGE_NAME );
        return mockPackage;
    }

}

