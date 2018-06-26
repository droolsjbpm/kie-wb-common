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
import java.util.function.Function;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

public abstract class AbstractCanvasShortcutsControlImpl extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements CanvasShortcutsControl<AbstractCanvasHandler, EditorSession> {

    protected EditorSession editorSession;

    protected final CommonDomainLookups commonDomainLookups;

    protected final DefinitionsCacheRegistry definitionsCacheRegistry;

    protected final GeneralCreateNodeAction createNodeAction;

    public AbstractCanvasShortcutsControlImpl(final CommonDomainLookups commonDomainLookups,
                                              final DefinitionsCacheRegistry definitionsCacheRegistry,
                                              final GeneralCreateNodeAction createNodeAction) {
        this.commonDomainLookups = commonDomainLookups;
        this.definitionsCacheRegistry = definitionsCacheRegistry;
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
    public void appendNode(final String sourceNodeId, final Function<String, Boolean> canBeNodeAppended) {

        final Node sourceNode = CanvasLayoutUtils.getElement(canvasHandler, sourceNodeId).asNode();

        commonDomainLookups.setDomain(canvasHandler.getDiagram().getMetadata().getDefinitionSetId());
        final Set<String> connectorDefinitionIds = commonDomainLookups.lookupTargetConnectors(sourceNode);

        for (final String connectorDefinitionId : connectorDefinitionIds) {
            final Set<String> targetNodesDefinitionIds =
                    commonDomainLookups.lookupTargetNodes(canvasHandler.getDiagram().getGraph(),
                                                          sourceNode,
                                                          connectorDefinitionId);

            for (final String targetNodeDefinitionId : targetNodesDefinitionIds) {
                if (canBeNodeAppended.apply(targetNodeDefinitionId)) {
                    createNodeAction.executeAction(canvasHandler,
                                                   sourceNodeId,
                                                   targetNodeDefinitionId,
                                                   connectorDefinitionId);

                    break;
                }
            }
        }
    }

    protected String selectedNodeId() {
        if (editorSession != null && editorSession.getSelectionControl().getSelectedItems().size() == 1) {
            return editorSession.getSelectionControl().getSelectedItems().iterator().next();
        } else {
            return null;
        }
    }

    protected Element selectedNodeElement() {
        return canvasHandler.getGraphIndex().get(selectedNodeId());
    }
}
