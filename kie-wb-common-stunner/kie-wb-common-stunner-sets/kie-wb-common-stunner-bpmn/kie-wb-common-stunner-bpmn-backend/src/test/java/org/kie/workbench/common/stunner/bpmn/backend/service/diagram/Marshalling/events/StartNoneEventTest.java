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
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

public class StartNoneEventTest extends StartEvent {

    private static final String BPMN_START_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startNoneEvents.bpmn";

    private static final String FILLED_TOP_LEVEL_EVENT_ID = "D248D0BE-78CA-4980-99C8-93F9B8A0BC4C";
    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "1BA1C8C8-16DA-49A5-9C04-2F165B5A4273";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "6B710126-9C80-4409-9A49-4EECD2F88A14";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "6A19FF0A-296B-4BC5-B951-C380B8E7AB56";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 11;

    public StartNoneEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String eventName = "Hello none start event name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String eventDocumentation = "~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\nDocumentation";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartNoneEvent filledTop = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_ID, StartNoneEvent.class);
        assertGeneralSet(filledTop.getGeneral(), eventName, eventDocumentation);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartNoneEvent emptyTop = getStartNodeById(diagram, EMPTY_TOP_LEVEL_EVENT_ID, StartNoneEvent.class);
        assertGeneralSet(emptyTop.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String eventName = "It is also not empty ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String eventDocumentation = "Some documentation as well\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\n";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartNoneEvent filledSubprocess = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_ID, StartNoneEvent.class);
        assertGeneralSet(filledSubprocess.getGeneral(), eventName, eventDocumentation);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartNoneEvent emptySubprocess = getStartNodeById(diagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID, StartNoneEvent.class);
        assertGeneralSet(emptySubprocess.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventFilledProperties() throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, FILLED_TOP_LEVEL_EVENT_ID, StartNoneEvent.class);
    }

    @Test
    @Override
    public void testMarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, EMPTY_TOP_LEVEL_EVENT_ID, StartNoneEvent.class);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventFilledProperties() throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, FILLED_SUBPROCESS_LEVEL_EVENT_ID, StartNoneEvent.class);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID, StartNoneEvent.class);
    }
}
