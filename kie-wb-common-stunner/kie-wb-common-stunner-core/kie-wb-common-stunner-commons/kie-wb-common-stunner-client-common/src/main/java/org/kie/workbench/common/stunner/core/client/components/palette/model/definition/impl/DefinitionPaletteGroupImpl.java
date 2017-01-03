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

import org.kie.workbench.common.stunner.core.client.components.palette.model.AbstractPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.model.AbstractPaletteGroupBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;

public final class DefinitionPaletteGroupImpl extends AbstractPaletteGroup<DefinitionPaletteItem> implements DefinitionPaletteGroup {

    private DefinitionPaletteGroupImpl( final String itemId,
                                        final String title,
                                        final String description,
                                        final String tooltip,
                                        final List<DefinitionPaletteItem> items,
                                        final String definitionId ) {
        super( itemId, title, description, tooltip, definitionId, items );

    }

    static class DefinitionPaletteGroupBuilder extends AbstractPaletteGroupBuilder<DefinitionPaletteGroupBuilder,
            DefinitionPaletteGroupImpl, DefinitionPaletteItem> {

        public DefinitionPaletteGroupBuilder( final String id ) {
            super( id );
        }

        @Override
        protected DefinitionPaletteGroupImpl doBuild( final List<DefinitionPaletteItem> items ) {
            return new DefinitionPaletteGroupImpl( id, title, description, tooltip, items, definitionId );
        }

    }

}
