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

package org.kie.workbench.common.stunner.core.validation.impl;

import java.util.Collection;
import java.util.Collections;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelBeanValidatorTest {

    @Mock
    Validator beanValidator;

    @Mock
    Object bean1;

    @Mock
    ConstraintViolation violation1;

    private TestModelValidator tested;

    private class TestModelValidator extends AbstractModelBeanValidator {

        @Override
        protected Validator getBeanValidator() {
            return beanValidator;
        }
    }

    @Before
    public void setup() {
        final Path propertyPath = mock(Path.class);
        when(violation1.getMessage()).thenReturn("message1");
        when(violation1.getPropertyPath()).thenReturn(propertyPath);
        when(propertyPath.toString()).thenReturn("path1");
        this.tested = new TestModelValidator();
    }

    @Test
    public void testValidateBean1Success() {
        when(beanValidator.validate(eq(bean1))).thenReturn(Collections.emptySet());
        tested.validate(bean1,
                        this::assertNoViolations);
    }

    @Test
    public void testValidateBean1Failed() {
        when(beanValidator.validate(eq(bean1))).thenReturn(Collections.singleton(violation1));
        tested.validate(bean1,
                        v -> assertViolation(v,
                                             violation1));
    }

    private void assertViolation(final Collection<ModelBeanViolation> violations,
                                 final ConstraintViolation violation) {
        assertNotNull(violations);
        assertFalse(violations.isEmpty());
        final ModelBeanViolation modelBeanViolation = violations.iterator().next();
        assertEquals(Violation.Type.WARNING,
                     modelBeanViolation.getViolationType());
        assertEquals(violation.getMessage(),
                     modelBeanViolation.getMessage());
        assertEquals(violation.getPropertyPath().toString(),
                     modelBeanViolation.getPropertyPath());
    }

    private void assertNoViolations(final Collection<ModelBeanViolation> violations) {
        assertNotNull(violations);
        assertTrue(violations.isEmpty());
    }
}
