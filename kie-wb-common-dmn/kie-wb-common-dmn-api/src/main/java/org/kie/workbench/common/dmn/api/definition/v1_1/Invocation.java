/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Label;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@Portable
public class Invocation extends Expression {

    private Expression expression;
    private List<Binding> binding;

    public Invocation() {
    }

    public Invocation(final @MapsTo("id") Id id,
                      final @MapsTo("label") Label label,
                      final @MapsTo("description") Description description,
                      final @MapsTo("typeRef") QName typeRef,
                      final @MapsTo("expression") Expression expression,
                      final @MapsTo("binding") List<Binding> binding) {
        super(id,
              label,
              description,
              typeRef);
        this.expression = expression;
        this.binding = binding;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(final Expression value) {
        this.expression = value;
    }

    public List<Binding> getBinding() {
        if (binding == null) {
            binding = new ArrayList<>();
        }
        return this.binding;
    }
}
