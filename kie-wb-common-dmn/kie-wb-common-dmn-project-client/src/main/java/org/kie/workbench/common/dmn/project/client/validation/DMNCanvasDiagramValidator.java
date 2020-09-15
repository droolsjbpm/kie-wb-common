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

package org.kie.workbench.common.dmn.project.client.validation;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasDiagramValidator;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationFailEvent;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationSuccessEvent;

@Dependent
@Specializes
public class DMNCanvasDiagramValidator extends CanvasDiagramValidator<AbstractCanvasHandler> {

    @Inject
    public DMNCanvasDiagramValidator(final @DMNEditor DMNClientDiagramValidator diagramValidator,
                                     final Event<CanvasValidationSuccessEvent> validationSuccessEvent,
                                     final Event<CanvasValidationFailEvent> validationFailEvent) {
        super(diagramValidator, validationSuccessEvent, validationFailEvent);
    }
}
