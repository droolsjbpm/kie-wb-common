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

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.shape.def.BPMNPictures;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGMutableShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class SubprocessShapeDef
        extends AbstractShapeDef<ReusableSubprocess>
        implements SVGMutableShapeDef<ReusableSubprocess, BPMNSVGViewFactory> {

    @Override
    public double getAlpha(final ReusableSubprocess element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final ReusableSubprocess element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final ReusableSubprocess element) {
        return 1;
    }

    @Override
    public String getBorderColor(final ReusableSubprocess element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final ReusableSubprocess element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final ReusableSubprocess element) {
        return 1;
    }

    @Override
    public String getFontFamily(final ReusableSubprocess element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final ReusableSubprocess element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize(final ReusableSubprocess element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final ReusableSubprocess element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final ReusableSubprocess element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final ReusableSubprocess element) {
        return 0;
    }

    @Override
    public double getWidth(final ReusableSubprocess element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final ReusableSubprocess element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public boolean isSVGViewVisible(final String viewName,
                                    final ReusableSubprocess element) {
        return false;
    }

    // TODO: Icon.
    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final ReusableSubprocess subprocess) {
        return factory.rectangle(getWidth(subprocess),
                                 getHeight(subprocess),
                                 true);
    }

    @Override
    public Class<BPMNSVGViewFactory> getViewFactoryType() {
        return BPMNSVGViewFactory.class;
    }

    @Override
    public GlyphDef<ReusableSubprocess> getGlyphDef() {
        return GLYPH_DEF;
    }

    private static final PictureGlyphDef<ReusableSubprocess, BPMNPictures> GLYPH_DEF = new PictureGlyphDef<ReusableSubprocess, BPMNPictures>() {
        @Override
        public BPMNPictures getSource(final Class<?> type) {
            return BPMNPictures.SUB_PROCESS;
        }

        @Override
        public String getGlyphDescription(final ReusableSubprocess element) {
            return element.getDescription();
        }
    };
}