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

package org.kie.workbench.common.stunner.bpmn.project.backend.dataproviders;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRuleFlowNamesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.stunner.bpmn.backend.dataproviders.RuleFlowGroupFormDataProvider;

@Dependent
@Specializes
public class RuleFlowGroupFormProjectDataProvider extends RuleFlowGroupFormDataProvider {

    private final RefactoringQueryService queryService;
    private final Function<List<RefactoringPageRow>, TreeMap> resultToSelectorData;

    // CDI Proxy.
    protected RuleFlowGroupFormProjectDataProvider() {
        this(null, null);
    }

    @Inject
    public RuleFlowGroupFormProjectDataProvider(final RefactoringQueryService queryService) {
        this(queryService,
             DEFAULT_RESULT_CONVERTER);
    }

    RuleFlowGroupFormProjectDataProvider(final RefactoringQueryService queryService,
                                         final Function<List<RefactoringPageRow>, TreeMap> resultToSelectorData) {
        this.queryService = queryService;
        this.resultToSelectorData = resultToSelectorData;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Object, String> getRuleFlowGroupNames() {
        List<RefactoringPageRow> queryResult = queryService.query(
                FindRuleFlowNamesQuery.NAME,
                new Sets.Builder<ValueIndexTerm>()
                        .add(new ValueSharedPartIndexTerm("*",
                                                          PartType.RULEFLOW_GROUP,
                                                          ValueIndexTerm.TermSearchType.WILDCARD)).build()
        );
        return resultToSelectorData.apply(queryResult);
    }

    public static Function<List<RefactoringPageRow>, TreeMap> DEFAULT_RESULT_CONVERTER =
            rows -> rows.stream()
                    .map(RuleFlowGroupFormProjectDataProvider::getValue)
                    .filter(RuleFlowGroupFormProjectDataProvider::isNotEmpty)
                    .collect(Collectors.toMap(name -> name, name -> name, (oldValue, newValue) -> oldValue, TreeMap::new));

    @SuppressWarnings("unchecked")
    private static String getValue(final RefactoringPageRow row) {
        return ((Map<String, String>) row.getValue()).get("name");
    }

    private static boolean isNotEmpty(final String s) {
        return null != s && s.trim().length() > 0;
    }
}
