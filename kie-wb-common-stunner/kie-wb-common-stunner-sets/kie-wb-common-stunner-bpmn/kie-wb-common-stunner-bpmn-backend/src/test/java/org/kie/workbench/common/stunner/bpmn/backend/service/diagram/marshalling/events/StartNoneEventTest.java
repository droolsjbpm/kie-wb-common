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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;

public class StartNoneEventTest extends StartEvent<StartNoneEvent> {

    private static final String BPMN_START_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startNoneEvents.bpmn";

    private static final String FILLED_TOP_LEVEL_EVENT_ID = "D248D0BE-78CA-4980-99C8-93F9B8A0BC4C";
    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "1BA1C8C8-16DA-49A5-9C04-2F165B5A4273";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "6B710126-9C80-4409-9A49-4EECD2F88A14";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "6A19FF0A-296B-4BC5-B951-C380B8E7AB56";

    private static final String SLA_DUE_DATE = "12/25/1983";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 11;

    public StartNoneEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Hello none start event name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\nDocumentation";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartNoneEvent filledTop = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_ID, StartNoneEvent.class);
        assertGeneralSet(filledTop.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);

        if (getMarshallerType() == Marshaller.NEW) {
            assertEquals(filledTop.getExecutionSet().getSlaDueDate().getValue(), SLA_DUE_DATE);
        }
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartNoneEvent emptyTop = getStartNodeById(diagram, EMPTY_TOP_LEVEL_EVENT_ID, StartNoneEvent.class);
        assertGeneralSet(emptyTop.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);

        if (getMarshallerType() == Marshaller.NEW) {
            assertEquals(emptyTop.getExecutionSet().getSlaDueDate().getValue(), "");
        }
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "It is also not empty ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "Some documentation as well\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\n";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartNoneEvent filledSubprocess = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_ID, StartNoneEvent.class);
        assertGeneralSet(filledSubprocess.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);

        if (getMarshallerType() == Marshaller.NEW) {
            assertEquals(filledSubprocess.getExecutionSet().getSlaDueDate().getValue(), SLA_DUE_DATE);
        }
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartNoneEvent emptySubprocess = getStartNodeById(diagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID, StartNoneEvent.class);
        assertGeneralSet(emptySubprocess.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);

        if (getMarshallerType() == Marshaller.NEW) {
            assertEquals(emptySubprocess.getExecutionSet().getSlaDueDate().getValue(), "");
        }
    }

    @Override
    String getBpmnStartEventFilePath() {
        return BPMN_START_EVENT_FILE_PATH;
    }

    @Override
    String getFilledTopLevelEventId() {
        return FILLED_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptyTopLevelEventId() {
        return EMPTY_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getFilledSubprocessLevelEventId() {
        return FILLED_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptySubprocessLevelEventId() {
        return EMPTY_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    Class<StartNoneEvent> getStartEventType() {
        return StartNoneEvent.class;
    }
}
