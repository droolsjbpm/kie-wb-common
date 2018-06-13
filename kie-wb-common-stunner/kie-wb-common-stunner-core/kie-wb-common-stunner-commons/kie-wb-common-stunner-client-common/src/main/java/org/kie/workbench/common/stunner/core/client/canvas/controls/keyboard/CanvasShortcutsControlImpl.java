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

package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard;

import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;

@Dependent
public class CanvasShortcutsControlImpl extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements CanvasShortcutsControl<AbstractCanvasHandler, EditorSession> {

    private EditorSession editorSession;

    private final CommonDomainLookups commonDomainLookups;

    private final GeneralCreateNodeAction createNodeAction;

    @Inject
    public CanvasShortcutsControlImpl(final CommonDomainLookups commonDomainLookups,
                                      final GeneralCreateNodeAction createNodeAction) {
        this.commonDomainLookups = commonDomainLookups;
        this.createNodeAction = createNodeAction;
    }

    @Override
    public void register(final Element element) {

    }

    @Override
    public void bind(final EditorSession session) {
        this.editorSession = session;
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
    }

    @Override
    public void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        if (selectedNodeId() != null) {
            if (KeysMatcher.doKeysMatch(keys,
                                        KeyboardEvent.Key.T)) {
                if (selectedNodeIsStart()) {
                    // TODO
                    appendNode(selectedNodeId());
                }
            }

            if (KeysMatcher.doKeysMatch(keys,
                                        KeyboardEvent.Key.G)) {
                if (selectedNodeIsTask()) {
                    // TODO
                    appendNode(selectedNodeId());
                }
            }

            if (KeysMatcher.doKeysMatch(keys,
                                        KeyboardEvent.Key.E)) {
                if (selectedNodeIsStart() || selectedNodeIsTask() || selectedNodeIsGateway()) {
                    // TODO
                    appendNode(selectedNodeId());
                }
            }
        }
    }

    /**
     * Append first possible node
     * @param sourceNodeId
     */
    public void appendNode(final String sourceNodeId) {

        final Node sourceNode = CanvasLayoutUtils.getElement(canvasHandler, sourceNodeId).asNode();

        commonDomainLookups.setDomain(canvasHandler.getDiagram().getMetadata().getDefinitionSetId());
        final Set<String> connectors = commonDomainLookups.lookupTargetConnectors(sourceNode);

        connectors
                .stream()
                .filter(connector -> {
                    final Set<String> targetNodes =
                            commonDomainLookups.lookupTargetNodes(canvasHandler.getDiagram().getGraph(),
                                                                  sourceNode,
                                                                  connector);
                    return targetNodes.size() > 0;
                })
                .findFirst()
                .ifPresent(connector -> {
                    final Set<String> targetNodes =
                            commonDomainLookups.lookupTargetNodes(canvasHandler.getDiagram().getGraph(),
                                                                  sourceNode,
                                                                  connector);
                    createNodeAction.executeAction(canvasHandler,
                                                   sourceNodeId,
                                                   targetNodes.iterator().next(),
                                                   connector);
                });
    }

    private boolean selectedNodeIsStart() {
        if (selectedNodeElement().getContent() instanceof Definition) {
            final Definition definition = (Definition) selectedNodeElement().getContent();
            return (definitionFQNContainsSubstring(definition, "Start"));
        } else {
            return false;
        }
    }

    private boolean selectedNodeIsTask() {
        if (selectedNodeElement().getContent() instanceof Definition) {
            final Definition definition = (Definition) selectedNodeElement().getContent();
            return (definitionFQNContainsSubstring(definition, "Task"));
        } else {
            return false;
        }
    }

    private boolean selectedNodeIsGateway() {
        if (selectedNodeElement().getContent() instanceof Definition) {
            final Definition definition = (Definition) selectedNodeElement().getContent();
            return (definitionFQNContainsSubstring(definition, "Gateway"));
        } else {
            return false;
        }
    }

    protected String selectedNodeId() {
        if (editorSession != null && editorSession.getSelectionControl().getSelectedItems().size() == 1) {
            return editorSession.getSelectionControl().getSelectedItems().iterator().next();
        } else {
            return null;
        }
    }

    private Element selectedNodeElement() {
        return canvasHandler.getGraphIndex().get(selectedNodeId());
    }

    private static boolean definitionFQNContainsSubstring(final Definition definition, final String subString) {
        return definition.getDefinition().getClass().getName().contains(subString);
    }
}
