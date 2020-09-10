/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.kogito.client.marshalling.converters.associations;

import java.util.Objects;

import com.google.common.base.Strings;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.definition.dto.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Property;

public class VariableDeclaration {

    private final ItemDefinition typeDeclaration;
    private final Property typedIdentifier;
    private String identifier;
    private String type;
    private String tags;

    public VariableDeclaration(String identifier, String type) {
        this(identifier, type, "");
    }

    public VariableDeclaration(String identifier, String type, String tags) {
        this.setIdentifier(identifier);
        this.type = type;

        this.typeDeclaration = new ItemDefinition();
        this.typeDeclaration.setId(Ids.item(this.getIdentifier()));
        this.typeDeclaration.setStructureRef(type);

        this.typedIdentifier = new Property();
        this.typedIdentifier.setId(Ids.typedIdentifier("GLOBAL", this.getIdentifier()));
        this.typedIdentifier.setName(identifier);
        this.typedIdentifier.setItemSubjectRef(typeDeclaration.getId());
        this.tags = tags;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        String safeIdentifier = identifier.replaceAll(" ", "-");
        this.identifier = safeIdentifier;
    }

    public static VariableDeclaration fromString(String encoded) {
        String[] split = encoded.split(":");
        String identifier = split[0];
        String type = (split.length == 2 || split.length == 3) ? split[1] : "";
        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("Variable identifier cannot be empty. Given: '" + encoded + "'");
        }
        String tags = "";

        if (split.length == 3) {
            tags = split[2];
        }

        return new VariableDeclaration(identifier, type, tags);
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ItemDefinition getTypeDeclaration() {
        return typeDeclaration;
    }

    public Property getTypedIdentifier() {
        return typedIdentifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, type, tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VariableDeclaration that = (VariableDeclaration) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(type, that.type) &&
                Objects.equals(tags, that.tags);
    }

    @Override
    public String toString() {
        String tagString = "";
        if (!Strings.isNullOrEmpty(tags)) {
            tagString = ":" + tags;
        }
        if (type == null || type.isEmpty()) {
            return typedIdentifier.getName() + tagString;
        } else {
            return typedIdentifier.getName() + ":" + type + tagString;
        }
    }
}

