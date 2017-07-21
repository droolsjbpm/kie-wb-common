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
import java.util.List;
import java.util.Set;
import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.dmn.model.v1_1.DMNElementReference;
import org.kie.dmn.model.v1_1.InformationRequirement;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Question;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = Decision.DecisionBuilder.class)
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, startElement = "id")
public class Decision extends DRGElement {

    @Category
    public static final transient String stunnerCategory = Categories.NODES;

    @Title
    public static final transient String stunnerTitle = "DMN Decision";

    @Description
    public static final transient String stunnerDescription = "DMN Decision";

    @Labels
    private final Set<String> stunnerLabels = new HashSet<String>() {{
        add("decision");
    }};

    @Property
    @FormField(afterElement = "name")
    protected Question question;

    @Property
    @FormField(afterElement = "question")
    protected AllowedAnswers allowedAnswers;

    @PropertySet
    @FormField(afterElement = "allowedAnswers")
    protected InformationItem variable;

    protected Expression expression;

    @PropertySet
    @FormField
    @Valid
    protected BackgroundSet backgroundSet;

    @PropertySet
    @FormField
    @Valid
    protected FontSet fontSet;

    @PropertySet
    @FormField
    @Valid
    protected RectangleDimensionsSet dimensionsSet;

    @NonPortable
    public static class DecisionBuilder extends BaseNodeBuilder<Decision> {

        @Override
        public Decision build() {
            return new Decision(new Id(),
                                new org.kie.workbench.common.dmn.api.property.dmn.Description(),
                                new Name(),
                                new Question(),
                                new AllowedAnswers(),
                                new InformationItem(),
                                new LiteralExpression(),
                                new BackgroundSet(),
                                new FontSet(),
                                new RectangleDimensionsSet());
        }
    }

    public Decision() {
    }

    public Decision(final @MapsTo("id") Id id,
                    final @MapsTo("description") org.kie.workbench.common.dmn.api.property.dmn.Description description,
                    final @MapsTo("name") Name name,
                    final @MapsTo("question") Question question,
                    final @MapsTo("allowedAnswers") AllowedAnswers allowedAnswers,
                    final @MapsTo("variable") InformationItem variable,
                    final @MapsTo("expression") Expression expression,
                    final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                    final @MapsTo("fontSet") FontSet fontSet,
                    final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet) {
        super(id,
              description,
              name);
        this.question = question;
        this.allowedAnswers = allowedAnswers;
        this.variable = variable;
        this.expression = expression;
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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(final Question question) {
        this.question = question;
    }

    public AllowedAnswers getAllowedAnswers() {
        return allowedAnswers;
    }

    public void setAllowedAnswers(final AllowedAnswers allowedAnswers) {
        this.allowedAnswers = allowedAnswers;
    }

    public InformationItem getVariable() {
        return variable;
    }

    public void setVariable(final InformationItem variable) {
        this.variable = variable;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(final Expression expression) {
        this.expression = expression;
    }

    @Override
    public org.kie.dmn.model.v1_1.Decision asDMN( List<Edge<?, Node<?, ?>>> inEdges ) {
        org.kie.dmn.model.v1_1.Decision d = new org.kie.dmn.model.v1_1.Decision();
        d.setId( this.getId().getValue() );
        d.setName( this.getName().getValue() );
        for ( Edge<?, ?> e : inEdges ) {
            Node<?,?> sourceNode = e.getSourceNode();
            if ( sourceNode.getContent() instanceof View<?> ) {
                View<?> view = (View<?>) sourceNode.getContent();
                if ( view.getDefinition() instanceof DRGElement ) {
                    DRGElement drgElement = (DRGElement) view.getDefinition();
                    InformationRequirement iReq = new InformationRequirement();
                    DMNElementReference ri = new DMNElementReference();
                    ri.setHref( new StringBuilder("#").append( drgElement.getId().getValue() ).toString() );
                    iReq.setRequiredInput( ri );
                    d.getInformationRequirement().add(iReq);
                }
            }
        }
        return d;
    }
    
    public static Decision from( org.kie.dmn.model.v1_1.Decision dmn ) {
        Id id = new Id( dmn.getId() );
        org.kie.workbench.common.dmn.api.property.dmn.Description description = new org.kie.workbench.common.dmn.api.property.dmn.Description( dmn.getDescription() );
        Name name = new Name( dmn.getName() );
        InformationItem informationItem = InformationItem.from( dmn.getVariable() );
        Decision result = new Decision(id,
                description,
                name,
                new Question(),
                new AllowedAnswers(),
                informationItem,
                new LiteralExpression(),
                new BackgroundSet(),
                new FontSet(),
                new RectangleDimensionsSet());
        return result;
    }

}
