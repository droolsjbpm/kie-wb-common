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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom;

import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.dmn.client.commands.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class TextBoxDOMElement extends org.uberfire.ext.wires.core.grids.client.widget.dom.impl.TextBoxDOMElement {

    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private BaseUIModelMapper<?> uiModelMapper;

    public TextBoxDOMElement(final TextBox widget,
                             final GridLayer gridLayer,
                             final GridWidget gridWidget,
                             final SessionManager sessionManager,
                             final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                             final BaseUIModelMapper<?> uiModelMapper) {
        super(widget,
              gridLayer,
              gridWidget);
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.uiModelMapper = uiModelMapper;
    }

    @Override
    public void flush(final String value) {
        final int rowIndex = context.getRowIndex();
        final int columnIndex = context.getColumnIndex();
        if (value == null || value.trim().isEmpty()) {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteCellValueCommand(rowIndex,
                                                                     columnIndex,
                                                                     gridWidget,
                                                                     uiModelMapper));
        } else {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new SetCellValueCommand<>(rowIndex,
                                                                    columnIndex,
                                                                    gridWidget,
                                                                    new BaseGridCellValue<>(value),
                                                                    uiModelMapper));
        }
    }
}
