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

package org.kie.workbench.common.stunner.bpmn.forms.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;
import org.kie.workbench.common.stunner.bpmn.forms.validation.notification.NotificationValueValidator;

public class NotificationValueValidatorTest extends GWTTestCase {

    private NotificationValueValidator validator;

    private ConstraintValidatorContext context;

    private NotificationValue value;

    private List<String> errorMessages = new ArrayList<>();

    @Override
    public String getModuleName() {
        return "org.kie.workbench.common.stunner.bpmn.forms.validation.NotificationValueValidatorTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        validator = new NotificationValueValidator();
        value = new NotificationValue();
        context = new ConstraintValidatorContext() {
            @Override
            public void disableDefaultConstraintViolation() {
            }

            @Override
            public String getDefaultConstraintMessageTemplate() {
                return null;
            }

            @Override
            public ConstraintViolationBuilder buildConstraintViolationWithTemplate(String message) {
                errorMessages.add(message);
                return new ConstraintViolationBuilder() {
                    @Override
                    public NodeBuilderDefinedContext addNode(String name) {
                        return null;
                    }

                    @Override
                    public ConstraintValidatorContext addConstraintViolation() {
                        return context;
                    }
                };
            }
        };
    }

    @Test
    public void testEmptyNotificationValue(){
        boolean result = validator.isValid(new NotificationValue(), context);
        assertTrue(result);
    }

    @Test
    public void testNegativeExpiresAtNotificationValue(){
        NotificationValue value =  new NotificationValue();
        value.setExpiresAt("-1d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
    }

    @Test
    public void testIsTooBigExpiresAtNotificationValue(){
        NotificationValue value =  new NotificationValue();
        value.setExpiresAt("1111111111111111111111111111111111111111111d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
    }
}
