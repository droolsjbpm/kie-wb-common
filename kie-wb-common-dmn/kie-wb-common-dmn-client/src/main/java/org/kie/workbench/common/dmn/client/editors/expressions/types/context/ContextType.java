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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;

@ApplicationScoped
public class ContextType implements ExpressionType<Context> {

    private ContextEditor editor;

    public ContextType() {
        //CDI proxy
    }

    @Inject
    public ContextType(final ContextEditor editor) {
        this.editor = editor;
    }

    @Override
    public int getIndex() {
        return 2;
    }

    @Override
    public String getName() {
        return Context.class.getSimpleName();
    }

    @Override
    public Optional<Context> getModelClass() {
        return Optional.of(new Context());
    }

    @Override
    public BaseExpressionEditorView.Editor<Context> getEditor() {
        return editor;
    }
}
