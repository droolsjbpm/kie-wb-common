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

package org.kie.workbench.common.stunner.core.graph.content.view;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public final class ViewImpl<W> implements View<W> {

    protected W definition;
    protected Bounds bounds;

    public ViewImpl(final @MapsTo("definition") W definition,
                    final @MapsTo("bounds") Bounds bounds) {
        this.definition = definition;
        this.bounds = bounds;
    }

    @Override
    public W getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(final W definition) {
        this.definition = definition;
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
        return HashUtil.combineHashCodes(definition == null ? 0 : definition.hashCode(),
                                         bounds == null ? 0 : bounds.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof View) {
            View other = (View) o;
            return (definition == null ? other.getDefinition() == null : definition.equals(other.getDefinition())) &&
                    (bounds == null ? other.getBounds() == null : bounds.equals(other.getBounds()));
        }
        return false;
    }
}
