/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Dependent
public class FieldRendererManager {

    @Inject
    private ManagedInstance<FieldRenderer> renderers;

    private static Map<String, FieldRenderer> availableRenderers = new HashMap<String, FieldRenderer>();

    static {
        Collection<SyncBeanDef<FieldRenderer>> renderers = IOC.getBeanManager().lookupBeans(FieldRenderer.class);
        for (SyncBeanDef<FieldRenderer> rendererDef : renderers) {
            FieldRenderer renderer = rendererDef.getInstance();
            if (renderer != null) {
                availableRenderers.put(renderer.getSupportedCode(),
                                       renderer);
            }
        }
    }

    public FieldRenderer getRendererForField(FieldDefinition fieldDefinition) {
        FieldRenderer def = availableRenderers.get(fieldDefinition.getFieldType().getTypeName());

        if (def != null) {
            return renderers.select(def.getClass()).get();
        }
        return null;
    }

    @PreDestroy
    public void destroy() {
        renderers.destroyAll();
    }
}
