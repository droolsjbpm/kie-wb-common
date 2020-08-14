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

package org.kie.workbench.common.dmn.client.canvas.controls.selection;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import elemental2.dom.DomGlobal;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoMultipleSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditDRDToolboxAction.DRDACTIONS_CONTEXT_MENU_ACTIONS_ADD_TO;
import static org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditDRDToolboxAction.DRDACTIONS_CONTEXT_MENU_ACTIONS_CREATE;
import static org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditDRDToolboxAction.DRDACTIONS_CONTEXT_MENU_ACTIONS_REMOVE;
import static org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditDRDToolboxAction.DRDACTIONS_CONTEXT_MENU_TITLE;
import static org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditDRDToolboxAction.HEADER_MENU_ICON_CLASS;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeXOfEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeYOfEvent;

/**
 * Specializes {@link LienzoMultipleSelectionControl} to also support selection of a single {@link DomainObject}.
 * Selection of an {@link Element}, clearance of the canvas or destruction of the underlying session also deselects
 * any {@link DomainObject}.
 * @param <H> {@link AbstractCanvasHandler}
 */
@Dependent
@MultipleSelection
@DMNEditor
public class DomainObjectAwareLienzoMultipleSelectionControl<H extends AbstractCanvasHandler> extends LienzoMultipleSelectionControl<H> {

    private Optional<DomainObject> selectedDomainObject = Optional.empty();
    private final ContextMenu drdContextMenu;
    private final ClientTranslationService translationService;

    @Inject
    public DomainObjectAwareLienzoMultipleSelectionControl(final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                                           final Event<CanvasClearSelectionEvent> clearSelectionEvent,
                                                           final ContextMenu drdContextMenu,
                                                           final ClientTranslationService translationService) {
        super(canvasSelectionEvent,
              clearSelectionEvent);
        this.drdContextMenu = drdContextMenu;
        this.translationService = translationService;
    }

    @Override
    protected void onEnable(H canvasHandler) {
        super.onEnable(canvasHandler);

        canvasHandler
                .getAbstractCanvas()
                .getView()
                .asWidget()
                .addDomHandler(event -> {
                    event.preventDefault();
                    event.stopPropagation();

                    final boolean selectionIsMultiple = getSelectedItems().size() > 1;
                    final boolean aSelectedShapeHasBeenClicked = isClickedOnShape(canvasHandler, getRelativeXOfEvent(event), getRelativeYOfEvent(event));

                    if (selectionIsMultiple && aSelectedShapeHasBeenClicked) {
                        drdContextMenu.appendContextMenuToTheDOM(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
                        drdContextMenu.show(self -> contextMenuHandler(self, getSelectedNodes(canvasHandler)));
                    }

        }, ContextMenuEvent.getType());
    }

    private boolean isClickedOnShape(final H canvasHandler, final int canvasX, final int canvasY) {
        return getSelectedNodesStream(canvasHandler)
                .map(Element::getContent)
                .filter(content -> content instanceof View)
                .anyMatch(view -> {
                    final Bounds bounds = ((View) view).getBounds();
                    return canvasX >= bounds.getUpperLeft().getX() && canvasX <= bounds.getLowerRight().getX()
                            && canvasY >= bounds.getUpperLeft().getY() && canvasY <= bounds.getLowerRight().getY();
                });
    }

    private List<Node<? extends Definition<?>, Edge>> getSelectedNodes(final H canvasHandler) {
        return getSelectedNodesStream(canvasHandler)
                .map(Element::asNode)
                .collect(Collectors.toList());
    }

    private Stream<? extends Element<? extends Definition<?>>> getSelectedNodesStream(final H canvasHandler) {
        return getSelectedItems().stream()
                .map(uuid -> CanvasLayoutUtils.getElement(canvasHandler, uuid))
                .filter(element -> element instanceof Node);
    }

    void contextMenuHandler(final ContextMenu contextMenu, final List<Node<? extends Definition<?>, Edge>> selectedNodes) {
        contextMenu.hide();
        contextMenu.setHeaderMenu(translationService.getValue(DRDACTIONS_CONTEXT_MENU_TITLE), HEADER_MENU_ICON_CLASS);
        contextMenu.addTextMenuItem(translationService.getValue(DRDACTIONS_CONTEXT_MENU_ACTIONS_CREATE),
                                    true,
                                    () -> DomGlobal.console.log("A", selectedNodes));
        contextMenu.addTextMenuItem(translationService.getValue(DRDACTIONS_CONTEXT_MENU_ACTIONS_ADD_TO),
                                    true,
                                    () -> DomGlobal.console.log("B", selectedNodes));
        contextMenu.addTextMenuItem(translationService.getValue(DRDACTIONS_CONTEXT_MENU_ACTIONS_REMOVE),
                                    true,
                                    () -> DomGlobal.console.log("C", selectedNodes));
    }

    @Override
    public Optional<Object> getSelectedItemDefinition() {
        if (selectedDomainObject.isPresent()) {
            return Optional.of(selectedDomainObject.get());
        } else {
            return super.getSelectedItemDefinition();
        }
    }

    @Override
    protected void onSelect(final Collection<String> uuids) {
        selectedDomainObject = Optional.empty();
        super.onSelect(uuids);
    }

    @Override
    public SelectionControl<H, Element> select(final String uuid) {
        selectedDomainObject = Optional.empty();
        return super.select(uuid);
    }

    @Override
    public void clear() {
        selectedDomainObject = Optional.empty();
        super.clear();
    }

    @Override
    protected void onClearSelection() {
        selectedDomainObject = Optional.empty();
        super.onClearSelection();
    }

    @Override
    public void destroy() {
        selectedDomainObject = Optional.empty();
        super.destroy();
    }

    @Override
    protected void onDestroy() {
        selectedDomainObject = Optional.empty();
        super.onDestroy();
    }

    @Override
    protected void handleCanvasElementSelectedEvent(final CanvasSelectionEvent event) {
        selectedDomainObject = Optional.empty();
        super.handleCanvasElementSelectedEvent(event);
    }

    @Override
    protected void handleCanvasClearSelectionEvent(final CanvasClearSelectionEvent event) {
        selectedDomainObject = Optional.empty();
        super.handleCanvasClearSelectionEvent(event);
        super.onClearSelection();
    }

    void handleDomainObjectSelectedEvent(final @Observes DomainObjectSelectionEvent event) {
        checkNotNull("event", event);
        if (Objects.equals(getCanvasHandler(), event.getCanvasHandler())) {
            selectedDomainObject = Optional.ofNullable(event.getDomainObject());
        }
    }

    private AbstractCanvasHandler getCanvasHandler() {
        return getSelectionControl().getCanvasHandler();
    }
}
