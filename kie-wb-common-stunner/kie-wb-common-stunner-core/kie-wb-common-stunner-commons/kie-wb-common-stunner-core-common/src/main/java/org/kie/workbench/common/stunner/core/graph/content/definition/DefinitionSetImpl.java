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
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class DefinitionSetImpl implements DefinitionSet {

    private String id;
    private Bounds bounds;

    public DefinitionSetImpl(final @MapsTo("id") String id) {
        this.id = id;
    }

    @Override
    public String getDefinition() {
        return id;
    }

    @Override
    public void setDefinition(final String definition) {
        this.id = definition;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(final Bounds bounds) {
        this.bounds = bounds;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id.hashCode(),
                                         bounds.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefinitionSet) {
            ViewConnector other = (ViewConnector) o;
            return id.equals(other.getDefinition()) &&
                    bounds.equals(other.getBounds());
        }
        return false;
    }
}
