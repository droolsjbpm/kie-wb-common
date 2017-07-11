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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class StartEventShapeDef
        implements BPMNSvgShapeDef<BaseStartEvent> {

    private static final String EVENT_START = "eventStart";
    private static final String EVENT_START_SIGNAL = "eventStartSignal";
    private static final String EVENT_START_TIMER = "eventStartTimer";

    @Override
    public double getAlpha(final BaseStartEvent element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final BaseStartEvent element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final BaseStartEvent element) {
        return 1;
    }

    @Override
    public String getBorderColor(final BaseStartEvent element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final BaseStartEvent element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final BaseStartEvent element) {
        return 1;
    }

    @Override
    public String getFontFamily(final BaseStartEvent element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final BaseStartEvent element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public String getFontBorderColor(final BaseStartEvent element) {
        return element.getFontSet().getFontBorderColor().getValue();
    }

    @Override
    public double getFontSize(final BaseStartEvent element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final BaseStartEvent element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final BaseStartEvent element) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation(final BaseStartEvent element) {
        return 0;
    }

    private final static Map<Class<? extends BaseStartEvent>, SafeUri> ICONS = new HashMap<Class<? extends BaseStartEvent>, SafeUri>(2) {{
        put(StartNoneEvent.class,
            BPMNImageResources.INSTANCE.eventStart().getSafeUri());
        put(StartSignalEvent.class,
            BPMNImageResources.INSTANCE.eventStartSignal().getSafeUri());
        put(StartTimerEvent.class,
            BPMNImageResources.INSTANCE.eventStartTimer().getSafeUri());
    }};

    @Override
    public double getWidth(final BaseStartEvent element) {
        return element.getDimensionsSet().getRadius().getValue() * 2;
    }

    @Override
    public Glyph getGlyph(final Class<? extends BaseStartEvent> type) {
        return SvgDataUriGlyph.create(ICONS.get(type));
    }

    @Override
    public double getHeight(final BaseStartEvent element) {
        return element.getDimensionsSet().getRadius().getValue() * 2;
    }

    @Override
    public boolean isSVGViewVisible(final String viewName,
                                    final BaseStartEvent element) {
        switch (viewName) {
            case EVENT_START:
                return element instanceof StartNoneEvent;
            case EVENT_START_SIGNAL:
                return element instanceof StartSignalEvent;
            case EVENT_START_TIMER:
                return element instanceof StartTimerEvent;
        }
        return false;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseStartEvent startEvent) {
        return factory.eventStart(getWidth(startEvent),
                                  getHeight(startEvent),
                                  false);
    }

    @Override
    public Class<BPMNSVGViewFactory> getViewFactoryType() {
        return BPMNSVGViewFactory.class;
    }
}