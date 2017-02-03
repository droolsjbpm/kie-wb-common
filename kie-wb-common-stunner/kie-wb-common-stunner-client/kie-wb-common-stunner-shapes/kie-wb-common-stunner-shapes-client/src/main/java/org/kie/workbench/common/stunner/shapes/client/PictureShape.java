/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.shapes.client;

import org.kie.workbench.common.stunner.client.lienzo.shape.impl.LienzoShape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeWrapper;
import org.kie.workbench.common.stunner.shapes.client.view.PictureShapeView;

public class PictureShape
        extends ShapeWrapper<PictureShapeView, LienzoShape<PictureShapeView>> {

    private final LienzoShape<PictureShapeView> wrapped;

    public PictureShape(final PictureShapeView view) {
        this.wrapped = new LienzoShape<>(view);
    }

    @Override
    protected LienzoShape<PictureShapeView> getWrappedShape() {
        return wrapped;
    }
}
