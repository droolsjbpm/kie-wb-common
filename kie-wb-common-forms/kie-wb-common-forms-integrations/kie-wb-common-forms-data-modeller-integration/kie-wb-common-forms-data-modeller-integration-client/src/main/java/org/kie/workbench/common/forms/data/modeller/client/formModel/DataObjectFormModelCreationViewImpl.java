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

package org.kie.workbench.common.forms.data.modeller.client.formModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;

@Templated
public class DataObjectFormModelCreationViewImpl extends Composite implements DataObjectFormModelCreationView {

    @DataField
    DivElement formGroup = Document.get().createDivElement();

    @DataField
    DivElement modelHelpBlock = Document.get().createDivElement();

    @DataField
    private ValueListBox<DataObjectFormModel> listBox = new ValueListBox<>( new Renderer<DataObjectFormModel>() {
        @Override
        public String render( DataObjectFormModel formModel ) {
            if ( formModel != null ) {
                return formModel.getClassName();
            }
            return "";
        }

        @Override
        public void render( DataObjectFormModel object, Appendable appendable ) throws IOException {
            appendable.append( render( object ) );
        }
    } );

    @Override
    public void setFormModels( List<DataObjectFormModel> formModels ) {
        listBox.setValue( null );
        listBox.setAcceptableValues( formModels );
    }

    @Override
    public DataObjectFormModel getSelectedFormModel() {
        return listBox.getValue();
    }

    @Override
    public void reset() {
        listBox.setValue( null );
        listBox.setAcceptableValues( new ArrayList<>() );
        listBox.reset();
        clearValidationErrors();
    }

    @Override
    public void clearValidationErrors() {
        formGroup.removeClassName( ValidationState.ERROR.getCssName() );
        modelHelpBlock.setInnerText( "" );
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        formGroup.addClassName( ValidationState.ERROR.getCssName() );
        modelHelpBlock.setInnerText( errorMessage );
    }
}
