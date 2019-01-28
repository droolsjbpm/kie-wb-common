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

package org.kie.workbench.common.dmn.backend.editors.types;

import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.workbench.common.dmn.api.editors.types.DMNValidationService;

public class DMNValidationServiceImpl implements DMNValidationService {

    @Override
    public boolean isValidVariableName(final String source) {
        return FEELParser.isVariableNameValid(source);
    }
}
