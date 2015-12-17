/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.project;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.backend.server.ProjectConfigurationContentHandler;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.project.ProjectImportsContent;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Service
@ApplicationScoped
public class ProjectImportsServiceImpl
        extends KieService<ProjectImportsContent>
        implements ProjectImportsService {

    protected IOService                          ioService;
    protected ProjectConfigurationContentHandler projectConfigurationContentHandler;

    public ProjectImportsServiceImpl() {
    }

    @Inject
    public ProjectImportsServiceImpl(@Named("ioStrategy") IOService ioService,
                                     ProjectConfigurationContentHandler projectConfigurationContentHandler) {

        this.ioService = ioService;
        this.projectConfigurationContentHandler = projectConfigurationContentHandler;
    }

    @Override
    public ProjectImportsContent loadContent(Path path) {
        return super.loadContent(path);
    }

    @Override
    protected ProjectImportsContent constructContent(Path path, Overview overview) {
        return new ProjectImportsContent(load(path),
                                         overview);
    }

    @Override
    public ProjectImports load(final Path path) {
        final String content = ioService.readAllString(Paths.convert(path));
        return projectConfigurationContentHandler.toModel(content);
    }

    @Override
    public Path save(final Path resource,
                     final ProjectImports projectImports,
                     final Metadata metadata,
                     final String comment) {
        try {
            ioService.write(Paths.convert(resource),
                            projectConfigurationContentHandler.toString(projectImports),
                            metadataService.setUpAttributes(resource,
                                                            metadata));

            //The pom.xml, kmodule.xml and project.imports are all saved from ProjectScreenPresenter
            //We only raise InvalidateDMOProjectCacheEvent and ResourceUpdatedEvent(pom.xml) events once
            //in POMService.save to avoid duplicating events (and re-construction of DMO).

            return resource;

        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }
}
