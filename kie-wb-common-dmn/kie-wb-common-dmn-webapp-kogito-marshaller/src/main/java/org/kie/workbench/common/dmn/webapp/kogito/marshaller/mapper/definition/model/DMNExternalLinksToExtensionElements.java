/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinks;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.ExternalLink;

class DMNExternalLinksToExtensionElements {

    static void loadExternalLinksFromExtensionElements(final JSITDRGElement source,
                                                       final org.kie.workbench.common.dmn.api.definition.model.DRGElement target) {

        if (!Objects.isNull(source.getExtensionElements())) {
            final Object[] any = JSITDRGElement.JSIExtensionElements.getAny(source.getExtensionElements());
            if (!Objects.isNull(any)) {
                for (final Object obj : any) {
                    if (obj instanceof ExternalLink) {
                        final ExternalLink el = (ExternalLink) obj;
                        final DMNExternalLink external = new DMNExternalLink();
                        external.setDescription(el.getName());
                        external.setUrl(el.getUrl());
                        target.getLinksHolder().getValue().addLink(external);
                    }
                }
            }
        }
    }

    static void loadExternalLinksIntoExtensionElements(final org.kie.workbench.common.dmn.api.definition.model.DRGElement source,
                                                       final JSITDRGElement target) {

        if (Objects.isNull(source.getLinksHolder()) || Objects.isNull(source.getLinksHolder().getValue())) {
            return;
        }

        final DocumentationLinks links = source.getLinksHolder().getValue();
        final JSITDMNElement.JSIExtensionElements elements = getOrCreateExtensionElements(target);
        if (Objects.isNull(elements.getAny())) {
            elements.setAny(new Object[]{});
        }
        final List<Object> extensions = Arrays.asList(elements.getAny());
        removeAllExistingLinks(extensions);

        for (final DMNExternalLink link : links.getLinks()) {
            final ExternalLink external = new ExternalLink();
            external.setName(link.getDescription());
            external.setUrl(link.getUrl());
            extensions.add(external);
        }

        elements.setAny(extensions.toArray());
        target.setExtensionElements(elements);
    }

    private static void removeAllExistingLinks(final List<Object> extensions) {
        final List<Object> existingLinks = extensions.stream().filter(obj -> obj instanceof ExternalLink).collect(Collectors.toList());
        extensions.removeAll(existingLinks);
    }

    private static JSITDMNElement.JSIExtensionElements getOrCreateExtensionElements(final JSITDRGElement target) {
        return target.getExtensionElements() == null
                ? new JSITDMNElement.JSIExtensionElements()
                : target.getExtensionElements();
    }
}
