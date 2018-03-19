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
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.InterruptingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class StartTimerEventTest extends StartEvent {

    private static final String BPMN_START_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/timerStartEvents.bpmn";

    private static final String FILLED_TOP_LEVEL_EVENT_MULTIPLE_ID = "8B1A8442-B1F7-44C7-A33C-180B9477E672";
    private static final String FILLED_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID = "CA065A81-8586-429D-BCB9-6865153B006F";
    private static final String FILLED_TOP_LEVEL_EVENT_AFTER_DURATION_ID = "14D9E958-5461-4634-9C94-B51AD2DFEA72";
    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "D1207863-4C91-4AA6-8F0E-6BF8E598E8B4";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID = "3A82AF7C-E827-4E00-9D67-E91572D8D978";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID = "E1464EB2-DDD3-415F-8AE2-EA98FE294C24";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID = "20DDDE81-D3E0-4534-A6A5-F25B36466078";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "3958A858-7E9F-4A1C-862F-711DE3033A14";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 21;

    public StartTimerEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String eventNameMultiple = "~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String eventDocumentationMultiple = "~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String timerValueMultiple = "5m3s";
        final String timerValueLanguageMultiple = "cron";
        // Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //final String timerDataOutputMultiple = "||hello:String||[dout]hello->processVar";

        final String eventNameSpecificDate = "~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String eventDocumentationSpecificDate = "Why not\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\nhere\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String timerValueSpecificDate = "2018-03-16T13:50:59+02:00";
        // Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //final String timerDataOutputMultiple = "||hello:String||[dout]hello->processVar";

        final String eventNameAfterDuration = "'Some name'";
        final String eventDocumentationAfterDuration = "And some documentation\n\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String timerValueAfterDuration = "PT1H17M";
        // Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //final String timerDataOutputMultiple = "||hello:String||[dout]hello->processVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartTimerEvent filledTopMultiple = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_MULTIPLE_ID, StartTimerEvent.class);
        assertGeneralSet(filledTopMultiple.getGeneral(), eventNameMultiple, eventDocumentationMultiple);
        assertTimerEventMultiple(filledTopMultiple.getExecutionSet(), timerValueMultiple, timerValueLanguageMultiple, INTERRUPTING);
        // Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //final String timerDataOutputMultiple = "||hello:String||[dout]hello->processVar";

        StartTimerEvent filledTopSpecificDate = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID, StartTimerEvent.class);
        assertGeneralSet(filledTopSpecificDate.getGeneral(), eventNameSpecificDate, eventDocumentationSpecificDate);
        assertTimerEventSpecificDate(filledTopSpecificDate.getExecutionSet(), timerValueSpecificDate, INTERRUPTING);
        // Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //final String timerDataOutputSpecificDate = "||hello:String||[dout]hello->processVar";

        StartTimerEvent filledTopAfterDuration = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_AFTER_DURATION_ID, StartTimerEvent.class);
        assertGeneralSet(filledTopAfterDuration.getGeneral(), eventNameAfterDuration, eventDocumentationAfterDuration);
        assertTimerEventAfterDuration(filledTopAfterDuration.getExecutionSet(), timerValueAfterDuration, INTERRUPTING);
        // Know issue. Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //assertDataIOSet(filledTopSpecificDate.getDataIOSet(), timerDataOutputDuration);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartTimerEvent emptyTop = getStartNodeById(diagram, EMPTY_TOP_LEVEL_EVENT_ID, StartTimerEvent.class);
        assertGeneralSet(emptyTop.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertTimerEventEmpty(emptyTop.getExecutionSet(), NON_INTERRUPTING);
        // Know issue. Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //assertDataIOSet(emptySubprocess.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String eventNameMultiple = "~`!@#$%^&*()_+=-{}|\\][:\";'?><,./ name";
        final String eventDocumentationMultiple = "Some documentation for this event\n\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String timerValueMultiple = "R3/PT8M3S";
        //  "none" is a "not a cron" for engine and looks like ISO in GUI
        final String timerValueLanguageMultiple = "none";
        // Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //final String timerDataOutputMultiple = "||hello:String||[dout]hello->processVar";

        final String eventNameSpecificDate = "~`!@#$%^&*()_+=-{}|\\][:\";'?><,./ hello how are you?";
        final String eventDocumentationSpecificDate = "~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\ndocumentaion";
        final String timerValueSpecificDate = "2018-03-16T13:50:59+01:00";
        // Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //final String timerDataOutputSpecificDate = "||hello:String||[dout]hello->processVar";

        final String eventNameAfterDuration = "\"non empty name\"";
        final String eventDocumentationAfterDuration = "Time is here: ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String timerValueAfterDuration = "PT1H15M";
        // Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //final String timerDataOutputDuration = "||hello:String||[dout]hello->processVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartTimerEvent filledTopMultiple = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID, StartTimerEvent.class);
        assertGeneralSet(filledTopMultiple.getGeneral(), eventNameMultiple, eventDocumentationMultiple);
        assertTimerEventMultiple(filledTopMultiple.getExecutionSet(), timerValueMultiple, timerValueLanguageMultiple, INTERRUPTING);
        // Know issue. Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //assertDataIOSet(filledTopMultiple.getDataIOSet(), timerDataOutputMultiple);

        StartTimerEvent filledTopSpecificDate = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID, StartTimerEvent.class);
        assertGeneralSet(filledTopSpecificDate.getGeneral(), eventNameSpecificDate, eventDocumentationSpecificDate);
        assertTimerEventSpecificDate(filledTopSpecificDate.getExecutionSet(), timerValueSpecificDate, INTERRUPTING);
        // Know issue. Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //assertDataIOSet(filledTopSpecificDate.getDataIOSet(), timerDataOutputSpecificDate);

        StartTimerEvent filledTopAfterDuration = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID, StartTimerEvent.class);
        assertGeneralSet(filledTopAfterDuration.getGeneral(), eventNameAfterDuration, eventDocumentationAfterDuration);
        assertTimerEventAfterDuration(filledTopAfterDuration.getExecutionSet(), timerValueAfterDuration, INTERRUPTING);
        // Know issue. Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //assertDataIOSet(filledTopSpecificDate.getDataIOSet(), timerDataOutputDuration);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartTimerEvent emptySubprocess = getStartNodeById(diagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID, StartTimerEvent.class);
        assertGeneralSet(emptySubprocess.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertTimerEventEmpty(emptySubprocess.getExecutionSet(), NON_INTERRUPTING);
        // Know issue. Should be uncommented after https://issues.jboss.org/browse/JBPM-7038 will be fixed
        //assertDataIOSet(emptySubprocess.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(StartTimerEvent.class, FILLED_TOP_LEVEL_EVENT_MULTIPLE_ID);
        checkEventMarshalling(StartTimerEvent.class, FILLED_TOP_LEVEL_EVENT_AFTER_DURATION_ID);
        checkEventMarshalling(StartTimerEvent.class, FILLED_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID);
    }

    @Test
    @Override
    public void testMarshallTopLevelEmptyEventProperties() throws Exception {
        checkEventMarshalling(StartTimerEvent.class, EMPTY_TOP_LEVEL_EVENT_ID);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(StartTimerEvent.class, FILLED_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID);
        checkEventMarshalling(StartTimerEvent.class, FILLED_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID);
        checkEventMarshalling(StartTimerEvent.class, FILLED_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventEmptyProperties() throws Exception {
        checkEventMarshalling(StartTimerEvent.class, EMPTY_SUBPROCESS_LEVEL_EVENT_ID);
    }

    @Override
    String getBpmnStartEventFilePath() {
        return BPMN_START_EVENT_FILE_PATH;
    }

    private void assertTimerEventMultiple(InterruptingTimerEventExecutionSet executionSet, String timerValue, String timeCycleLanguage, boolean isInterrupting) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getTimerSettings());
        assertEquals(timerValue, executionSet.getTimerSettings().getValue().getTimeCycle());
        assertEquals(timeCycleLanguage, executionSet.getTimerSettings().getValue().getTimeCycleLanguage());
        assertEquals(isInterrupting, executionSet.getIsInterrupting().getValue());

        assertNull(executionSet.getTimerSettings().getValue().getTimeDuration());
        assertNull(executionSet.getTimerSettings().getValue().getTimeDate());
    }

    private void assertTimerEventAfterDuration(InterruptingTimerEventExecutionSet executionSet, String timerValue, boolean isInterrupting) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getTimerSettings());
        assertEquals(timerValue, executionSet.getTimerSettings().getValue().getTimeDuration());
        assertEquals(isInterrupting, executionSet.getIsInterrupting().getValue());

        assertNull(executionSet.getTimerSettings().getValue().getTimeDate());
        assertNull(executionSet.getTimerSettings().getValue().getTimeCycle());
        assertNull(executionSet.getTimerSettings().getValue().getTimeCycleLanguage());
    }

    private void assertTimerEventSpecificDate(InterruptingTimerEventExecutionSet executionSet, String dateValue, boolean isInterrupting) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getTimerSettings());
        assertEquals(dateValue, executionSet.getTimerSettings().getValue().getTimeDate());
        assertEquals(isInterrupting, executionSet.getIsInterrupting().getValue());

        assertNull(executionSet.getTimerSettings().getValue().getTimeCycle());
        assertNull(executionSet.getTimerSettings().getValue().getTimeDuration());
        assertNull(executionSet.getTimerSettings().getValue().getTimeCycleLanguage());
    }

    private void assertTimerEventEmpty(InterruptingTimerEventExecutionSet executionSet, boolean isInterrupting) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getTimerSettings());
        assertEquals(isInterrupting, executionSet.getIsInterrupting().getValue());

        assertNull(executionSet.getTimerSettings().getValue().getTimeDate());
        assertNull(executionSet.getTimerSettings().getValue().getTimeCycle());
        assertNull(executionSet.getTimerSettings().getValue().getTimeDuration());
        assertNull(executionSet.getTimerSettings().getValue().getTimeCycleLanguage());
    }
}
