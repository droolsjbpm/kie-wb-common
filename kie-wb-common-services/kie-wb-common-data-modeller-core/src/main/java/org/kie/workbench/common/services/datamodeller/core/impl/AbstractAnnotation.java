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

package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAnnotation implements Annotation {

    private AnnotationDefinition annotationDefinition;

    private Map<String, Object> values = new HashMap<String, Object>();

    public AbstractAnnotation() {
        //errai marshalling
    }

    public AbstractAnnotation(AnnotationDefinition annotationDefinition) {
        this.annotationDefinition = annotationDefinition;
    }

    @Override
    public Object getValue(String annotationMemberName) {
        return values.get(annotationMemberName);
    }

    @Override
    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public void setValue(String annotationMemberName, Object value) {
        values.put(annotationMemberName, value);
    }

    @Override
    public void removeValue( String annotationMemberName ) {
        values.remove( annotationMemberName );
    }

    @Override
    public AnnotationDefinition getAnnotationDefinition() {
        return annotationDefinition;
    }

    @Override
    public String getClassName() {
        return annotationDefinition.getClassName();
    }

}