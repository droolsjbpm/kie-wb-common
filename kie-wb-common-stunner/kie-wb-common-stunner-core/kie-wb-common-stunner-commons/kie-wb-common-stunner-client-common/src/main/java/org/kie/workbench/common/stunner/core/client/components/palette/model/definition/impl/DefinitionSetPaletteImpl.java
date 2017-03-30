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

package org.kie.workbench.common.stunner.core.client.components.palette.model.definition.impl;

import java.util.List;

import org.kie.workbench.common.stunner.core.client.components.palette.model.AbstractPaletteDefinition;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;

public final class DefinitionSetPaletteImpl
        extends AbstractPaletteDefinition<DefinitionPaletteCategory>
        implements DefinitionSetPalette {

    private final String defSetId;

    public DefinitionSetPaletteImpl(final List<DefinitionPaletteCategory> categories,
                                    final String defSetId) {
        super(categories);
        this.defSetId = defSetId;
    }

    @Override
    public String getDefinitionSetId() {
        return defSetId;
    }
}
