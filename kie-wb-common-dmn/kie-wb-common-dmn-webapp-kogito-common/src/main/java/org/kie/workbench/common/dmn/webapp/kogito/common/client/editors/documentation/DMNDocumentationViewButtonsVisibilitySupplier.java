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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.appformer.client.context.EditorContextProvider;

import static org.appformer.client.context.Channel.VSCODE;

@Dependent
@Alternative
@ApplicationScoped
public class DMNDocumentationViewButtonsVisibilitySupplier extends org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationViewButtonsVisibilitySupplier {

    private final EditorContextProvider contextProvider;

    @Inject
    public DMNDocumentationViewButtonsVisibilitySupplier(final EditorContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public boolean isButtonsVisible() {
        return !Objects.equals(contextProvider.getChannel(), VSCODE);
    }
}
