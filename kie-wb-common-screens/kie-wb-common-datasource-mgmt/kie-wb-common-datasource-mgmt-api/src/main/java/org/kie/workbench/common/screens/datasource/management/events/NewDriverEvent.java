/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.events;

import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;

@Portable
public class NewDriverEvent
        extends BaseDriverEvent {

    public NewDriverEvent( @MapsTo( "driverDef" ) final DriverDef driverDef,
                           @MapsTo( "project" ) final Project project,
                           @MapsTo( "sessionId" ) final String sessionId,
                           @MapsTo( "identity" ) final String identity ) {
        super( driverDef, project, sessionId, identity );
    }

    public NewDriverEvent( final DriverDef driverDef,
                           final String sessionId,
                           final String identity ) {
        this( driverDef, null, sessionId, identity );
    }
}