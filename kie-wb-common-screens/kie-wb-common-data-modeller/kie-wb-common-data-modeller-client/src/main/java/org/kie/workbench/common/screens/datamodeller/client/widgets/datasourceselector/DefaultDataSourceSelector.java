/*
 * Copyright 2016 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.datasourceselector;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.Window;
import org.guvnor.common.services.project.model.Module;
import org.uberfire.annotations.FallbackImplementation;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * Default implementation for a data source selector.
 */
@ApplicationScoped
@FallbackImplementation
public class DefaultDataSourceSelector
        implements DataSourceSelector {

    public DefaultDataSourceSelector() {
    }

    @Override
    public void setModuleSelection(final Module module) {

    }

    @Override
    public void setGlobalSelection() {

    }

    @Override
    public void show(ParameterizedCommand<DataSourceInfo> onSelectCommand, Command onCloseCommand) {
        Window.alert("Data sources selection is currently only available for the LiveSpark platform.");
    }
}
