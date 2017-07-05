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

package org.kie.workbench.common.stunner.bpmn.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

public interface BPMNImageResources extends ClientBundleWithLookup {

    public static final BPMNImageResources INSTANCE = GWT.create(BPMNImageResources.class);
    public static final String SEQUENCE_FLOW = "images/connectors/sequence.svg";

    // ****** BPMN ShapeSet Thumbnail. *******
    @Source("images/bpmn_thumb.png")
    DataResource bpmnSetThumb();

    // ******* Categories *******
    @ClientBundle.Source("images/categories/activity.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryActivity();

    @ClientBundle.Source("images/categories/container.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryContainer();

    @ClientBundle.Source("images/categories/gateway.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryGateway();

    @ClientBundle.Source("images/categories/event.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryEvent();

    @ClientBundle.Source("images/categories/library.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryLibrary();

    @ClientBundle.Source("images/categories/sequence.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categorySequence();

    // ******* Task *******
    @ClientBundle.Source(BPMNSVGViewFactory.PATH_TASK)
    @DataResource.MimeType("image/svg+xml")
    DataResource task();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_TASK_USER)
    @DataResource.MimeType("image/svg+xml")
    DataResource taskUser();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_TASK_SCRIPT)
    @DataResource.MimeType("image/svg+xml")
    DataResource taskScript();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_TASK_BUSINESS_RULE)
    @DataResource.MimeType("image/svg+xml")
    DataResource taskBusinessRule();

    @ClientBundle.Source("images/task/task-manual.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskManual();

    @ClientBundle.Source("images/task/task-service.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskService();

    // ******* Event *******
    @ClientBundle.Source(BPMNSVGViewFactory.PATH_EVENT_END)
    @DataResource.MimeType("image/svg+xml")
    DataResource eventEnd();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_EVENT_END_TERMINATE)
    @DataResource.MimeType("image/svg+xml")
    DataResource eventEndTerminate();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_EVENT_INTERMEDIATE)
    @DataResource.MimeType("image/svg+xml")
    DataResource eventIntermediate();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_EVENT_START)
    @DataResource.MimeType("image/svg+xml")
    DataResource eventStart();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_EVENT_SIGNAL)
    @DataResource.MimeType("image/svg+xml")
    DataResource eventSignal();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_EVENT_TIMER)
    @DataResource.MimeType("image/svg+xml")
    DataResource eventTimer();

    // ******* Gateway *******
    @ClientBundle.Source("images/gateway/parallel-event.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayParallelEvent();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_GATEWAY_PARALLEL_MULTIPLE)
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayParallelMultiple();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_GATEWAY_EXCLUSIVE)
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayExclusive();

    @ClientBundle.Source("images/gateway/complex.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayComplex();

    @ClientBundle.Source("images/gateway/event.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayEvent();

    @ClientBundle.Source("images/gateway/inclusive.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayInclusive();

    // ******* Containers *******

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_LANE)
    @DataResource.MimeType("image/svg+xml")
    DataResource lane();

    @ClientBundle.Source("images/lane/lane_icon.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource laneIcon();

    // ******* Subprocesses *******
    @ClientBundle.Source(BPMNSVGViewFactory.PATH_SUBPROCESS)
    @DataResource.MimeType("image/svg+xml")
    DataResource subProcess();

    //
    @ClientBundle.Source("images/subprocess/subprocess-glyph.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource subProcessGlyph();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_SUBPROCESS_REUSABLE)
    @DataResource.MimeType("image/svg+xml")
    DataResource subProcessReusable();

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_SUBPROCESS_ADHOC)
    @DataResource.MimeType("image/svg+xml")
    DataResource subProcessAdHoc();

    // ******* Connectors *******

    @ClientBundle.Source(SEQUENCE_FLOW)
    @DataResource.MimeType("image/svg+xml")
    DataResource sequenceFlow();

    // ******* Misc *******

    @ClientBundle.Source(BPMNSVGViewFactory.PATH_RECTANGLE)
    @DataResource.MimeType("image/svg+xml")
    DataResource rectangle();

    @ClientBundle.Source("images/misc/circle.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource cagetoryEvents();

    @ClientBundle.Source("images/cancel.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource cancel();

    @ClientBundle.Source("images/clock-o.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource clockO();

    @ClientBundle.Source("images/plus-square.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource plusSquare();

    //This is a hack for OOME related to SVG, or image/svg+xml;base64 URLs
    @Source("images/glyph-oome-hack.png")
    ImageResource glyphOOMEHack();
}