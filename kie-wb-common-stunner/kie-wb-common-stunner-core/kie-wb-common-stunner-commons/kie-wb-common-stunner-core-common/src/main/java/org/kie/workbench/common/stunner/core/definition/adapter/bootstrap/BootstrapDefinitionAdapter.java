/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.definition.adapter.bootstrap;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;

import java.util.Set;

class BootstrapDefinitionAdapter implements DefinitionAdapter<Object> {

    private final AdapterRegistry adapterRegistry;

    BootstrapDefinitionAdapter( final AdapterRegistry adapterRegistry ) {
        this.adapterRegistry = adapterRegistry;
    }

    @Override
    public String getId( final Object pojo ) {
        return getWrapped( pojo ).getId( pojo );
    }

    @Override
    public Object getNameProperty( final Object pojo ) {
        return getWrapped( pojo ).getNameProperty( pojo );
    }

    @Override
    public String getCategory( final Object pojo ) {
        return getWrapped( pojo ).getCategory( pojo );
    }

    @Override
    public String getTitle( final Object pojo ) {
        return getWrapped( pojo ).getTitle( pojo );
    }

    @Override
    public String getDescription( final Object pojo ) {
        return getWrapped( pojo ).getDescription( pojo );
    }

    @Override
    public Set<String> getLabels( final Object pojo ) {
        return getWrapped( pojo ).getLabels( pojo );
    }

    @Override
    public Set<?> getPropertySets( final Object pojo ) {
        return getWrapped( pojo ).getPropertySets( pojo );
    }

    @Override
    public Set<?> getProperties( final Object pojo ) {
        return getWrapped( pojo ).getProperties( pojo );
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType( final Object pojo ) {
        return getWrapped( pojo ).getGraphFactoryType( pojo );
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accepts( final Class<?> type ) {
        return null != getWrapped( type );
    }

    @Override
    public boolean isPojoModel() {
        return false;
    }

    private DefinitionAdapter<Object> getWrapped( final Object pojo ) {
        return getWrapped( pojo.getClass() );
    }

    private DefinitionAdapter<Object> getWrapped( final Class<?> type ) {
        return adapterRegistry.getDefinitionAdapter( type );
    }

}
