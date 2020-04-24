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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Arrays;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;

/**
 * <p>It provides a light validation for variable names, respect to the FEEL syntax</p>
 * <p>We already have a validation mechanism on back-end side, provided by <strong>FeelParser</strong></p>
 * <p>However, as for now, we cannot use back-end services in dmn client, so the aim of this class is to provide a more accurate as possible validation</p>
 * <p>Its purpose is to be temporary for the short/middle term</p>
 */
public class FEELSyntaxLightValidator {

    private static final Character[] FORBIDDEN_CHARS = new Character[]{
            '!', '@', '#', '$', '$', '%', '&', '^', '(', ')', '\"',
            '{', '}', '[', ']', '|', '\\', '"', '<', '>', ';', ':', ','
    };

    private static final Character[] FORBIDDEN_CHARS_AS_INITIAL = Stream.concat(
            of(FORBIDDEN_CHARS),
            of('-', '.', '/', '\'', '*', '+')
    ).toArray(Character[]::new);

    private static final String[] RESERVED_KEYWORDS = new String[]{
            "for", "return", "if", "then", "else", "some", "every", "satisfies", "instance", "of",
            "function", "external", "or", "and", "between", "not", "null", "true", "false"
    };

    public static boolean isVariableNameValid(final String variableName) {
        return notEmpty(variableName)
                && firstCharacterIsValid(variableName)
                && firstWordIsNotReservedKeyword(variableName)
                && doesNotContainForbiddenChars(variableName);
    }

    private static boolean notEmpty(final String variableName) {
        return variableName != null && !variableName.trim().isEmpty();
    }

    private static boolean firstCharacterIsValid(final String variableName) {
        final char firstLetter = variableName.charAt(0);
        return !Character.isDigit(firstLetter) && containsNone(firstLetter, FORBIDDEN_CHARS_AS_INITIAL);
    }

    private static boolean firstWordIsNotReservedKeyword(final String variableName) {
        return containsNone(variableName.split("[ \\-]")[0], RESERVED_KEYWORDS);
    }

    private static boolean doesNotContainForbiddenChars(final String variableName) {
        final String variableNameWithoutInitial = variableName.substring(1);
        return Stream.of(FORBIDDEN_CHARS)
                .map(c -> Character.toString(c))
                .noneMatch(variableNameWithoutInitial::contains);
    }

    private static <T> boolean containsNone(final T inputStr, final T[] items) {
        return Arrays.stream(items).noneMatch(inputStr::equals);
    }
}
