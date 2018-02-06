/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.content.definition;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class DefinitionImpl<T> implements Definition<T> {

    protected T definition;

    public DefinitionImpl(final @MapsTo("definition") T definition) {
        this.definition = definition;
    }

    @Override
    public T getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(final T definition) {
        this.definition = definition;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(definition == null ? 0 : definition.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Definition) {
            Object d = ((Definition) obj).getDefinition();
            return this.definition == null ? d == null : this.definition.equals(d);
        }
        return false;
    }
}
