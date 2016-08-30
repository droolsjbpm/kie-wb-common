/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.events;

import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.uberfire.backend.vfs.Path;

@Portable
public class DataModelerEvent {

    protected DataObject currentDataObject;

    protected ObjectProperty currentField;

    protected Project currentProject;

    protected String source;

    protected String contextId;

    protected Path path;

    public static final String DATA_MODEL_BROWSER = "DATA_MODEL_BROWSER";

    public static final String DATA_OBJECT_BROWSER = "DATA_OBJECT_BROWSER";

    public static final String DATA_OBJECT_EDITOR = "DATA_OBJECT_EDITOR";

    public DataModelerEvent() {
    }

    public DataModelerEvent( DataObject currentDataObject ) {
        this.currentDataObject = currentDataObject;
    }

    public DataModelerEvent( String contextId, String source, DataObject currentDataObject ) {
        this( contextId, source, currentDataObject, null );
    }

    public DataModelerEvent( String source, DataObject currentDataObject ) {
        this( null, source, currentDataObject, null );
    }

    public DataModelerEvent( String contextId, String source, DataObject currentDataObject, ObjectProperty currentField ) {
        this.contextId = contextId;
        this.source = source;
        this.currentDataObject = currentDataObject;
        this.currentField = currentField;
    }

    public DataModelerEvent( String contextId, Project currentProject, DataObject currentDataObject ) {
        this.contextId = contextId;
        this.currentProject = currentProject;
        this.currentDataObject = currentDataObject;
    }

    public DataModelerEvent( Project currentProject, DataObject currentDataObject ) {
        this.currentProject = currentProject;
        this.currentDataObject = currentDataObject;
    }

    public DataObject getCurrentDataObject() {
        return currentDataObject;
    }

    public DataModelerEvent withCurrentDataObject( DataObject currentDataObject ) {
        setCurrentDataObject( currentDataObject );
        return this;
    }

    public void setCurrentDataObject( DataObject currentDataObject ) {
        this.currentDataObject = currentDataObject;
    }

    public ObjectProperty getCurrentField() {
        return currentField;
    }

    public DataModelerEvent withCurrentField( ObjectProperty currentField ) {
        setCurrentField( currentField );
        return this;
    }

    public void setCurrentField( ObjectProperty currentField ) {
        this.currentField = currentField;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public DataModelerEvent withCurrentProject( Project currentProject ) {
        setCurrentProject( currentProject );
        return this;
    }

    public void setCurrentProject( Project currentProject ) {
        this.currentProject = currentProject;
    }

    public String getSource() {
        return source;
    }

    public DataModelerEvent withSource( String source ) {
        setSource( source );
        return this;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    public String getContextId() {
        return contextId;
    }

    public DataModelerEvent withContextId( String contextId ) {
        setContextId( contextId );
        return this;
    }

    public void setContextId( String contextId ) {
        this.contextId = contextId;
    }

    public Path getPath() {
        return path;
    }

    public DataModelerEvent withPath( Path path ) {
        setPath( path );
        return this;
    }

    public void setPath( Path path ) {
        this.path = path;
    }

    public boolean isFrom( Project project ) {
        return this.currentProject != null && this.currentProject.equals( project );
    }

    public boolean isFrom( String source ) {
        return this.source != null && this.source.equals( source );
    }

    public boolean isFromContext( String contextId ) {
        return this.contextId != null && this.contextId.equals( contextId );
    }

}
