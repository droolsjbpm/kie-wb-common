/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.social.hp.client.userpage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.kie.workbench.common.screens.social.hp.client.userpage.main.MainPresenter;
import org.kie.workbench.common.screens.social.hp.client.userpage.main.header.HeaderPresenter;

@Dependent
public class UserHomePageMainView extends Composite implements UserHomePageMainPresenter.View {

    private final FlowPanel panel = new FlowPanel();
    private UserHomePageMainPresenter presenter = null;

    private HeaderPresenter header  = null;
    private MainPresenter main = null;

    @PostConstruct
    public void setup() {
        initWidget( panel );
    }

    @Override
    public void init( final UserHomePageMainPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setHeader( HeaderPresenter header ) {
        if ( this.header == null ) {
            this.header = header;
            panel.clear();
            panel.add( header.getView() );
        }
    }

    @Override
    public void setMain( MainPresenter main ) {
        if ( this.main == null ) {
            this.main = main;
            panel.add( main.getView() );
        }
    }

}
