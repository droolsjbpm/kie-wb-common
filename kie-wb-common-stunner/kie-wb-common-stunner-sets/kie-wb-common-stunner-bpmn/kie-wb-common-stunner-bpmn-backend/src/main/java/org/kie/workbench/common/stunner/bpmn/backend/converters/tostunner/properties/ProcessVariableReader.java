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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;

class ProcessVariableReader {

    static String getProcessVariables(List<Property> properties) {
        return properties
                .stream()
                .map(ProcessVariableReader::toProcessVariableString)
                .collect(Collectors.joining(","));
    }

    private static String toProcessVariableString(Property p) {
        String processVariableName = getProcessVariableName(p);
        return Optional.ofNullable(p.getItemSubjectRef())
                .map(ItemDefinition::getStructureRef)
                .map(type -> processVariableName + ":" + type)
                .orElse(processVariableName);
    }

    public static String getProcessVariableName(Property p) {
        String name = p.getName();
        // legacy uses ID instead of name
        return name == null? p.getId() : name;
    }
}
