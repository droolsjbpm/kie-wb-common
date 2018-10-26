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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;

public class ContextEntryPropertyConverter {

    public static ContextEntry wbFromDMN(final org.kie.dmn.model.api.ContextEntry dmn) {
        InformationItem variable = InformationItemPropertyConverter.wbFromDMN(dmn.getVariable());
        Expression expression = ExpressionPropertyConverter.wbFromDMN(dmn.getExpression());

        ContextEntry result = new ContextEntry();
        if (variable != null) {
            variable.setParent(result);
        }
        result.setVariable(variable);
        if (expression != null) {
            expression.setParent(result);
        }
        result.setExpression(expression);
        return result;
    }

    public static org.kie.dmn.model.api.ContextEntry dmnFromWB(final ContextEntry wb) {
        org.kie.dmn.model.api.ContextEntry result = new org.kie.dmn.model.v1_2.TContextEntry();

        org.kie.dmn.model.api.InformationItem variable = InformationItemPropertyConverter.dmnFromWB(wb.getVariable());
        org.kie.dmn.model.api.Expression expression = ExpressionPropertyConverter.dmnFromWB(wb.getExpression());

        result.setVariable(variable);
        result.setExpression(expression);
        return result;
    }
}
