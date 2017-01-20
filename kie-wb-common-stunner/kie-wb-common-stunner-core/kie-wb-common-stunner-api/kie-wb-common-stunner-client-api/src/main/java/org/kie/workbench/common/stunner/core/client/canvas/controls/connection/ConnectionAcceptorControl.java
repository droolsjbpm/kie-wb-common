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

package org.kie.workbench.common.stunner.core.client.canvas.controls.connection;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public interface ConnectionAcceptorControl<H extends CanvasHandler> extends CanvasControl<H>,
                                                                            RequiresCommandManager<H> {

    boolean allowSource(final Node source,
                        final Edge<View<?>, Node> connector,
                        final int magnet);

    boolean allowTarget(final Node source,
                        final Edge<View<?>, Node> connector,
                        final int magnet);

    boolean acceptSource(final Node source,
                         final Edge<View<?>, Node> connector,
                         final int magnet);

    boolean acceptTarget(final Node source,
                         final Edge<View<?>, Node> connector,
                         final int magnet);
}
