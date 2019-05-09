/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.editors.included;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModelsService;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.backend.DMNMarshaller;
import org.kie.workbench.common.dmn.backend.common.DMNMarshallerImportsHelper;
import org.kie.workbench.common.dmn.backend.common.DMNPathsHelperImpl;
import org.kie.workbench.common.dmn.backend.definition.v1_1.ImportedItemDefinitionConverter;
import org.kie.workbench.common.dmn.backend.editors.common.DMNIncludeModelFactory;
import org.kie.workbench.common.dmn.backend.editors.common.DMNIncludedNodesFilter;
import org.kie.workbench.common.dmn.backend.editors.types.exceptions.DMNIncludeModelCouldNotBeCreatedException;
import org.uberfire.backend.vfs.Path;

@Service
public class DMNIncludedModelsServiceImpl implements DMNIncludedModelsService {

    private static Logger LOGGER = Logger.getLogger(DMNIncludedModelsServiceImpl.class.getName());

    private final DMNPathsHelperImpl pathsHelper;

    private final DMNIncludeModelFactory includeModelFactory;

    private final DMNIncludedNodesFilter includedNodesFilter;

    private final DMNMarshallerImportsHelper importsHelper;

    private final DMNMarshaller dmnMarshaller;

    @Inject
    public DMNIncludedModelsServiceImpl(final DMNPathsHelperImpl pathsHelper,
                                        final DMNIncludeModelFactory includeModelFactory,
                                        final DMNIncludedNodesFilter includedNodesFilter,
                                        final DMNMarshallerImportsHelper importsHelper,
                                        final DMNMarshaller dmnMarshaller) {
        this.pathsHelper = pathsHelper;
        this.includeModelFactory = includeModelFactory;
        this.includedNodesFilter = includedNodesFilter;
        this.importsHelper = importsHelper;
        this.dmnMarshaller = dmnMarshaller;
    }

    @PostConstruct
    public void init() {
        importsHelper.init(getMarshaller());
    }

    @Override
    public List<DMNIncludedModel> loadModels(final WorkspaceProject workspaceProject) {
        return getPaths(workspaceProject)
                .stream()
                .map(getPathDMNIncludeModelFunction())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<DMNIncludedNode> loadNodesFromImports(final WorkspaceProject workspaceProject,
                                                      final List<DMNIncludedModel> includedModels) {
        return getPaths(workspaceProject)
                .stream()
                .map(path -> includedNodesFilter.getNodesFromImports(path, includedModels))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDefinition> loadItemDefinitionsByNamespace(final WorkspaceProject workspaceProject,
                                                               final String modelName,
                                                               final String namespace) {
        return importsHelper
                .getImportedItemDefinitionsByNamespace(workspaceProject, modelName, namespace)
                .stream()
                .map(itemDefinition -> wbFromDMN(itemDefinition, modelName))
                .collect(Collectors.toList());
    }

    private Function<Path, DMNIncludedModel> getPathDMNIncludeModelFunction() {
        return path -> {
            try {
                return includeModelFactory.create(path);
            } catch (final DMNIncludeModelCouldNotBeCreatedException e) {
                LOGGER.warning("The 'DMNIncludedModel' could not be created for " + path.toURI());
                return null;
            }
        };
    }

    private List<Path> getPaths(final WorkspaceProject workspaceProject) {
        return pathsHelper.getDiagramsPaths(workspaceProject);
    }

    private org.kie.dmn.api.marshalling.DMNMarshaller getMarshaller() {
        return dmnMarshaller.getMarshaller();
    }

    ItemDefinition wbFromDMN(final org.kie.dmn.model.api.ItemDefinition itemDefinition,
                             final String modelName) {
        return ImportedItemDefinitionConverter.wbFromDMN(itemDefinition, modelName);
    }
}
