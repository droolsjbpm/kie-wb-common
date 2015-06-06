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

import com.google.gwt.user.client.ui.IsWidget;

public interface PersistenceUnitPropertyGridView
        extends IsWidget {

    interface Presenter {

        void onAddProperty();

        void onRemoveProperty( PropertyRow property );

    }

    void setPresenter( Presenter presenter );

    void setReadOnly( boolean readOnly );

    void setList( List<PropertyRow> properties );

    void redraw();

    String getNewPropertyName();

    String getNewPropertyValue();

    void setNewPropertyName( String name );

    void setNewPropertyValue( String value );

}
