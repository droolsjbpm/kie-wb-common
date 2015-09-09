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

package org.kie.workbench.common.screens.datamodeller.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.type.PersistenceDescriptorTypeDefinition;
import org.uberfire.client.resources.UberfireResources;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class PersistenceDescriptorType
        extends PersistenceDescriptorTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return new Image( UberfireResources.INSTANCE.images().typeGenericFile() );
    }

    @Override
    public String getDescription() {
        String desc = Constants.INSTANCE.persistence_descriptor_resource_type_description();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}