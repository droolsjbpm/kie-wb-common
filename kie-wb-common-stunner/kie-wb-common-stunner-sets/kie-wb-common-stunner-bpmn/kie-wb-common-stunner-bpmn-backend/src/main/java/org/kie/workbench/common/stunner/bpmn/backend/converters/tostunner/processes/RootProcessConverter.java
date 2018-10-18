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
package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes;

import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;

public class RootProcessConverter extends BaseRootProcessConverter<BPMNDiagramImpl> {

    public RootProcessConverter(TypedFactoryManager typedFactoryManager,
                                PropertyReaderFactory propertyReaderFactory,
                                DefinitionResolver definitionResolver,
                                BaseConverterFactory<BPMNDiagramImpl, ?, ?> factory) {
        super(typedFactoryManager, propertyReaderFactory, definitionResolver, factory);
    }

    @Override
    public Class<BPMNDiagramImpl> getDiagramClass() {
        return BPMNDiagramImpl.class;
    }
}
