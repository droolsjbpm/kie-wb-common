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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events.intermediate;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.CancellingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BoundaryCatchingIntermediateEscalationEventTest extends BoundaryCatchingIntermediateEvent<IntermediateEscalationEvent> {

    private static final String BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundaryEscalationEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_C4710D6A-661D-4E8A-9F1E-364F592FBDA4";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_688A335E-4CFB-4B02-94EC-E3019727AE4E";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_26F7F387-C073-406E-B75B-3F0ED6F82067";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_71DF27D5-F356-4C1E-BE29-D2F16CC6D88F";

    private static final String EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID = "_816018AD-B9D9-4F19-B713-547E8A822AC5";
    private static final String FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID = "_FBAD5555-D73B-4AD8-9C41-34B6B8593DAE";
    private static final String EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID = "_B343D66C-61C6-4FB6-BFF7-8B914DB3FED7";
    private static final String FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID = "_BCD0A0C2-F936-48BC-B612-F99BE773E8EF";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 25;

    public BoundaryCatchingIntermediateEscalationEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Escalation event01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation01";
        final String EVENT_DATA_OUTPUT = "||output:String||[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent filledTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                     FILLED_TOP_LEVEL_EVENT_ID,
                                                                                     HAS_NO_INCOME_EDGE,
                                                                                     HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledTopEvent.getExecutionSet(), EVENT_REF, CANCELLING);
        assertDataIOSet(filledTopEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent emptyTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                    EMPTY_TOP_LEVEL_EVENT_ID,
                                                                                    HAS_NO_INCOME_EDGE,
                                                                                    HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptyTopEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING);
        assertDataIOSet(emptyTopEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Escalation event03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation03";
        final String EVENT_DATA_OUTPUT = "||output:String||[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                            HAS_NO_INCOME_EDGE,
                                                                                            HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                           EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                           HAS_NO_INCOME_EDGE,
                                                                                           HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "Escalation event02 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation02";
        final String EVENT_DATA_OUTPUT = "||output:String||[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID,
                                                                                            HAS_NO_INCOME_EDGE,
                                                                                            HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent emptyEvent = getCatchingIntermediateNodeById(diagram,
                                                                                 EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID,
                                                                                 HAS_NO_INCOME_EDGE,
                                                                                 HAS_OUTGOING_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptyEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING);
        assertDataIOSet(emptyEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                           EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                           HAS_NO_INCOME_EDGE,
                                                                                           HAS_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "Escalation event04 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation04";
        final String EVENT_DATA_OUTPUT = "||output:String||[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                            HAS_NO_INCOME_EDGE,
                                                                                            HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Override
    String getBpmnCatchingIntermediateEventFilePath() {
        return BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateEscalationEvent> getCatchingIntermediateEventType() {
        return IntermediateEscalationEvent.class;
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
    String getFilledTopLevelEventWithEdgesId() {
        return FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptyTopLevelEventWithEdgesId() {
        return EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getFilledSubprocessLevelEventWithEdgesId() {
        return FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptySubprocessLevelEventWithEdgesId() {
        return EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID;
    }

    private void assertEscalationEventExecutionSet(CancellingEscalationEventExecutionSet executionSet, String escalationRef, boolean isCancelling) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getEscalationRef());
        assertNotNull(executionSet.getCancelActivity());
        assertEquals(escalationRef, executionSet.getEscalationRef().getValue());
        assertEquals(isCancelling, executionSet.getCancelActivity().getValue());
    }
}
