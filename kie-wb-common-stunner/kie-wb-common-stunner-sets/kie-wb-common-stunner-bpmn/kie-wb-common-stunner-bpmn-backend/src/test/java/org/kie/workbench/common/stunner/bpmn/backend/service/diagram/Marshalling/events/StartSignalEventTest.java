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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Marshalling.events;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.InterruptingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StartSignalEventTest extends StartEvent {

    private static final String BPMN_START_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/signalStartEvents.bpmn";

    private static final String FILLED_TOP_LEVEL_EVENT_ID = "40270ECD-79FB-4211-BBF5-B1B6DF39CC24";
    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "6DF9C3AB-7A3B-44D0-AAE1-C0E09D60ACC6";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "EEC78B63-5E63-4301-9B7F-30A26634091C";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "E49AC940-F618-4F25-AE18-74AFDC923A7C";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 11;

    public StartSignalEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String eventName = "Signal Start Event with Name";
        final String eventDocumentation = "Non empty\nDocumentation\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String signalRef = "Signal1";
        final String eventDataOutput = "||event:String||[dout]event->processVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartSignalEvent filledTop = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_ID, StartSignalEvent.class);
        assertGeneralSet(filledTop.getGeneral(), eventName, eventDocumentation);
        assertSignalEventExecutionSet(filledTop.getExecutionSet(), signalRef, INTERRUPTING);
        assertDataIOSet(filledTop.getDataIOSet(), eventDataOutput);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartSignalEvent emptyTop = getStartNodeById(diagram, EMPTY_TOP_LEVEL_EVENT_ID, StartSignalEvent.class);
        assertGeneralSet(emptyTop.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptyTop.getExecutionSet(), EMPTY_VALUE, NON_INTERRUPTING);
        assertDataIOSet(emptyTop.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String eventName = "Signal inside of Event sub-process";
        final String eventDocumentation = "Non empty Signal Event\nDocumentation\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String signalRef = "AnotherSignal";
        final String eventDataOutput = "||hello:String||[dout]hello->processVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartSignalEvent filledSubprocess = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_ID, StartSignalEvent.class);
        assertGeneralSet(filledSubprocess.getGeneral(), eventName, eventDocumentation);
        assertSignalEventExecutionSet(filledSubprocess.getExecutionSet(), signalRef, INTERRUPTING);
        assertDataIOSet(filledSubprocess.getDataIOSet(), eventDataOutput);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartSignalEvent emptySubprocess = getStartNodeById(diagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID, StartSignalEvent.class);
        assertGeneralSet(emptySubprocess.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptySubprocess.getExecutionSet(), EMPTY_VALUE, NON_INTERRUPTING);
        assertDataIOSet(emptySubprocess.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(StartSignalEvent.class, FILLED_TOP_LEVEL_EVENT_ID);
    }

    @Test
    @Override
    public void testMarshallTopLevelEmptyEventProperties() throws Exception {
        checkEventMarshalling(StartSignalEvent.class, EMPTY_TOP_LEVEL_EVENT_ID);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(StartSignalEvent.class, FILLED_SUBPROCESS_LEVEL_EVENT_ID);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventEmptyProperties() throws Exception {
        checkEventMarshalling(StartSignalEvent.class, EMPTY_SUBPROCESS_LEVEL_EVENT_ID);
    }

    @Override
    String getBpmnStartEventFilePath() {
        return BPMN_START_EVENT_FILE_PATH;
    }

    private void assertSignalEventExecutionSet(InterruptingSignalEventExecutionSet executionSet, String eventName, boolean isInterrupting) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getSignalRef());
        assertEquals(eventName, executionSet.getSignalRef().getValue());
        assertEquals(isInterrupting, executionSet.getIsInterrupting().getValue());
    }
}
