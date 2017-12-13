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

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;

import static java.util.Objects.nonNull;

public class SubprocessShapeDef
        implements BPMNSvgShapeDef<BaseSubprocess> {

    public static final SVGShapeViewResources<BaseSubprocess, BPMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<BaseSubprocess, BPMNSVGViewFactory>()
                    .put(ReusableSubprocess.class, BPMNSVGViewFactory::reusableSubProcess)
                    .put(EmbeddedSubprocess.class, BPMNSVGViewFactory::embeddedSubProcess);

    public static final Map<Class<? extends BaseSubprocess>, SvgDataUriGlyph> GLYPHS =
            new HashMap<Class<? extends BaseSubprocess>, SvgDataUriGlyph>() {{
                put(ReusableSubprocess.class, BPMNSVGGlyphFactory.REUSABLE_SUBPROCESS_GLYPH);
                put(EmbeddedSubprocess.class, BPMNSVGGlyphFactory.ADHOC_SUBPROCESS_GLYPH);
            }};

    @Override
    public FontHandler<BaseSubprocess, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .positon(SubprocessShapeDef::getSubprocessTextPosition)
                .build();
    }

    @Override
    public SizeHandler<BaseSubprocess, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .width(task -> task.getDimensionsSet().getWidth().getValue())
                .height(task -> task.getDimensionsSet().getHeight().getValue())
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseSubprocess task) {
        Double width = (nonNull(task.getDimensionsSet().getWidth()) ? task.getDimensionsSet().getWidth().getValue() : null);
        Double height = (nonNull(task.getDimensionsSet().getHeight()) ? task.getDimensionsSet().getHeight().getValue() : null);
        SVGShapeViewResource resource = VIEW_RESOURCES.getResource(factory, task);
        return (nonNull(width) && nonNull(height) ? resource.build(width, height, true) : resource.build(true));
    }

    @Override
    public Glyph getGlyph(final Class<? extends BaseSubprocess> type) {
        return GLYPHS.get(type);
    }

    private static HasTitle.Position getSubprocessTextPosition(final BaseSubprocess bean) {
        return bean instanceof EmbeddedSubprocess ? HasTitle.Position.BOTTOM : HasTitle.Position.CENTER;
    }
}
