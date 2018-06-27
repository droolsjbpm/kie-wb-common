/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.property.dmn.DefaultValueUtilities;

public class FunctionDefaultValueUtilities {

    public static final String PREFIX = "p-";

    public static String getNewParameterName(final FunctionDefinition function) {
        return PREFIX + getMaxUnusedIndex(function);
    }

    private static long getMaxUnusedIndex(final FunctionDefinition function) {
        int maxIndex = 0;
        for (InformationItem informationItem : function.getFormalParameter()) {
            if (informationItem != null) {
                final Name name = informationItem.getName();
                final String value = name.getValue();
                final Optional<Integer> informationItemIndex = DefaultValueUtilities.extractIndex(value, PREFIX);
                if (informationItemIndex.isPresent()) {
                    maxIndex = Math.max(maxIndex, informationItemIndex.get());
                }
            }
        }
        return maxIndex + 1;
    }
}
