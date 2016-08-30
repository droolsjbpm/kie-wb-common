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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import java.net.URI;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;

/**
 * Helper methods to be used by the different services related to data sources and drivers.
 */
@ApplicationScoped
public class DataSourceServicesHelper {

    private static final Logger logger = LoggerFactory.getLogger( DataSourceServicesHelper.class );

    @Inject
    @Named( "ioStrategy" )
    private IOService ioService;

    /**
     *  Root to the platform data sources and drivers repository.
     */
    private org.uberfire.java.nio.file.Path root;

    /**
     * Filesystem that will hold the platform data sources. Platform data sources has a global scope instead of belong
     * to a given project.
     */
    private FileSystem fileSystem;

    public DataSourceServicesHelper() {
    }

    @PostConstruct
    protected void init() {
        String repositoryURI = null;
        try {
            repositoryURI = "default://" + getGlobalFileSystemName();
            fileSystem = ioService.newFileSystem( URI.create( repositoryURI ),
                    new HashMap<String, Object>() {{
                        put( "init", Boolean.TRUE );
                        put( "internal", Boolean.TRUE );
                    }} );

            logger.debug( "Data sources platform repository: {} was successfully created.", repositoryURI );

        } catch ( FileSystemAlreadyExistsException e ) {
            logger.debug( "Data sources platform repository: {} already exits and will be used.", repositoryURI );
            fileSystem = ioService.getFileSystem( URI.create( repositoryURI ) );
        }
        this.root = fileSystem.getRootDirectories().iterator().next();
    }

    /**
     * Returns the path where platform global data sources and drivers al located.
     */
    public Path getGlobalDataSourcesContext() {
        return Paths.convert( root );
    }

    /**
     * Returns the path where data sources and drivers are located for a given project.
     */
    public Path getProjectDataSourcesContext( final Project project ) {
        Path rootPath = project.getRootPath();
        org.uberfire.java.nio.file.Path dataSourcesNioPath = Paths.convert( rootPath ).resolve( "src/main/resources/META-INF" );
        return Paths.convert( dataSourcesNioPath );
    }

    private String getGlobalFileSystemName() {
        String name = System.getProperty( "org.kie.workbench.datasource-filesystem" );
        if ( name == null || "".equals( name ) ) {
            name = "datasources";
        }
        return name;
    }

}
