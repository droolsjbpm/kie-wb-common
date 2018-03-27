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

package org.kie.workbench.common.stunner.client.widgets.canvas.actions;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextEditorBox;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.uberfire.client.mvp.UberElement;

public interface TextEditorBoxView extends UberElement<TextEditorBoxView.Presenter> {

    void show(final String name);

    void hide();

    boolean isVisible();

    interface Presenter extends TextEditorBox<AbstractCanvasHandler, Element> {

        void onSave();

        void onClose();

        void onChangeName(final String name);

        void onKeyPress(final int keyCode,
                        final boolean shiftKeyPressed,
                        final String value);

        String getNameValue();
    }
}
