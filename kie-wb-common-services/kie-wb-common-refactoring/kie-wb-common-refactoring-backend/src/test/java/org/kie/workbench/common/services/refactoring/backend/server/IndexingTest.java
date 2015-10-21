/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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

package org.kie.workbench.common.services.refactoring.backend.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.guvnor.common.services.project.model.Package;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.ext.metadata.io.IndexersFactory;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public abstract class IndexingTest<T extends ResourceTypeDefinition> {

    public static final String TEST_PROJECT_ROOT = "/a/mock/project/root";

    public static final String TEST_PACKAGE_NAME = "org.kie.workbench.mock.package";

    private IOService ioService = null;
    private static LuceneConfig config;

    protected static final List<File> tempFiles = new ArrayList<File>();

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for ( final File tempFile : tempFiles ) {
            FileUtils.deleteQuietly( tempFile );
        }
    }

    protected static LuceneConfig getConfig() {
        return config;
    }

    protected abstract TestIndexer<T> getIndexer();

    protected abstract Map<String, Analyzer> getAnalyzers();

    protected abstract T getResourceTypeDefinition();

    protected void loadProperties( final String fileName,
                                   final Path basePath ) throws IOException {
        final Path path = basePath.resolve( fileName );
        final Properties properties = new Properties();
        properties.load( this.getClass().getResourceAsStream( fileName ) );
        ioService().write( path,
                           propertiesToString( properties ) );
    }

    protected String loadText( final String fileName ) throws IOException {
        final BufferedReader br = new BufferedReader( new InputStreamReader( this.getClass().getResourceAsStream( fileName ) ) );
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while ( line != null ) {
                sb.append( line );
                sb.append( System.getProperty( "line.separator" ) );
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    protected String propertiesToString( final Properties properties ) {
        final StringBuilder sb = new StringBuilder();
        for ( String name : properties.stringPropertyNames() ) {
            sb.append( name ).append( "=" ).append( properties.getProperty( name ) ).append( "\n" );
        }
        return sb.toString();
    }

    protected IOService ioService() {
        if ( ioService == null ) {
            final Map<String, Analyzer> analyzers = getAnalyzers();
            config = new LuceneConfigBuilder()
                    .withInMemoryMetaModelStore()
                    .usingAnalyzers( analyzers )
                    .useDirectoryBasedIndex()
                    .useInMemoryDirectory()
                    .build();

            ioService = new IOServiceIndexedImpl( config.getIndexEngine() );
            final TestIndexer indexer = getIndexer();
            IndexersFactory.clear();
            IndexersFactory.addIndexer( indexer );

            //Mock CDI injection and setup
            indexer.setIOService( ioService );
            indexer.setProjectService( getProjectService() );
            indexer.setResourceTypeDefinition( getResourceTypeDefinition() );
        }
        return ioService;
    }

    public void dispose() {
        ioService().dispose();
        ioService = null;
    }

    protected KieProjectService getProjectService() {
        final org.uberfire.backend.vfs.Path mockRoot = mock( org.uberfire.backend.vfs.Path.class );
        when( mockRoot.toURI() ).thenReturn( TEST_PROJECT_ROOT );

        final KieProject mockProject = mock( KieProject.class );
        when( mockProject.getRootPath() ).thenReturn( mockRoot );

        final Package mockPackage = mock( Package.class );
        when( mockPackage.getPackageName() ).thenReturn( TEST_PACKAGE_NAME );

        final KieProjectService mockProjectService = mock( KieProjectService.class );
        when( mockProjectService.resolveProject( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( mockProject );
        when( mockProjectService.resolvePackage( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( mockPackage );

        return mockProjectService;
    }

    protected void assertContains( final List<KObject> results,
                                   final Path path ) {
        for ( KObject kObject : results ) {
            final String key = kObject.getKey();
            final String fileName = path.getFileName().toString();
            if ( key.endsWith( fileName ) ) {
                return;
            }
        }
        fail( "Results do not contain expected Path '" + path.toUri().toString() );
    }

    protected static File createTempDirectory() throws IOException {
        final File temp = File.createTempFile( "temp", Long.toString( System.nanoTime() ) );
        if ( !( temp.delete() ) ) {
            throw new IOException( "Could not delete temp file: " + temp.getAbsolutePath() );
        }
        if ( !( temp.mkdir() ) ) {
            throw new IOException( "Could not create temp directory: " + temp.getAbsolutePath() );
        }
        tempFiles.add( temp );
        return temp;
    }

}
