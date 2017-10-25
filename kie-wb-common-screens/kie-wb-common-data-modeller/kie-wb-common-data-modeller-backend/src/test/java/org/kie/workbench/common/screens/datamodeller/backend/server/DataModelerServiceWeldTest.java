/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.vfs.Path;
import t1p1.Pojo1;
import t1p2.Pojo2;

import static org.junit.Assert.*;

/**
 * Tests for DataModelService
 */
public class DataModelerServiceWeldTest extends AbstractDataModelerServiceWeldTest {

    @Test
    public void testDataModelerService() throws Exception {

        final URL packageUrl = this.getClass().getResource( "/DataModelerTest1" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        KieProject project = projectService.resolveProject( packagePath );
        lruProjectDependenciesClassLoaderCache.assertDependenciesClassLoader(project, "system");

        DataModel dataModelOriginal = new DataModelTestUtil( systemAnnotations ).createModel( Pojo1.class,
                                                                                              Pojo2.class );

        org.kie.workbench.common.services.datamodeller.core.DataModel dataModel = dataModelService.loadModel( project );
        Map<String, DataObject> objectsMap = new HashMap<String, DataObject>();

        assertNotNull( dataModel );

        assertEquals( dataModelOriginal.getDataObjects().size(),
                      dataModel.getDataObjects().size() );

        for ( DataObject dataObject : dataModel.getDataObjects() ) {
            objectsMap.put( dataObject.getClassName(),
                            dataObject );
        }

        for ( DataObject dataObject : dataModelOriginal.getDataObjects() ) {
            org.kie.workbench.common.services.datamodeller.DataModelerAssert.assertEqualsDataObject( dataObject,
                                                                                                     objectsMap.get( dataObject.getClassName() ) );
        }
    }
}
