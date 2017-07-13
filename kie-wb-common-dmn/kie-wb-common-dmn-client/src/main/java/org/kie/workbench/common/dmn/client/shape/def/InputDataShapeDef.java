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
package org.kie.workbench.common.dmn.client.shape.def;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.client.resources.DMNSVGViewFactory;
import org.kie.workbench.common.dmn.client.shape.DMNPictures;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGMutableShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class InputDataShapeDef extends AbstractShapeDef<InputData>
        implements SVGMutableShapeDef<InputData, DMNSVGViewFactory> {

    @Override
    public double getAlpha(final InputData element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final InputData element) {
        return element.getBackgroundSet().getBgColour().getValue();
    }

    @Override
    public double getBackgroundAlpha(final InputData element) {
        return 1;
    }

    @Override
    public String getBorderColor(final InputData element) {
        return element.getBackgroundSet().getBorderColour().getValue();
    }

    @Override
    public double getBorderSize(final InputData element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final InputData element) {
        return 1;
    }

    @Override
    public String getFontFamily(final InputData element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final InputData element) {
        return element.getFontSet().getFontColour().getValue();
    }

    @Override
    public double getFontSize(final InputData element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final InputData element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public String getFontBorderColor(final InputData element) {
        return null;
    }

    @Override
    public HasTitle.Position getFontPosition(final InputData element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final InputData element) {
        return 0;
    }

    private static final PictureGlyphDef<InputData, DMNPictures> INPUT_DATA_GLYPH_DEF = new PictureGlyphDef<InputData, DMNPictures>() {

        private final Map<Class<?>, DMNPictures> PICTURES = new HashMap<Class<?>, DMNPictures>() {{
            put(InputData.class,
                DMNPictures.INPUT_DATA);
        }};

        @Override
        public String getGlyphDescription(final InputData element) {
            return element.getStunnerDescription();
        }

        @Override
        public DMNPictures getSource(final Class<?> type) {
            return PICTURES.get(type);
        }
    };

    @Override
    public GlyphDef<InputData> getGlyphDef() {
        return INPUT_DATA_GLYPH_DEF;
    }

    @Override
    public double getWidth(final InputData element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final InputData element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public boolean isSVGViewVisible(final String viewName,
                                    final InputData element) {
        return true;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final DMNSVGViewFactory factory,
                                           final InputData task) {
        return factory.inputData(getWidth(task),
                                 getHeight(task),
                                 true);
    }

    @Override
    public Class<DMNSVGViewFactory> getViewFactoryType() {
        return DMNSVGViewFactory.class;
    }
}
