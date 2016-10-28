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

package org.kie.workbench.common.widgets.client.widget;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;

public class KSessionSelector
        implements IsWidget,
                   SelectionChangeEvent.HasSelectionChangedHandlers {

    private static String DEFAULT_KIE_BASE    = "defaultKieBase";
    private static String DEFAULT_KIE_SESSION = "defaultKieSession";
    private static String NON_EXISTING_KBASE  = "---";

    private KSessionSelectorView      view;
    private Caller<KieProjectService> projectService;

    private Caller<KModuleService> kModuleService;
    private KModuleModel           kmodule;

    @Inject
    public KSessionSelector( final KSessionSelectorView view,
                             final Caller<KieProjectService> projectService,
                             final Caller<KModuleService> kModuleService ) {
        this.view = view;
        this.projectService = projectService;
        this.kModuleService = kModuleService;

        view.setPresenter( this );
    }

    public void init( final Path path,
                      final String ksession ) {
        projectService.call( getSuccessfulResolveProjectCallback( ksession ) ).resolveProject( path );
    }

    private RemoteCallback<KieProject> getSuccessfulResolveProjectCallback( final String currentKSession ) {
        return new RemoteCallback<KieProject>() {
            @Override
            public void callback( KieProject project ) {
                kModuleService.call( getSuccessfulLoadKModuleCallback( currentKSession ) ).load( project.getKModuleXMLPath() );
            }
        };
    }

    private RemoteCallback<KModuleModel> getSuccessfulLoadKModuleCallback( final String currentKSession ) {
        return new RemoteCallback<KModuleModel>() {
            @Override
            public void callback( KModuleModel kmodule ) {
                KSessionSelector.this.kmodule = kmodule;

                initKBases( currentKSession );
                selectCurrentKBaseAndKSession( currentKSession );
            }
        };
    }

    private void selectCurrentKBaseAndKSession( final String currentKSession ) {
        if ( isNotNullOrEmpty( currentKSession ) && kmoduleContainsCurrentKSession( currentKSession ) ) {
            selectFromModel( currentKSession );
        } else {
            selectFirstKBaseAndKSession();
        }
    }

    private void initKBases( final String currentKSession ) {
        view.clear();

        if ( kmodule.getKBases().isEmpty() ) {
            addMockKBaseModel( DEFAULT_KIE_BASE, DEFAULT_KIE_SESSION );
            view.addKBase( DEFAULT_KIE_BASE );
        } else {
            for ( KBaseModel kBase : kmodule.getKBases().values() ) {
                view.addKBase( kBase.getName() );
            }
        }
        if ( isNotNullOrEmpty( currentKSession ) && !kmoduleContainsCurrentKSession( currentKSession ) ) {
            addMockKBaseModel( NON_EXISTING_KBASE,
                               currentKSession );
            view.addKBase( NON_EXISTING_KBASE );
            view.showWarningSelectedKSessionDoesNotExist();
        }
    }

    private void addMockKBaseModel( final String kbaseName,
                                    final String ksessionsName ) {
        KBaseModel kbaseModel = new KBaseModel();
        kbaseModel.setName( kbaseName );
        KSessionModel ksessionModel = new KSessionModel();
        ksessionModel.setName( ksessionsName );
        kbaseModel.getKSessions().add( ksessionModel );
        kmodule.getKBases().put( kbaseName, kbaseModel );
    }

    private boolean isNotNullOrEmpty( final String ksession ) {
        return ksession != null && !ksession.trim().isEmpty();
    }

    private boolean kmoduleContainsCurrentKSession( final String currentKSession ) {
        if ( isNotNullOrEmpty( currentKSession ) ) {
            for ( KBaseModel kbase : kmodule.getKBases().values() ) {
                for ( KSessionModel ksession : kbase.getKSessions() ) {
                    if ( ksession.getName().equals( currentKSession ) ) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void onKBaseSelected( final String kbaseName ) {
        final List<KSessionModel> ksessions = kmodule.getKBases().get( kbaseName ).getKSessions();
        listKSessions( ksessions );

        view.setSelected( kbaseName,
                          ksessions.iterator().next().getName() );
    }

    public String getSelectedKSessionName(){
        return view.getSelectedKSessionName();
    }

    private void selectFromModel( final String currentKSession ) {
        for ( KBaseModel kbase : kmodule.getKBases().values() ) {
            for ( KSessionModel ksession : kbase.getKSessions() ) {
                if ( ksession.getName().equals( currentKSession ) ) {
                    listKSessions( kmodule.getKBases().get( kbase.getName() ).getKSessions() );
                    view.setSelected( kbase.getName(),
                                      currentKSession );
                    break;
                }
            }
        }
    }

    private void selectFirstKBaseAndKSession() {

        KBaseModel firstKBase = kmodule.getKBases().values().iterator().next();

        List<KSessionModel> ksessions = firstKBase.getKSessions();

        listKSessions( ksessions );

        view.setSelected( firstKBase.getName(),
                          ksessions.iterator().next().getName() );
    }

    private void listKSessions( List<KSessionModel> ksessions ) {
        List<String> ksessionNames = new ArrayList<String>();
        for ( KSessionModel ksession : ksessions ) {
            ksessionNames.add( ksession.getName() );
        }
        view.setKSessions( ksessionNames );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public HandlerRegistration addSelectionChangeHandler( final SelectionChangeEvent.Handler handler ) {
        return view.addSelectionChangeHandler( handler );
    }

    @Override
    public void fireEvent( final GwtEvent<?> gwtEvent ) {
        view.fireEvent( gwtEvent );
    }
}
