/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Label;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = BusinessKnowledgeModel.BusinessKnowledgeModelBuilder.class)
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, startElement = "id")
public class BusinessKnowledgeModel extends DRGElement {

    @Category
    public static final transient String stunnerCategory = Categories.NODES;

    @Title
    public static final transient String stunnerTitle = "DMN BusinessKnowledgeModel";

    @Description
    public static final transient String stunnerDescription = "DMN BusinessKnowledgeModel";

    @Labels
    private final Set<String> stunnerLabels = new HashSet<String>() {{
        add("business-knowledge-model");
    }};

    @PropertySet
    @FormField(afterElement = "name")
    protected InformationItem variable;

    @PropertySet
    @FormField(afterElement = "variable")
    protected FunctionDefinition encapsulatedLogic;

    @PropertySet
    @FormField(afterElement = "encapsulatedLogic")
    @Valid
    protected BackgroundSet backgroundSet;

    @PropertySet
    @FormField(afterElement = "backgroundSet")
    @Valid
    protected FontSet fontSet;

    @PropertySet
    @FormField(afterElement = "fontSet")
    @Valid
    protected RectangleDimensionsSet dimensionsSet;

    @NonPortable
    public static class BusinessKnowledgeModelBuilder extends BaseNodeBuilder<BusinessKnowledgeModel> {

        @Override
        public BusinessKnowledgeModel build() {
            return new BusinessKnowledgeModel(new Id(),
                                              new Label(),
                                              new org.kie.workbench.common.dmn.api.property.dmn.Description(),
                                              new Name(),
                                              new InformationItem(),
                                              new FunctionDefinition(),
                                              new BackgroundSet(),
                                              new FontSet(),
                                              new RectangleDimensionsSet());
        }
    }

    public BusinessKnowledgeModel() {
    }

    public BusinessKnowledgeModel(final @MapsTo("id") Id id,
                                  final @MapsTo("label") Label label,
                                  final @MapsTo("description") org.kie.workbench.common.dmn.api.property.dmn.Description description,
                                  final @MapsTo("name") Name name,
                                  final @MapsTo("variable") InformationItem variable,
                                  final @MapsTo("encapsulatedLogic") FunctionDefinition encapsulatedLogic,
                                  final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                  final @MapsTo("fontSet") FontSet fontSet,
                                  final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet) {
        super(id,
              label,
              description,
              name);
        this.variable = variable;
        this.encapsulatedLogic = encapsulatedLogic;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
    }

    // -----------------------
    // Stunner core properties
    // -----------------------

    public String getStunnerCategory() {
        return stunnerCategory;
    }

    public String getStunnerTitle() {
        return stunnerTitle;
    }

    public String getStunnerDescription() {
        return stunnerDescription;
    }

    public Set<String> getStunnerLabels() {
        return stunnerLabels;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(final BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setFontSet(final FontSet fontSet) {
        this.fontSet = fontSet;
    }

    public RectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(final RectangleDimensionsSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public InformationItem getVariable() {
        return variable;
    }

    public void setVariable(final InformationItem variable) {
        this.variable = variable;
    }

    public FunctionDefinition getEncapsulatedLogic() {
        return encapsulatedLogic;
    }

    public void setEncapsulatedLogic(final FunctionDefinition value) {
        this.encapsulatedLogic = value;
    }
}
