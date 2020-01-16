/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.artifacts.widgets;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLOptionElement;

public class DataObjectType {

    HTMLOptionElement option;

    public DataObjectType(String name, String value, String role, boolean custom, boolean editable, boolean selected) {
        createOptionElement();
        option.text = name;
        option.value = value;
        setAttribute("value", value);
        setAttribute("role", role);
        setAttribute("custom", String.valueOf(custom));
        setAttribute("editable", String.valueOf(editable));
        option.selected = selected;
    }

    protected void createOptionElement() {
        option = (HTMLOptionElement) DomGlobal.document.createElement("option");
    }

    protected void setAttribute(String name, String value) {
        option.setAttribute(name, value);
    }

    public HTMLOptionElement asElement() {
        return option;
    }

}
