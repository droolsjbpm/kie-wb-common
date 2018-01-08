/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.model.kiedeployment;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class KieDeploymentDescriptorContent {

    private String runtimeStrategy;
    private String persistenceUnitName;
    private String persistenceMode;
    private String auditPersistenceUnitName;
    private String auditMode;

    private List<BlergsModel> marshallingStrategies;
    private List<BlergsModel> eventListeners;
    private List<BlergsModel> globals;
    private List<BlergsModel> requiredRoles;

    public String getRuntimeStrategy() {
        return runtimeStrategy;
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public String getPersistenceMode() {
        return persistenceMode;
    }

    public String getAuditPersistenceUnitName() {
        return auditPersistenceUnitName;
    }

    public String getAuditMode() {
        return auditMode;
    }

    public List<BlergsModel> getMarshallingStrategies() {
        return marshallingStrategies;
    }

    public List<BlergsModel> getEventListeners() {
        return eventListeners;
    }

    public List<BlergsModel> getGlobals() {
        return globals;
    }

    public List<BlergsModel> getRequiredRoles() {
        return requiredRoles;
    }

    public void setRuntimeStrategy(final String runtimeStrategy) {
        this.runtimeStrategy = runtimeStrategy;
    }

    public void setPersistenceUnitName(final String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    public void setPersistenceMode(final String persistenceMode) {
        this.persistenceMode = persistenceMode;
    }

    public void setAuditPersistenceUnitName(final String auditPersistenceUnitName) {
        this.auditPersistenceUnitName = auditPersistenceUnitName;
    }

    public void setAuditMode(final String auditMode) {
        this.auditMode = auditMode;
    }

    public void setMarshallingStrategies(final List<BlergsModel> marshallingStrategies) {
        this.marshallingStrategies = marshallingStrategies;
    }

    public void setEventListeners(final List<BlergsModel> eventListeners) {
        this.eventListeners = eventListeners;
    }

    public void setGlobals(final List<BlergsModel> globals) {
        this.globals = globals;
    }

    public void setRequiredRoles(final List<BlergsModel> requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    @Portable
    public static class BlergsModel {

        private String name;
        private String resolver;
        private Map<String, String> parameters;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getResolver() {
            return resolver;
        }

        public void setResolver(final String resolver) {
            this.resolver = resolver;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public void setParameters(final Map<String, String> parameters) {
            this.parameters = parameters;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KieDeploymentDescriptorContent that = (KieDeploymentDescriptorContent) o;
        return Objects.equals(runtimeStrategy, that.runtimeStrategy) &&
                Objects.equals(persistenceUnitName, that.persistenceUnitName) &&
                Objects.equals(persistenceMode, that.persistenceMode) &&
                Objects.equals(auditPersistenceUnitName, that.auditPersistenceUnitName) &&
                Objects.equals(auditMode, that.auditMode) &&
                Objects.equals(marshallingStrategies, that.marshallingStrategies) &&
                Objects.equals(eventListeners, that.eventListeners) &&
                Objects.equals(globals, that.globals) &&
                Objects.equals(requiredRoles, that.requiredRoles);
    }

    @Override
    public int hashCode() {

        return Objects.hash(runtimeStrategy,
                            persistenceUnitName,
                            persistenceMode,
                            auditPersistenceUnitName,
                            auditMode,
                            marshallingStrategies,
                            eventListeners,
                            globals,
                            requiredRoles);
    }
}
