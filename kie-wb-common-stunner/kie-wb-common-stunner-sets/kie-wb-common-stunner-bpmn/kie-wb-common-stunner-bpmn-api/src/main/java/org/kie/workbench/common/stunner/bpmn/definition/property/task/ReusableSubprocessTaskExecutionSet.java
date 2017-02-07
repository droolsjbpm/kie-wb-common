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
package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.type.CheckBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "BPMNProperties"),
        startElement = "calledElement"
)
public class ReusableSubprocessTaskExecutionSet implements BPMNPropertySet {

    @Name
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.stunner.bpmn.backend.dataproviders.CalledElementFormProvider")
    @FormField(
            type = ListBoxFieldType.class,
            labelKey = "calledElement"
    )
    @Valid
    protected CalledElement calledElement;

    @Property
    @FormField(
            type = CheckBoxFieldType.class,
            labelKey = "independent",
            afterElement = "calledElement"
    )
    @Valid
    private Independent independent;

    @Property
    @FormField(
            type = CheckBoxFieldType.class,
            labelKey = "waitForCompletion",
            afterElement = "independent"
    )
    @Valid
    private WaitForCompletion waitForCompletion;

    @Property
    @FormField(
            type = CheckBoxFieldType.class,
            labelKey = "isAsync",
            afterElement = "waitForCompletion"
    )
    @Valid
    private IsAsync isAsync;

    public ReusableSubprocessTaskExecutionSet() {
        this(new CalledElement(),
             new Independent(),
             new WaitForCompletion(),
             new IsAsync());
    }

    public ReusableSubprocessTaskExecutionSet(final @MapsTo("calledElement") CalledElement calledElement,
                                              final @MapsTo("independent") Independent independent,
                                              final @MapsTo("waitForCompletion") WaitForCompletion waitForCompletion,
                                              final @MapsTo("isAsync") IsAsync isAsync) {
        this.calledElement = calledElement;
        this.independent = independent;
        this.waitForCompletion = waitForCompletion;
        this.isAsync = isAsync;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public CalledElement getCalledElement() {
        return calledElement;
    }

    public Independent getIndependent() {
        return independent;
    }

    public WaitForCompletion getWaitForCompletion() {
        return waitForCompletion;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setCalledElement(final CalledElement calledElement) {
        this.calledElement = calledElement;
    }

    public void setIndependent(final Independent independent) {
        this.independent = independent;
    }

    public void setWaitForCompletion(final WaitForCompletion waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
    }

    public void setIsAsync(final IsAsync isAsync) {
        this.isAsync = isAsync;
    }
}
