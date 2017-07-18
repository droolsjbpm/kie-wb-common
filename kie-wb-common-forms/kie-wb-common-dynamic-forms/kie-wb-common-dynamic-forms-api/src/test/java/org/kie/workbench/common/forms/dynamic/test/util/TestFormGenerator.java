/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.test.util;

import java.util.Date;
import java.util.stream.Collectors;

import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.test.model.Address;
import org.kie.workbench.common.forms.dynamic.test.model.Employee;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.PortableJavaModel;

public class TestFormGenerator {

    public static StaticModelFormRenderingContext getContextForEmployee(Employee employee) {
        FormDefinition form = getEmployeeForm();
        StaticModelFormRenderingContext context = new StaticModelFormRenderingContext();
        context.setRootForm(form);
        context.setModel(employee);
        context.getAvailableForms().put(form.getId(),
                                        form);

        form = getAddressForm();
        context.getAvailableForms().put(form.getId(),
                                        form);

        return context;
    }

    public static FormDefinition getEmployeeForm() {
        FormDefinition form = new FormDefinition();
        form.setName("Employee");
        form.setId("Employee");

        TextBoxFieldDefinition name = new TextBoxFieldDefinition();
        name.setId("name");
        name.setName("name");
        name.setLabel("Name");
        name.setPlaceHolder("Name");
        name.setBinding("name");
        name.setStandaloneClassName(String.class.getName());

        TextBoxFieldDefinition lastName = new TextBoxFieldDefinition();
        lastName.setId("surname");
        lastName.setName("surname");
        lastName.setLabel("Surname");
        lastName.setPlaceHolder("SurName");
        lastName.setBinding("surname");
        lastName.setStandaloneClassName(String.class.getName());

        DatePickerFieldDefinition birthday = new DatePickerFieldDefinition();
        birthday.setId("birthday");
        birthday.setName("birthday");
        birthday.setLabel("Birthday");
        birthday.setBinding("birthday");
        birthday.setStandaloneClassName(Date.class.getName());

        TextBoxFieldDefinition age = new TextBoxFieldDefinition();
        age.setId("age");
        age.setName("age");
        age.setLabel("Age");
        age.setPlaceHolder("age");
        age.setBinding("age.value");
        age.setStandaloneClassName(Integer.class.getName());

        CheckBoxFieldDefinition married = new CheckBoxFieldDefinition();
        married.setId("married");
        married.setName("married");
        married.setLabel("Married");
        married.setBinding("married");
        married.setStandaloneClassName(Boolean.class.getName());

        SubFormFieldDefinition address = new SubFormFieldDefinition();
        address.setId("address");
        address.setName("address");
        address.setLabel("Address");
        address.setBinding("address");
        address.setNestedForm("Address");
        address.setStandaloneClassName(Address.class.getName());

        form.getFields().add(name);
        form.getFields().add(lastName);
        form.getFields().add(birthday);
        form.getFields().add(age);
        form.getFields().add(married);
        form.getFields().add(address);

        form.setModel(generateModelFor(form));

        return form;
    }

    public static FormDefinition getAddressForm() {
        FormDefinition form = new FormDefinition();
        form.setName("Address");
        form.setId("Address");

        TextBoxFieldDefinition name = new TextBoxFieldDefinition();
        name.setId("street");
        name.setName("street");
        name.setLabel("Street Name");
        name.setPlaceHolder("Street Name");
        name.setBinding("street");
        name.setStandaloneClassName(String.class.getName());

        TextBoxFieldDefinition num = new TextBoxFieldDefinition();
        num.setId("num");
        num.setName("num");
        num.setLabel("#");
        num.setPlaceHolder("#");
        num.setBinding("num");
        num.setStandaloneClassName(Integer.class.getName());

        form.getFields().add(name);
        form.getFields().add(num);

        form.setModel(generateModelFor(form));

        return form;
    }

    public static FormModel generateModelFor(FormDefinition form) {
        PortableJavaModel model = new PortableJavaModel(Employee.class.getName());

        model.getProperties().addAll(form.getFields().stream().map(TestFormGenerator::generatePropertyFor).collect(Collectors.toList()));

        return model;
    }

    private static ModelProperty generatePropertyFor(FieldDefinition field) {
        return new ModelPropertyImpl(field.getBinding(), field.getFieldTypeInfo());
    }
}
