/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.*;

@Portable
public class AnnotationTO {

    private String className;

    private AnnotationDefinitionTO annotationDefinition;

    private Map<String, Object> values = new HashMap<String, Object>();

    public AnnotationTO() {
    }

    public AnnotationTO(AnnotationDefinitionTO annotationDefinition) {
        this.annotationDefinition = annotationDefinition;
        this.className = annotationDefinition.getClassName();
    }

    public Object getValue(String annotationMemberName) {
        return values.get(annotationMemberName);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValue(String annotationMemberName, Object value) {
        values.put(annotationMemberName,  value);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public AnnotationDefinitionTO getAnnotationDefinition() {
        return annotationDefinition;
    }

    public void setAnnotationDefinition(AnnotationDefinitionTO annotationDefinition) {
        this.annotationDefinition = annotationDefinition;
    }
    
    public String getStringId() {
        StringBuilder strId = new StringBuilder();
        strId.append(getAnnotationDefinition().getClassName());
        SortedMap<String, Object> sortedValues = new TreeMap<String, Object>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            sortedValues.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Object> sortedEntry : sortedValues.entrySet()) {
            strId.append(sortedEntry.getKey());
            strId.append(sortedEntry.getValue() != null ? sortedEntry.getValue().toString() : "");
        }
        return strId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AnnotationTO that = (AnnotationTO) o;

        if (annotationDefinition != null ? !annotationDefinition.equals(that.annotationDefinition) : that.annotationDefinition != null) {
            return false;
        }
        if (className != null ? !className.equals(that.className) : that.className != null) {
            return false;
        }
        if (values != null ? !values.equals(that.values) : that.values != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (annotationDefinition != null ? annotationDefinition.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (values != null ? values.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
