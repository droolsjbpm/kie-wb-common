/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas;

import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.uberfire.mvp.Command;

public interface Layer<T, S, A> extends HasEventHandlers<T, A> {

    T initialize(final Object view);

    T addShape(final S shape);

    T removeShape(final S shape);

    T draw();

    Transform getTransform();

    void clear();

    String toDataURL();

    String toDataURL(final int x,
                     final int y,
                     final int width,
                     final int height);

    void onAfterDraw(Command callback);

    void destroy();
}
