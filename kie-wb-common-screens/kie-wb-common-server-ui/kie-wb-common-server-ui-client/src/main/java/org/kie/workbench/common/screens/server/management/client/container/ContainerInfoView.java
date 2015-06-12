/*
 * Copyright 2013 JBoss Inc
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
package org.kie.workbench.common.screens.server.management.client.container;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpBlock;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.GAV;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.util.NumericTextBox;
import org.kie.workbench.common.screens.server.management.client.util.ReadOnlyTextBox;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.*;
import static org.kie.workbench.common.screens.server.management.client.util.ContainerStatusUtil.*;

@Dependent
public class ContainerInfoView
        extends Composite
        implements ContainerInfoPresenter.View {

    interface Binder
            extends
            UiBinder<Widget, ContainerInfoView> {

    }

    @UiField
    Element status;

    @UiField
    ControlGroup intervalGroup;

    @UiField
    NumericTextBox interval;

    @UiField
    ControlGroup groupIdGroup;

    @UiField
    ReadOnlyTextBox groupId;

    @UiField
    ControlGroup artifactIdGroup;

    @UiField
    ReadOnlyTextBox artifactId;

    @UiField
    ControlGroup versionGroup;

    @UiField
    TextBox version;

    @UiField
    ControlGroup resolvedGroupIdGroup;

    @UiField
    ReadOnlyTextBox resolvedGroupId;

    @UiField
    ControlGroup resolvedArtifactIdGroup;

    @UiField
    ReadOnlyTextBox resolvedArtifactId;

    @UiField
    ControlGroup resolvedVersionGroup;

    @UiField
    ReadOnlyTextBox resolvedVersion;

    @UiField
    HelpBlock endpoint;

    @UiField
    Button startScanner;

    @UiField
    Button stopScanner;

    @UiField
    Button scanNow;

    @UiField
    Button upgrade;

    private org.uberfire.mvp.Command stopScannerActive = new org.uberfire.mvp.Command() {
        @Override
        public void execute() {
            Scheduler.get().scheduleDeferred( new Command() {
                @Override
                public void execute() {
                    stopScanner.setActive( true );
                    startScanner.setActive( false );
                }
            } );
        }
    };

    private org.uberfire.mvp.Command startScannerActive = new org.uberfire.mvp.Command() {
        @Override
        public void execute() {
            Scheduler.get().scheduleDeferred( new Command() {
                @Override
                public void execute() {
                    startScanner.setActive( true );
                    stopScanner.setActive( false );
                }
            } );
        }
    };

    private ContainerInfoPresenter presenter;

    private static Binder uiBinder = GWT.create( Binder.class );

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );

        version.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( !version.getText().trim().isEmpty() ) {
                    versionGroup.setType( ControlGroupType.NONE );
                }
            }
        } );

        interval.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( !interval.getText().trim().isEmpty() ) {
                    intervalGroup.setType( ControlGroupType.NONE );
                }
            }
        } );
    }

    @Override
    public void init( ContainerInfoPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setStatus( final ContainerStatus status ) {
        setupStatus( this.status, status );
    }

    @Override
    public void setInterval( final String pollInterval ) {
        this.intervalGroup.setType( ControlGroupType.NONE );
        this.interval.setText( pollInterval );
    }

    @Override
    public void setGroupId( final String groupId ) {
        this.groupId.setText( groupId );
    }

    @Override
    public void setArtifactId( final String artifactId ) {
        this.artifactId.setText( artifactId );
    }

    @Override
    public void setVersion( final String version ) {
        this.version.setText( version );
    }

    @Override
    public void setResolvedGroupId( final String resolvedGroupId ) {
        this.resolvedGroupId.setText( resolvedGroupId );
    }

    @Override
    public void setResolvedArtifactId( final String resolvedArtifactId ) {
        this.resolvedArtifactId.setText( resolvedArtifactId );
    }

    @Override
    public void setResolvedVersion( final String resolvedVersion ) {
        this.resolvedVersion.setText( resolvedVersion );
    }

    @Override
    public void setEndpoint( final String endpoint ) {
        this.endpoint.setText( endpoint );
    }

    @Override
    public void setStartScannerState( final ContainerInfoPresenter.State state ) {
        this.startScanner.setEnabled( state.equals( ContainerInfoPresenter.State.ENABLED ) );
    }

    @Override
    public void setStopScannerState( final ContainerInfoPresenter.State state ) {
        this.stopScanner.setEnabled( state.equals( ContainerInfoPresenter.State.ENABLED ) );
    }

    @Override
    public void setScanNowState( final ContainerInfoPresenter.State state ) {
        this.scanNow.setEnabled( state.equals( ContainerInfoPresenter.State.ENABLED ) );
    }

    @Override
    public void setUpgradeState( final ContainerInfoPresenter.State state ) {
        this.upgrade.setEnabled( state.equals( ContainerInfoPresenter.State.ENABLED ) );
    }

    @Override
    public IsWidget getCustomMenuItem( final org.uberfire.mvp.Command onClick ) {
        return new Button() {
            {
                setIcon( IconType.REMOVE );
                setTitle( Constants.INSTANCE.remove() );
                setSize( MINI );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onClick.execute();
                    }
                } );
            }
        };
    }

    @Override
    public void cleanup() {
        intervalGroup.setType( ControlGroupType.NONE );
        interval.setText( "" );
        startScanner.setEnabled( false );
        stopScanner.setEnabled( false );
        scanNow.setEnabled( false );
        groupId.setText( "" );
        artifactId.setText( "" );
        version.setText( "" );
        resolvedGroupId.setText( "" );
        resolvedArtifactId.setText( "" );
        resolvedVersion.setText( "" );
        endpoint.setText( "" );
        groupIdGroup.setType( ControlGroupType.NONE );
        artifactIdGroup.setType( ControlGroupType.NONE );
        versionGroup.setType( ControlGroupType.NONE );
    }

    @UiHandler("startScanner")
    public void startScanner( final ClickEvent e ) {
        if ( startScanner.isActive() ) {
            return;
        }

        try {
            presenter.startScanner( interval.getText() );
        } catch ( final IllegalArgumentException ex ) {
            intervalGroup.setType( ControlGroupType.ERROR );
            stopScannerActive.execute();
        }
    }

    @UiHandler("stopScanner")
    public void stopScanner( final ClickEvent e ) {
        if ( stopScanner.isActive() ) {
            return;
        }
        stopScannerActive.execute();
        presenter.stopScanner();
    }

    @UiHandler("scanNow")
    public void scanNow( final ClickEvent e ) {
        stopScannerActive.execute();
        presenter.scanNow();
    }

    @UiHandler("upgrade")
    public void upgrade( final ClickEvent e ) {
        try {
            presenter.upgrade( new GAV( groupId.getText(), artifactId.getText(), version.getText() ) );
        } catch ( final IllegalArgumentException ex ) {
            versionGroup.setType( ControlGroupType.ERROR );
        }
    }
}
