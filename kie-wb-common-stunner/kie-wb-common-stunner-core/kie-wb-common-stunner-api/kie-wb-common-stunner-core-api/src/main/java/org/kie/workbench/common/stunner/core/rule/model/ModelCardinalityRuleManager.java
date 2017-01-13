/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.rule.model;

import org.kie.workbench.common.stunner.core.rule.CardinalityRuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

/**
 * Manager for cardinality rules specific for the Stunner's domain model.
 */
public interface ModelCardinalityRuleManager extends CardinalityRuleManager {

    /**
     * It checks cardinality rules and evaluates if the given candidate role can be added or removed
     * as the given count value .for this role.
     * @param label The role/label to add or remove.
     * @param count The count value for this roles.
     * @param operation Add or remove
     */
    RuleViolations evaluate(final String label,
                            final int count,
                            final Operation operation);
}
