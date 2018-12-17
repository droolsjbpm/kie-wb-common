/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.project.config;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.backend.organizationalunit.OrganizationalUnitFactoryImpl;
import org.uberfire.spaces.SpacesAPI;

@Alternative
public class MigrationOrganizationalUnitFactoryImpl extends OrganizationalUnitFactoryImpl {

    @Inject
    public MigrationOrganizationalUnitFactoryImpl(final MigrationRepositoryServiceImpl repositoryService,
                                                  final BackwardCompatibleUtil backward,
                                                  final SpacesAPI spacesAPI,
                                                  final MigrationConfigurationServiceImpl configurationService,
                                                  final MigrationConfigurationFactoryImpl configurationFactory) {
        super(repositoryService,
              backward,
              spacesAPI,
              configurationService,
              configurationFactory);
    }
}
