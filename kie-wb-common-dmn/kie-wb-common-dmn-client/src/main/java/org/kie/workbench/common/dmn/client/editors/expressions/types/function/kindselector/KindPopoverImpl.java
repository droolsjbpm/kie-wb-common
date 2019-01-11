/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;

@ApplicationScoped
public class KindPopoverImpl implements KindPopoverView.Presenter {

    private KindPopoverView view;
    private Optional<HasKindSelectControl> binding = Optional.empty();

    public KindPopoverImpl() {
        //CDI proxy
    }

    @Inject
    public KindPopoverImpl(final KindPopoverView view) {
        this.view = view;
        view.init(this);
        view.setFunctionKinds(FunctionDefinition.Kind.values());
    }

    @Override
    public void show(final Optional<String> editorTitle) {
        binding.ifPresent(b -> view.show(editorTitle));
    }

    @Override
    public void hide() {
        binding.ifPresent(b -> view.hide());
    }

    @Override
    public void bind(final HasKindSelectControl bound, final int uiRowIndex, final int uiColumnIndex) {
        binding = Optional.ofNullable(bound);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void onFunctionKindSelected(final FunctionDefinition.Kind kind) {
        binding.ifPresent(b -> {
            b.setFunctionKind(kind);
            view.hide();
        });
    }
}