/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.bpmn.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindDataTypesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.stunner.bpmn.service.DataTypesService;

@Service
public class FindDataTypesService implements DataTypesService {

    @Inject
    protected RefactoringQueryService queryService;

    public List<String> getDataTypeNames() {
        // Query Java resources
        List<RefactoringPageRow> results = queryService.query(
                FindDataTypesQuery.NAME,
                new HashSet<ValueIndexTerm>() {{
                    add(new ValueResourceIndexTerm("*",
                                                   ResourceType.JAVA,
                                                   ValueIndexTerm.TermSearchType.WILDCARD));
                }});
        final List<String> dataTypeNames = new ArrayList<String>();
        for (RefactoringPageRow row : results) {
            dataTypeNames.add((String) row.getValue());
        }
        Collections.sort(dataTypeNames);

        return dataTypeNames;
    }
}
