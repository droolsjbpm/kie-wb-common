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

package org.kie.workbench.common.stunner.shapes.client.view;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresContainerShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;

public abstract class AbstractHasSizeView<T extends AbstractHasSizeView> extends WiresContainerShapeView<T>
        implements HasSize<T> {

    public AbstractHasSizeView(final ViewEventType[] supportedEventTypes,
                               final MultiPath path) {
        super(supportedEventTypes, path);
    }

    @Override
    public T setSizeConstraints(double minWidth, double minHeight, double maxWidth, double maxHeight) {
        getPath().setSizeConstraints(new BoundingBox(minWidth,
                                                     minHeight,
                                                     maxWidth,
                                                     maxHeight));
        return cast();
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}
