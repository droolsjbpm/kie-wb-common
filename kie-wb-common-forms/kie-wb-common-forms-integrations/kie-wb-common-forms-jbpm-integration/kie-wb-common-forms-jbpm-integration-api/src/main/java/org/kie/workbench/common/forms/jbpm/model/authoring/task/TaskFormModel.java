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

package org.kie.workbench.common.forms.jbpm.model.authoring.task;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.jbpm.model.authoring.AbstractJBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMVariable;

@Portable
public class TaskFormModel extends AbstractJBPMFormModel {

    protected String processId;

    protected String taskId;

    protected String taskName;

    protected String formName;

    public TaskFormModel( @MapsTo( "processId" ) String processId,
                          @MapsTo( "taskId" ) String taskId,
                          @MapsTo( "taskName" ) String taskName,
                          @MapsTo( "formName" ) String formName,
                          @MapsTo( "variables" ) List<JBPMVariable> variables ) {
        super( variables );
        this.processId = processId;
        this.taskId = taskId;
        this.taskName = taskName;
        this.formName = formName;
    }

    @Override
    public String getName() {
        return "task";
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId( String processId ) {
        this.processId = processId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId( String taskId ) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName( String taskName ) {
        this.taskName = taskName;
    }

    @Override
    public String getFormName() {
        return formName;
    }

    public void setFormName( String formName ) {
        this.formName = formName;
    }
}
