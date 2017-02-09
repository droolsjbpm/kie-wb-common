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
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.type.CheckBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeEditorFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "BPMNProperties"),
        startElement = "taskName"
)
public class UserTaskExecutionSet implements BPMNPropertySet {

    @Name
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FormField(
            labelKey = "taskName"
    )
    @Valid
    protected TaskName taskName;

    @Property
    @FormField(
            type = CheckBoxFieldType.class,
            labelKey = "isAsync",
            afterElement = "taskName"
    )
    @Valid
    private IsAsync isAsync;

    @Property
    @FormField(
            type = CheckBoxFieldType.class,
            labelKey = "skippable",
            afterElement = "isAsync"
    )
    @Valid
    private Skippable skippable;

    @Property
    @FormField(
            labelKey = "priority",
            afterElement = "skippable"
    )
    @Valid
    private Priority priority;

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            labelKey = "subject",
            afterElement = "skippable"
    )
    @Valid
    private Subject subject;

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            labelKey = "content",
            afterElement = "subject"
    )
    @Valid
    private Content content;

    @Property
    @FormField(
            labelKey = "description",
            afterElement = "content"
    )
    @Valid
    private Description description;

    @Property
    @FormField(
            type = AssigneeEditorFieldType.class,
            labelKey = "createdBy",
            afterElement = "description",
            settings = @FieldParam(name = "type", value = "USER")
    )
    @Valid
    private CreatedBy createdBy;

    @Property
    @FormField(
            type = CheckBoxFieldType.class,
            labelKey = "adHocAutostart",
            afterElement = "createdBy"
    )
    @Valid
    private AdHocAutostart adHocAutostart;

    @Property
    @FormField(
            type = ListBoxFieldType.class,
            labelKey = "scriptLanguage",
            afterElement = "adHocAutostart"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.stunner.bpmn.backend.dataproviders.ScriptLanguageFormProvider")
    @Valid
    protected ScriptLanguage scriptLanguage;

    public UserTaskExecutionSet() {
        this(new TaskName("Task"),
             new IsAsync(),
             new Skippable(),
             new Priority(""),
             new Subject(""),
             new Content(""),
             new Description(""),
             new CreatedBy(),
             new AdHocAutostart(),
             new ScriptLanguage(""));
    }

    public UserTaskExecutionSet(final @MapsTo("taskName") TaskName taskName,
                                final @MapsTo("isAsync") IsAsync isAsync,
                                final @MapsTo("skippable") Skippable skippable,
                                final @MapsTo("priority") Priority priority,
                                final @MapsTo("subject") Subject subject,
                                final @MapsTo("content") Content content,
                                final @MapsTo("description") Description description,
                                final @MapsTo("createdBy") CreatedBy createdBy,
                                final @MapsTo("adHocAutostart") AdHocAutostart adHocAutostart,
                                final @MapsTo("scriptLanguage") ScriptLanguage scriptLanguage) {
        this.taskName = taskName;
        this.isAsync = isAsync;
        this.skippable = skippable;
        this.priority = priority;
        this.subject = subject;
        this.content = content;
        this.description = description;
        this.createdBy = createdBy;
        this.adHocAutostart = adHocAutostart;
        this.scriptLanguage = scriptLanguage;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public TaskName getTaskName() {
        return taskName;
    }

    public void setTaskName(final TaskName taskName) {
        this.taskName = taskName;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    public Skippable getSkippable() {
        return skippable;
    }

    public void setSkippable(Skippable skippable) {
        this.skippable = skippable;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public AdHocAutostart getAdHocAutostart() {
        return adHocAutostart;
    }

    public void setAdHocAutostart(AdHocAutostart adHocAutostart) {
        this.adHocAutostart = adHocAutostart;
    }

    public ScriptLanguage getScriptLanguage() {
        return scriptLanguage;
    }

    public void setScriptLanguage(ScriptLanguage scriptLanguage) {
        this.scriptLanguage = scriptLanguage;
    }
}
