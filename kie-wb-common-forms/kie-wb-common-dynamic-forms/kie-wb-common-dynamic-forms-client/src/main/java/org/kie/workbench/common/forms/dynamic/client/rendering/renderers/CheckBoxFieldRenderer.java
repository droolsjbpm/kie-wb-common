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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.SimpleCheckBox;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;

@Dependent
public class CheckBoxFieldRenderer extends FieldRenderer<CheckBoxFieldDefinition> {

    private SimpleCheckBox checkbox;

    @Override
    public String getName() {
        return "CheckBox";
    }

    @Override
    public void initInputWidget() {
        checkbox = new SimpleCheckBox();
        checkbox.setEnabled(!field.getReadOnly() && renderingContext.getRenderMode().equals(RenderMode.EDIT_MODE));
    }

    @Override
    public IsWidget getInputWidget() {
        return checkbox;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        initInputWidget();
        return getInputWidget();
    }

    @Override
    public String getSupportedCode() {
        return CheckBoxFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        checkbox.setEnabled(!readOnly);
    }
}
