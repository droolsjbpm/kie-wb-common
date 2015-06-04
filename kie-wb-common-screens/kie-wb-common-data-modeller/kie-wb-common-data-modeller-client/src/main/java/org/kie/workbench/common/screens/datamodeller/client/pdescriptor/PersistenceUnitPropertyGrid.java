/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.pdescriptor;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class PersistenceUnitPropertyGrid
        implements IsWidget,
                   PersistenceUnitPropertyGridView.Presenter {

    private PersistenceUnitPropertyGridView view;

    private List<PropertyRow> properties;

    public PersistenceUnitPropertyGrid() {
    }

    @Inject
    public PersistenceUnitPropertyGrid( PersistenceUnitPropertyGridView view ) {
        this.view = view;
        view.setPresenter( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void fillList( List<PropertyRow> properties ) {
        this.properties = properties;
        view.setList( properties );
    }

    public List<PropertyRow> getProperties() {
        return properties;
    }

    @Override
    public void onAddProperty() {
        //TODO add validations
        String propertyName = view.getNewPropertyName();
        String propertyValue = view.getNewPropertyValue();
        properties.add( new PropertyRowImpl( propertyName, propertyValue ) );
        fillList( properties );

        view.setNewPropertyName( "" );
        view.setNewPropertyValue( "" );
    }

    @Override
    public void onRemoveProperty( PropertyRow propertyRow ) {
        properties.remove( propertyRow );
        fillList( properties );
    }

    public void setReadOnly( boolean readOnly ) {
        view.setReadOnly( readOnly );
    }

    public void redraw() {
        view.redraw();
    }
}
