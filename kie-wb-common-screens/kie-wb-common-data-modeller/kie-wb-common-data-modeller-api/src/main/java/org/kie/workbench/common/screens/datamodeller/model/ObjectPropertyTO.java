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

import java.util.ArrayList;
import java.util.List;

@Portable
public class ObjectPropertyTO {

    private String className;

    private String name;

    //Remembers the original name for the DataObject.
    //This value shouldn't be changed in the UI.
    private String originalName;

    private boolean multiple = false;

    private boolean baseType = true;
    
    private String bag;

    private int modifiers = 0;

    private VisibilityTO visibility;

    private List<AnnotationTO> annotations = new ArrayList<AnnotationTO>();
    
    public static final String DEFAULT_PROPERTY_BAG = "java.util.List";

    private DataModelTO.TOStatus status = DataModelTO.TOStatus.VOLATILE;

    private boolean _static = false;

    private boolean _final = false;

    public ObjectPropertyTO() {
    }

    public ObjectPropertyTO(String name, String className, boolean multiple, boolean baseType) {
        this.name = name;
        this.className = className;
        this.multiple = multiple;
        this.baseType = baseType;
        if (multiple) {
            this.bag = DEFAULT_PROPERTY_BAG;
        }
    }

    public ObjectPropertyTO(String name, String className, boolean multiple, boolean baseType, String bag, int modifiers) {
        this.name = name;
        this.className = className;
        this.multiple = multiple;
        this.baseType = baseType;
        this.bag = bag;
        this.modifiers = modifiers;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isBaseType() {
        return baseType;
    }

    public void setBaseType(boolean baseType) {
        this.baseType = baseType;
    }

    public VisibilityTO getVisibility() {
        return visibility;
    }

    public boolean isStatic() {
        return _static;
    }

    public boolean isFinal() {
        return  _final;
    }

    public void setVisibility( VisibilityTO visibility ) {
        this.visibility = visibility;
    }

    public String getBag() {
        return bag;
    }

    public void setBag(String bag) {
        this.bag = bag;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName( String originalName ) {
        this.originalName = originalName;
    }

    public DataModelTO.TOStatus getStatus() {
        return status;
    }

    public void setStatus( DataModelTO.TOStatus status ) {
        this.status = status;
    }

    public boolean isVolatile() {
        return getStatus() == DataModelTO.TOStatus.VOLATILE;
    }

    public boolean isPersistent() {
        return getStatus() == DataModelTO.TOStatus.PERSISTENT;
    }

    public int getModifiers() {
        return modifiers;
    }

    public List<AnnotationTO> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<AnnotationTO> annotations) {
        this.annotations = annotations;
    }

    public AnnotationTO getAnnotation(String annotationClassName) {
        AnnotationTO annotation = null;
        int index = _getAnnotation(annotationClassName);
        if (index >= 0) annotation = annotations.get(_getAnnotation(annotationClassName));
        return annotation;
    }

    public void addAnnotation(AnnotationTO annotation) {
        annotations.add(annotation);
    }

    public AnnotationTO addAnnotation(AnnotationDefinitionTO annotationDefinitionTO, String memberName, Object value) {
        AnnotationTO annotation = new AnnotationTO(annotationDefinitionTO);
        annotation.setValue(memberName, value);
        addAnnotation(annotation);
        return annotation;
    }

    public void removeAnnotation(AnnotationTO annotation) {
        if (annotation != null) {
            int index = _getAnnotation(annotation.getClassName());
            if (index >= 0) annotations.remove(index);
        }
    }
    
    public String getStringId() {
        StringBuilder strId = new StringBuilder();
        strId.append(getName());
        strId.append(getClassName());
        strId.append(isMultiple());
        strId.append(getBag());
        for (AnnotationTO annotationTO : annotations) {
            strId.append(annotationTO.getStringId());
        }
        return strId.toString();
    }

    public boolean nameChanged() {
        return !getName().equals(getOriginalName());
    }

    private Integer _getAnnotation(String annotationClassName) {
        if (annotationClassName == null || "".equals(annotationClassName)) return -1;
        for (int i = 0; i < annotations.size(); i++) {
            AnnotationTO _annotation = annotations.get(i);
            if (annotationClassName.equals(_annotation.getClassName())) return i;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObjectPropertyTO that = (ObjectPropertyTO) o;

        if (baseType != that.baseType) {
            return false;
        }
        if (modifiers != that.modifiers) {
            return false;
        }
        if (multiple != that.multiple) {
            return false;
        }
        if (annotations != null ? !annotations.equals(that.annotations) : that.annotations != null) {
            return false;
        }
        if (bag != null ? !bag.equals(that.bag) : that.bag != null) {
            return false;
        }
        if (className != null ? !className.equals(that.className) : that.className != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (originalName != null ? !originalName.equals(that.originalName) : that.originalName != null) {
            return false;
        }
        if (status != that.status) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (originalName != null ? originalName.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (multiple ? 1 : 0);
        result = ~~result;
        result = 31 * result + (baseType ? 1 : 0);
        result = ~~result;
        result = 31 * result + (bag != null ? bag.hashCode() : 0);
        result = ~~result;
        result = 31 * result + modifiers;
        result = ~~result;
        result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = ~~result;
        return result;
    }
}