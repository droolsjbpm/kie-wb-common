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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldDefinition;

@Dependent
public class ComboBoxFieldRenderer
    extends AbstractComboBoxFieldRenderer<ComboBoxFieldDefinition> {

  public static final String TYPE_NAME = ComboBoxFieldDefinition.FIELD_TYPE.getTypeName();

  @Inject
  public ComboBoxFieldRenderer(final ComboBoxWidgetView comboBoxEditor) {
    super(comboBoxEditor);
  }

  @Override
  public String getName() {
    return TYPE_NAME;
  }

  @Override
  public String getSupportedCode() {
    return TYPE_NAME;
  }

  @Override
  public Class<ComboBoxFieldDefinition> getSupportedFieldDefinition() {
    return ComboBoxFieldDefinition.class;
  }
}
