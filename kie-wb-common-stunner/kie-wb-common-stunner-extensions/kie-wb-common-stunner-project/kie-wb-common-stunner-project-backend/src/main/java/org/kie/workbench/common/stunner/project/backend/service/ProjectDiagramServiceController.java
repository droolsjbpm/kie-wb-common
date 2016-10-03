/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.backend.service;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.AbstractVFSDiagramService;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.StandardDeleteOption;

import javax.enterprise.inject.Instance;
import java.io.InputStream;
import java.util.Map;

// TODO: Metadata handling.
class ProjectDiagramServiceController extends AbstractVFSDiagramService<ProjectDiagram> {

    private static final Logger LOG =
            LoggerFactory.getLogger( ProjectDiagramServiceController.class.getName() );

    ProjectDiagramServiceController( final DefinitionManager definitionManager,
                                     final FactoryManager factoryManager,
                                     final Instance<DefinitionSetService> definitionSetServiceInstances,
                                     final IOService ioService,
                                     final BackendRegistryFactory registryFactory,
                                     final DiagramFactory diagramFactory ) {
        super( definitionManager, factoryManager, definitionSetServiceInstances, ioService, registryFactory, diagramFactory );
    }

    @Override
    protected void initialize() {
        // Initialize caches.
        super.initialize();
    }

    @Override
    protected InputStream loadMetadataForPath( Path path ) {
        return null;
    }

    public Path save( Path path,
                      ProjectDiagram diagram,
                      Map<String, ?> attributes,
                      OpenOption... comment ) {
        try {
            String[] raw = serizalize( diagram );
            getIoService().write( Paths.convert( path ), raw[ 0 ], attributes, comment );
        } catch ( Exception e ) {
            LOG.error( "Error while saving diagram with UUID [" + diagram.getName() + "].", e );
            throw new RuntimeException( e );
        }
        return path;
    }

    @Override
    protected void doSave( ProjectDiagram diagram, String raw, String metadata ) {
        try {
            Path _path = diagram.getMetadata().getPath();
            org.uberfire.java.nio.file.Path path = Paths.convert( _path );
            if ( !getIoService().exists( path ) ) {
                final org.uberfire.java.nio.file.Path parent = path.getParent();
                final String fileName = path.getFileName().toString();
                path = parent.resolve( fileName );
            }
            getIoService().write( path, raw );
        } catch ( Exception e ) {
            LOG.error( "Error while saving diagram with UUID [" + diagram.getName() + "].", e );
            throw new RuntimeException( e );
        }
    }

    // TODO: Use commit message.
    public boolean delete( Path _path, String message ) {
        final org.uberfire.java.nio.file.Path path = Paths.convert( _path );
        return getIoService().deleteIfExists( path, StandardDeleteOption.NON_EMPTY_DIRECTORIES );
    }

    @Override
    protected boolean doDelete( Path path ) {
        return delete( path, "" );
    }

}
