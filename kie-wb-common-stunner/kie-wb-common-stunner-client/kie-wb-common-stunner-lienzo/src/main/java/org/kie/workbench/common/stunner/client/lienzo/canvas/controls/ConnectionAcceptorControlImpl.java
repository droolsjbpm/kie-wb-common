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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

@Dependent
public class ConnectionAcceptorControlImpl extends AbstractAcceptorControl
        implements ConnectionAcceptorControl<AbstractCanvasHandler> {

    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private CanvasHighlight canvasHighlight;

    @Inject
    public ConnectionAcceptorControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    protected void onEnable(final WiresCanvas.View canvasView) {
        this.canvasHighlight = new CanvasHighlight(getCanvasHandler());
        canvasView.setConnectionAcceptor(CONNECTION_ACCEPTOR);
    }

    @Override
    protected void onDisable(final WiresCanvas.View canvasView) {
        canvasView.setConnectionAcceptor(IConnectionAcceptor.NONE);
        this.canvasHighlight.destroy();
        this.canvasHighlight = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean allowSource(final Node source,
                               final Edge<View<?>, Node> connector,
                               final Connection connection) {
        if (isSourceChanged(source,
                            connector,
                            connection)) {
            final CommandResult<CanvasViolation> violations =
                    getCommandManager().allow(getCanvasHandler(),
                                              canvasCommandFactory.setSourceNode(source,
                                                                                 connector,
                                                                                 connection));
            final boolean accepts = isAccept(violations);
            highlight(source,
                      connector,
                      accepts);
            return accepts;
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean allowTarget(final Node target,
                               final Edge<View<?>, Node> connector,
                               final Connection connection) {
        if (isTargetChanged(target,
                            connector,
                            connection)) {
            final CommandResult<CanvasViolation> violations =
                    getCommandManager().allow(getCanvasHandler(),
                                              canvasCommandFactory.setTargetNode(target,
                                                                                 connector,
                                                                                 connection));
            final boolean accepts = isAccept(violations);
            highlight(target,
                      connector,
                      accepts);
            return accepts;
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean acceptSource(final Node source,
                                final Edge<View<?>, Node> connector,
                                final Connection connection) {
        ensureUnHighLight();
        if (isSourceChanged(source,
                            connector,
                            connection)) {
            final CommandResult<CanvasViolation> violations =
                    getCommandManager().execute(getCanvasHandler(),
                                                canvasCommandFactory.setSourceNode(source,
                                                                                   connector,
                                                                                   connection));
            return isAccept(violations);
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean acceptTarget(final Node target,
                                final Edge<View<?>, Node> connector,
                                final Connection connection) {
        ensureUnHighLight();
        if (isTargetChanged(target,
                            connector,
                            connection)) {
            final CommandResult<CanvasViolation> violations =
                    getCommandManager().execute(getCanvasHandler(),
                                                canvasCommandFactory.setTargetNode(target,
                                                                                   connector,
                                                                                   connection));
            return isAccept(violations);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static boolean isSourceChanged(final Node node,
                                           final Edge<View<?>, Node> connector,
                                           final Connection connection) {
        final ViewConnector vc = null != connector.getContent() ?
                (ViewConnector) connector.getContent() :
                null;
        return (!eq(node,
                    connector.getSourceNode(),
                    connection,
                    null != vc ? vc.getSourceConnection() : Optional.empty()));
    }

    @SuppressWarnings("unchecked")
    private static boolean isTargetChanged(final Node node,
                                           final Edge<View<?>, Node> connector,
                                           final Connection connection) {
        final ViewConnector vc = null != connector.getContent() ?
                (ViewConnector) connector.getContent() :
                null;
        return (!eq(node,
                    connector.getTargetNode(),
                    connection,
                    null != vc ? vc.getTargetConnection() : Optional.empty()));
    }

    private static boolean eq(final Element<?> e1,
                              final Element<?> e2,
                              final Connection c1,
                              final Optional<Connection> c2) {
        return eq(e1,
                  e2) && eq(c1,
                            c2.orElse(null));
    }

    @SuppressWarnings("unchecked")
    private static boolean eq(final Object n1,
                              final Object n2) {
        if (n1 == null && n2 == null) {
            return true;
        }
        return null != n1 && n1.equals(n2);
    }

    private final IConnectionAcceptor CONNECTION_ACCEPTOR = new IConnectionAcceptor() {

        // Set the source Node for the connector.
        @Override
        @SuppressWarnings("unchecked")
        public boolean acceptHead(final WiresConnection head,
                                  final WiresMagnet wiresMagnet) {
            if (!isEnabled()) {
                return false;
            }
            final Edge edge = WiresUtils.getEdge(getCanvasHandler(),
                                                 head.getConnector());
            final Node sourceNode = WiresUtils.getNode(getCanvasHandler(),
                                                       wiresMagnet);
            final Connection connection = createConnection(head,
                                                           wiresMagnet);
            return acceptSource(sourceNode,
                                edge,
                                connection);
        }

        // Set the target Node for the connector.
        @Override
        @SuppressWarnings("unchecked")
        public boolean acceptTail(final WiresConnection tail,
                                  final WiresMagnet wiresMagnet) {
            if (!isEnabled()) {
                return false;
            }
            final WiresConnection head = tail.getConnector().getHeadConnection();
            final Edge edge = WiresUtils.getEdge(getCanvasHandler(),
                                                 head.getConnector());
            final Node targetNode = WiresUtils.getNode(getCanvasHandler(),
                                                       wiresMagnet);
            final Connection connection = createConnection(tail,
                                                           wiresMagnet);
            return acceptTarget(targetNode,
                                edge,
                                connection);
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean headConnectionAllowed(final WiresConnection head,
                                             final WiresShape shape) {

            if (!isEnabled()) {
                return false;
            }
            final Edge<View<?>, Node> edge = WiresUtils.getEdge(getCanvasHandler(),
                                                                head.getConnector());
            final Node sourceNode = WiresUtils.getNode(getCanvasHandler(),
                                                       shape);
            return allowSource(sourceNode,
                               edge,
                               createConnection(sourceNode));
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean tailConnectionAllowed(final WiresConnection tail,
                                             final WiresShape shape) {
            if (!isEnabled()) {
                return false;
            }
            final Edge<View<?>, Node> edge = WiresUtils.getEdge(getCanvasHandler(),
                                                                tail.getConnector());
            final Node targetNode = WiresUtils.getNode(getCanvasHandler(),
                                                       shape);
            return allowTarget(targetNode,
                               edge,
                               createConnection(targetNode));
        }
    };

    private boolean isAccept(final CommandResult<CanvasViolation> result) {
        return !CommandUtils.isError(result);
    }

    private void highlight(final Node node,
                           final Edge<View<?>, Node> connector,
                           final boolean valid) {
        canvasHighlight.unhighLight();
        if (null != node && valid) {
            canvasHighlight.highLight(node);
        } else if (null != node) {
            canvasHighlight.invalid(node);
        }
    }

    private void ensureUnHighLight() {
        if (null != canvasHighlight) {
            canvasHighlight.unhighLight();
        }
    }

    public static MagnetConnection createConnection(final WiresConnection wiresConnection,
                                                    final WiresMagnet wiresMagnet) {
        return null != wiresMagnet ? new MagnetConnection.Builder()
                .atX(wiresMagnet.getX())
                .atY(wiresMagnet.getY())
                .auto(null != wiresConnection && wiresConnection.isAutoConnection())
                .magnet(wiresMagnet.getIndex())
                .build() :
                null;
    }

    @SuppressWarnings("unchecked")
    public static MagnetConnection createConnection(final Element element) {
        return null != element ?
                MagnetConnection.Builder.forElement(element) :
                null;
    }
}
