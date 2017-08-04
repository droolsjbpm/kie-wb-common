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
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = DMNDiagram.DMNDiagramBuilder.class)
@FormDefinition(policy = FieldPolicy.ONLY_MARKED)
@CanContain(roles = {
        "input-data",
        "knowledge-source",
        "business-knowledge-model",
        "decision",
        "text-annotation",
        "association",
        "information-requirement",
        "knowledge-requirement",
        "authority-requirement"
})
public class DMNDiagram extends DMNModelInstrumentedBase {

    @Category
    public static final transient String stunnerCategory = Categories.DIAGRAM;

    @Title
    public static final transient String stunnerTitle = "DMNDiagram";

    @Description
    public static final transient String stunnerDescription = "DMNDiagram";

    @Labels
    public static final Set<String> stunnerLabels = new HashSet<String>() {{
        add("dmn_diagram");
    }};
    
    protected Definitions definitions;

    @PropertySet
    @FormField
    @Valid
    protected BackgroundSet backgroundSet;

    @PropertySet
    @FormField
    protected FontSet fontSet;

    @PropertySet
    @FormField
    protected RectangleDimensionsSet dimensionsSet;

    @NonPortable
    public static class DMNDiagramBuilder extends BaseNodeBuilder<DMNDiagram> {

        @Override
        public DMNDiagram build() {
            return new DMNDiagram(new Definitions(),
                                  new BackgroundSet(),
                                  new FontSet(),
                                  new RectangleDimensionsSet());
        }
    }

    public DMNDiagram() {
    }

    public DMNDiagram(final @MapsTo("definitions") Definitions definitions,
                      final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                      final @MapsTo("fontSet") FontSet fontSet,
                      final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet) {
        this.definitions = definitions;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
    }
    
    public Definitions getDefinitions() {
        return definitions;
    }
    
    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }

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
}
