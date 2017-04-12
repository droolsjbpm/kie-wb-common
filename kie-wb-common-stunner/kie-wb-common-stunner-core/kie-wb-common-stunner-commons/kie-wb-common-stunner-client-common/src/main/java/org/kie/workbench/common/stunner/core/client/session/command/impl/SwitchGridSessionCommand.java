/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasGrid;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class SwitchGridSessionCommand extends AbstractClientSessionCommand<AbstractClientSession> {

    public final static CanvasGrid[] GRIDS = new CanvasGrid[]{
            CanvasGrid.SMALL_POINT_GRID, CanvasGrid.DEFAULT_GRID,
            CanvasGrid.DRAG_GRID, null
    };
    private static final byte DEFAULT_GRID_INDEX = 0;
    private byte gridIndex;

    public SwitchGridSessionCommand() {
        super(true);
    }

    @Override
    public SwitchGridSessionCommand bind(final AbstractClientSession session) {
        super.bind(session);
        resetGrid();
        return this;
    }

    @Override
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);
        if (++gridIndex == GRIDS.length) {
            gridIndex = 0;
        }
        updateGrid();
        // Run the callback.
        callback.onSuccess();
    }

    private void resetGrid() {
        this.gridIndex = DEFAULT_GRID_INDEX;
        updateGrid();
    }

    private void updateGrid() {
        getSession().getCanvas().setGrid(GRIDS[gridIndex]);
    }
}
