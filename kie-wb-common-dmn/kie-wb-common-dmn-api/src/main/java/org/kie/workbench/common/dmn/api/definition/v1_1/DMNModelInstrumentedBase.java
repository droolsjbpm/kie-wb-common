/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;

public abstract class DMNModelInstrumentedBase implements DMNDefinition {

    //TODO {manstis} This should be a PropertySet
    private Map<String, String> nameSpaces = new HashMap<>();

    @NonPortable
    protected static abstract class BaseNodeBuilder<T extends DMNModelInstrumentedBase> implements Builder<T> {

    }

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public Map<String, String> getNsContext() {
        return nameSpaces;
    }
}
