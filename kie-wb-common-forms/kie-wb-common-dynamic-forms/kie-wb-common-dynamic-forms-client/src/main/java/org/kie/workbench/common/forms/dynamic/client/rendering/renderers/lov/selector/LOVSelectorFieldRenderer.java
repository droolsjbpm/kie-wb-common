/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.RequiresValueConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.converters.LOVConvertersFactory;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.LOVSelectorInput;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.AbstractLOVSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.LOVSelectorFieldType;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchEntry;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchResults;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchService;
import org.uberfire.ext.widgets.common.client.dropdown.MultipleLiveSearchSelectionHandler;

@Dependent
public class LOVSelectorFieldRenderer<TYPE> extends FieldRenderer<AbstractLOVSelectorFieldDefinition, DefaultFormGroup> implements RequiresValueConverter {

    private LOVSelectorInput<TYPE> selector;

    private TranslationService translationService;

    private LiveSearchService<TYPE> searchService;

    @Inject
    public LOVSelectorFieldRenderer(LOVSelectorInput<TYPE> selector,
                                    TranslationService translationService) {
        this.selector = selector;
        this.translationService = translationService;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {

        DefaultFormGroup formGroup = formGroupsInstance.get();

        final List<TYPE> values = field.getListOfValues();

        searchService = (pattern, maxResults, callback) -> {

            List<TYPE> result = values;

            if (pattern != null & !pattern.isEmpty()) {
                result = values.stream()
                        .filter(value -> stringValue(value).toLowerCase().contains(pattern.toLowerCase()))
                        .collect(Collectors.toList());
            }

            LiveSearchResults<TYPE> entries = new LiveSearchResults<>();

            for (int i = 0; i < result.size() && i < maxResults; i++) {
                TYPE value = result.get(i);
                entries.add(new LiveSearchEntry<>(value,
                                                stringValue(value)));
            }

            callback.afterSearch(entries);
        };

        selector.init(searchService,
                      new MultipleLiveSearchSelectionHandler<>(field.getMaxElementsOnTitle()));
        selector.setMaxItems(field.getMaxDropdownElements());
        selector.setEnabled(!field.getReadOnly() && renderingContext.getRenderMode().equals(RenderMode.EDIT_MODE));
        selector.setFilterEnabled(field.getAllowFilter());
        selector.setClearSelectionEnabled(field.getAllowClearSelection());

        if (renderMode.equals(RenderMode.PRETTY_MODE)) {
            selector.setEnabled(false);
        }

        formGroup.render(selector.asWidget(),
                         field);

        return formGroup;
    }

    protected String stringValue(TYPE value) {
        if(value == null) {
            return "";
        }

        return value.toString();
    }

    @Override
    public String getName() {
        return LOVSelectorFieldType.NAME;
    }

    @Override
    public String getSupportedCode() {
        return LOVSelectorFieldType.NAME;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        selector.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        return LOVConvertersFactory.getConverter(field.getStandaloneClassName());
    }
}
