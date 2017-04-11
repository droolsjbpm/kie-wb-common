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

package org.kie.workbench.common.stunner.core;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;

/**
 * An utility class for testing scope that provides some "real" (not mocked) graph's initial
 * structure, with different nodes and connectors.
 */
public class TestingGraphInstanceBuilder {

    public static final String DEF0_ID = "def0";
    public static final Set<String> DEF0_LABELS = Collections.singleton("label0");
    public static final String DEF1_ID = "def1";
    public static final Set<String> DEF1_LABELS = Collections.singleton("label1");
    public static final String DEF2_ID = "def2";
    public static final Set<String> DEF2_LABELS = Collections.singleton("label2");
    public static final String DEF3_ID = "def3";
    public static final Set<String> DEF3_LABELS = Collections.singleton("label3");
    public static final String EDGE1_ID = "edge11d";
    public static final String EDGE2_ID = "edge21d";
    public static final String PARENT_NODE_UUID = "parent1";
    public static final String START_NODE_UUID = "node1";
    public static final String INTERM_NODE_UUID = "node2";
    public static final String END_NODE_UUID = "node3";
    public static final String EDGE1_UUID = "edge1";
    public static final String EDGE2_UUID = "edge2";

    /**
     * **********
     * * Graph1 *
     * **********
     * <p>
     * Structure:
     * startNode --(edge1)--> intermNode --(edge2)--> endNode
     */
    public static class TestGraph1 {

        public Object startNodeBean;
        public Node startNode;
        public Object intermNodeBean;
        public Node intermNode;
        public Object endNodeBean;
        public Node endNode;
        public Object edge1Bean;
        public Edge edge1;
        public Object edge2Bean;
        public Edge edge2;
        public int evaluationsCount;
    }

    public static TestGraph1 newGraph1(final TestingGraphMockHandler handler) {
        return buildTestGraph1(handler);
    }

    /**
     * **********
     * * Graph2 *
     * **********
     * <p>
     * Structure:
     * parentNode
     * --------------------------------------------
     * |                     |                     |
     * startNode --(edge1)--> intermNode --(edge2)--> endNode
     */
    public static class TestGraph2 {

        public Node parentNode;
        public Node startNode;
        public Node intermNode;
        public Node endNode;
        public Edge edge1;
        public Edge edge2;
        public int evaluationsCount;
    }

    public static TestGraph2 newGraph2(final TestingGraphMockHandler handler) {
        return buildTestGraph2(handler);
    }



    /*
        ************ PRIVATE BUILDER METHODS ******************
     */

    private static TestGraph1 buildTestGraph1(final TestingGraphMockHandler graphTestHandler) {
        TestGraph1 result = new TestGraph1();
        result.startNodeBean = graphTestHandler.newDef(DEF1_ID,
                                                       Optional.of(DEF1_LABELS));
        result.startNode =
                graphTestHandler.newNode(START_NODE_UUID,
                                         Optional.of(result.startNodeBean));
        result.intermNodeBean = graphTestHandler.newDef(DEF2_ID,
                                                        Optional.of(DEF2_LABELS));
        result.intermNode =
                graphTestHandler.newNode(INTERM_NODE_UUID,
                                         Optional.of(result.intermNodeBean));
        result.endNodeBean = graphTestHandler.newDef(DEF3_ID,
                                                     Optional.of(DEF3_LABELS));
        result.endNode =
                graphTestHandler.newNode(END_NODE_UUID,
                                         Optional.of(result.endNodeBean));
        result.edge1Bean = graphTestHandler.newDef(EDGE1_ID,
                                                   Optional.empty());
        result.edge1 =
                graphTestHandler.newEdge(EDGE1_UUID,
                                         Optional.of(result.edge1Bean));
        result.edge2Bean = graphTestHandler.newDef(EDGE2_UUID,
                                                   Optional.empty());
        result.edge2 =
                graphTestHandler.newEdge(EDGE2_UUID,
                                         Optional.of(result.edge2Bean));
        graphTestHandler
                .addEdge(result.edge1,
                         result.startNode)
                .connectTo(result.edge1,
                           result.intermNode)
                .addEdge(result.edge2,
                         result.intermNode)
                .connectTo(result.edge2,
                           result.endNode);
        result.evaluationsCount = 18;
        return result;
    }

    private static TestGraph2 buildTestGraph2(final TestingGraphMockHandler graphTestHandler) {
        TestGraph2 result = new TestGraph2();
        result.parentNode =
                graphTestHandler.newNode(PARENT_NODE_UUID,
                                         DEF0_ID,
                                         Optional.of(DEF0_LABELS));
        result.startNode =
                graphTestHandler.newNode(START_NODE_UUID,
                                         DEF1_ID,
                                         Optional.of(DEF1_LABELS));
        result.intermNode =
                graphTestHandler.newNode(INTERM_NODE_UUID,
                                         DEF2_ID,
                                         Optional.of(DEF2_LABELS));
        result.endNode =
                graphTestHandler.newNode(END_NODE_UUID,
                                         DEF3_ID,
                                         Optional.of(DEF3_LABELS));
        result.edge1 =
                graphTestHandler.newEdge(EDGE1_UUID,
                                         EDGE1_ID,
                                         Optional.empty());
        result.edge2 =
                graphTestHandler.newEdge(EDGE2_UUID,
                                         EDGE2_ID,
                                         Optional.empty());
        graphTestHandler
                .setChild(result.parentNode,
                          result.startNode)
                .setChild(result.parentNode,
                          result.intermNode)
                .setChild(result.parentNode,
                          result.endNode)
                .addEdge(result.edge1,
                         result.startNode)
                .connectTo(result.edge1,
                           result.intermNode)
                .addEdge(result.edge2,
                         result.intermNode)
                .connectTo(result.edge2,
                           result.endNode);
        result.evaluationsCount = 23;
        return result;
    }
}
