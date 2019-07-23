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
package org.kie.workbench.common.dmn.showcase.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.BaseKogitoDMNDiagramEditor;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.SimpleNoExpandWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = BaseKogitoDMNDiagramEditor.PERSPECTIVE_ID, isDefault = true)
public class AuthoringPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(SimpleNoExpandWorkbenchPanelPresenter.class.getName());
        perspective.setName("Authoring");
        perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest(BaseKogitoDMNDiagramEditor.EDITOR_ID)));
        return perspective;
    }
}
