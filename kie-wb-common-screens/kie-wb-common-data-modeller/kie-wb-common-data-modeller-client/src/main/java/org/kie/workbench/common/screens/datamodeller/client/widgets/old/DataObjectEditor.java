/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.old;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector.PackageSelector;
import org.kie.workbench.common.screens.datamodeller.client.widgets.refactoring.ShowUsagesPopup;
import org.kie.workbench.common.screens.datamodeller.client.widgets.superselector.SuperclassSelector;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

public class DataObjectEditor extends Composite {

    interface DataObjectDetailEditorUIBinder
            extends UiBinder<Widget, DataObjectEditor> {

    }

    public static final String NOT_SELECTED = "NOT_SELECTED";

    private static final String DEFAULT_LABEL_CLASS = "gwt-Label";

    private static final String TEXT_ERROR_CLASS = "text-error";

    @UiField
    TextBox name;

    @UiField
    Label nameLabel;

    @UiField
    TextBox label;

    @UiField
    TextArea description;

    @UiField
    Label packageNameLabel;

    @UiField
    SimplePanel packageSelectorPanel;

    @Inject
    PackageSelector packageSelector;

    @UiField
    Label superclassLabel;

    @UiField
    SuperclassSelector superclassSelector;

    @UiField
    ListBox roleSelector;

    @UiField
    CheckBox classReactiveSelector;

    @UiField
    CheckBox propertyReactiveSelector;

    @UiField
    Icon roleHelpIcon;

    @UiField
    Icon classReactiveHelpIcon;

    @UiField
    Icon propertyReactiveHelpIcon;

    @UiField
    Label typeSafeLabel;

    @UiField
    Icon typeSafeHelpIcon;

    @UiField
    ListBox typeSafeSelector;

    @UiField
    Label timestampLabel;

    @UiField
    Icon timestampHelpIcon;

    @UiField
    ListBox timestampFieldSelector;

    @UiField
    Label durationLabel;

    @UiField
    ListBox durationFieldSelector;

    @UiField
    Icon durationHelpIcon;

    @UiField
    Label expiresLabel;

    @UiField
    Icon expiresHelpIcon;

    @UiField
    TextBox expires;

    @UiField
    Label remotableLabel;

    @UiField
    Icon remotableHelpIcon;

    @UiField
    CheckBox remotableSelector;

    @UiField
    Label droolsParametersLabel;

    @Inject
    Event<DataModelerEvent> dataModelerEvent;

    DataObject dataObject;

    DataModelerContext context;

    String editorId;

    @Inject
    private Caller<DataModelerService> modelerService;

    @Inject
    private ValidatorService validatorService;

    private boolean readonly = true;

    private static DataObjectDetailEditorUIBinder uiBinder = GWT.create( DataObjectDetailEditorUIBinder.class );

    public DataObjectEditor() {
        initWidget( uiBinder.createAndBindUi( this ) );

        //roleHelpIcon.getElement().getStyle().setPaddingLeft( 4, Style.Unit.PX );
        roleHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        //classReactiveHelpIcon.getElement().getStyle().setPaddingLeft( 4, Style.Unit.PX );
        classReactiveHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        //propertyReactiveHelpIcon.getElement().getStyle().setPaddingLeft( 4, Style.Unit.PX );
        propertyReactiveHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        typeSafeHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        timestampHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        durationHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        expiresHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        remotableHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
    }

    @PostConstruct
    void init() {

        superclassSelector.getSuperclassList().addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                superClassChanged( event );
            }
        } );

        roleSelector.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                roleChanged( event );
            }
        } );
        typeSafeSelector.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                typeSafeChanged( event );
            }
        } );
        timestampFieldSelector.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                timestampChanged( event );
            }
        } );
        durationFieldSelector.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                durationChanged( event );
            }
        } );

        // TODO Change this when necessary (for now hardcoded here)
        roleSelector.addItem( "", NOT_SELECTED );
        roleSelector.addItem( "EVENT", "EVENT" );
        roleSelector.setSelectedValue( NOT_SELECTED );

        typeSafeSelector.addItem( "", NOT_SELECTED );
        typeSafeSelector.addItem( "false", "false" );
        typeSafeSelector.addItem( "true", "true" );

        timestampFieldSelector.addItem( "", NOT_SELECTED );
        durationFieldSelector.addItem( "", NOT_SELECTED );

        packageSelectorPanel.add( packageSelector );
        packageSelector.getPackageList().addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                packageChanged( event );
            }
        } );
        setReadonly( true );
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject( DataObject dataObject ) {
        this.dataObject = dataObject;
    }

    public DataModelerContext getContext() {
        return context;
    }

    private Project getProject() {
        return getContext() != null ? getContext().getCurrentProject() : null;
    }

    public void setContext( DataModelerContext context ) {
        this.context = context;
        packageSelector.setContext( context );
        superclassSelector.setContext( context );
    }

    public void setEditorId( String editorId ) {
        this.editorId = editorId;
        packageSelector.setEditorId( editorId );
        superclassSelector.setEditorId( editorId );
    }

    public String getEditorId() {
        return editorId;
    }

    private DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    public void refreshTypeList( boolean keepSelection ) {
        superclassSelector.refreshList( keepSelection );
    }

    private void setReadonly( boolean readonly ) {
        this.readonly = readonly;
        boolean value = !readonly;

        name.setEnabled( value );
        label.setEnabled( value );
        description.setEnabled( value );
        packageSelector.setEnabled( value );
        superclassSelector.setEnabled( value );
        roleSelector.setEnabled( value );
        propertyReactiveSelector.setEnabled( value );
        classReactiveSelector.setEnabled( value );
        typeSafeSelector.setEnabled( value );
        expires.setEnabled( value );
        durationFieldSelector.setEnabled( value );
        timestampFieldSelector.setEnabled( value );
        remotableSelector.setEnabled( value );
    }

    private boolean isReadonly() {
        return readonly;
    }

    private void loadDataObject( DataObject dataObject ) {
        clean();
        setReadonly( true );
        if ( dataObject != null ) {
            setDataObject( dataObject );

            name.setText( dataObject.getName() );

            Annotation annotation = dataObject.getAnnotation( AnnotationDefinitionTO.LABEL_ANNOTATION );
            if ( annotation != null ) {
                label.setText( annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() );
            }

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION );
            if ( annotation != null ) {
                description.setText( annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() );
            }

            packageSelector.setDataObject( dataObject );

            superclassSelector.setDataObject( dataObject );

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.ROLE_ANNOTATION );
            if ( annotation != null ) {
                String value = annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ) != null ? annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() : NOT_SELECTED;
                roleSelector.setSelectedValue( value );
            }

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.PROPERTY_REACTIVE_ANNOTATION );
            if ( annotation != null ) {
                propertyReactiveSelector.setValue( Boolean.TRUE );
            }

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.CLASS_REACTIVE_ANNOTATION );
            if ( annotation != null ) {
                classReactiveSelector.setValue( Boolean.TRUE );
            }

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.TYPE_SAFE_ANNOTATION );
            if ( annotation != null ) {
                String value = annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ) != null ? annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() : NOT_SELECTED;
                typeSafeSelector.setSelectedValue( value );
            }

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.EXPIRES_ANNOTATION );
            if ( annotation != null ) {
                expires.setText( annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() );
            }

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.REMOTABLE_ANNOTATION );
            if ( annotation != null ) {
                remotableSelector.setValue( Boolean.TRUE );
            }

            loadDuration( dataObject );

            loadTimestamp( dataObject );

            setReadonly( getContext() == null || getContext().isReadonly() );
        }
    }

    private void loadDuration( DataObject dataObject ) {
        Annotation annotation;
        loadDurationSelector( dataObject );
        annotation = dataObject.getAnnotation( AnnotationDefinitionTO.DURATION_ANNOTATION );
        if ( annotation != null ) {
            String value = annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ) != null ? annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() : NOT_SELECTED;
            durationFieldSelector.setSelectedValue( value );
        }
    }

    private void loadTimestamp( DataObject dataObject ) {
        Annotation annotation;
        loadTimestampSelector( dataObject );
        annotation = dataObject.getAnnotation( AnnotationDefinitionTO.TIMESTAMP_ANNOTATION );
        if ( annotation != null ) {
            String value = annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ) != null ? annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() : NOT_SELECTED;
            timestampFieldSelector.setSelectedValue( value );
        }
    }

    // Event observers

    private void onDataObjectSelected( @Observes DataObjectSelectedEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            loadDataObject( event.getCurrentDataObject() );
        }
    }

    private void onDataObjectFieldCreated( @Observes DataObjectFieldCreatedEvent event ) {
        updateFieldDependentSelectors( event, event.getCurrentDataObject(), event.getCurrentField() );
    }

    private void onDataObjectFieldChange( @Observes DataObjectFieldChangeEvent event ) {
        updateFieldDependentSelectors( event, event.getCurrentDataObject(), event.getCurrentField() );
    }

    private void onDataObjectFieldDeleted( @Observes DataObjectFieldDeletedEvent event ) {
        updateFieldDependentSelectors( event, event.getCurrentDataObject(), event.getCurrentField() );
    }

    private void updateFieldDependentSelectors( DataModelerEvent event,
                                                DataObject currentDataObject,
                                                ObjectProperty currentField ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) && getDataObject() == currentDataObject ) {
            loadDuration( getDataObject() );
            loadTimestamp( getDataObject() );
        }
    }

    // Event notifications
    private void notifyObjectChange( String memberName,
                                     Object oldValue,
                                     Object newValue ) {
        DataObjectChangeEvent changeEvent = new DataObjectChangeEvent( getContext().getContextId(), DataModelerEvent.DATA_OBJECT_EDITOR, getDataModel(), getDataObject(), memberName, oldValue, newValue );
        // Notify helper directly
        getContext().getHelper().dataModelChanged( changeEvent );
        dataModelerEvent.fire( changeEvent );
    }

    // Event handlers

    @UiHandler("name")
    void nameChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() == null ) {
            return;
        }

        // Set widgets to errorpopup for styling purposes etc.
        nameLabel.setStyleName( DEFAULT_LABEL_CLASS );

        final String packageName = getDataObject().getPackageName();
        final String oldValue = getDataObject().getName();
        final String newValue = name.getValue();

        final String originalClassName = getContext() != null ? getContext().getEditorModelContent().getOriginalClassName() : null;
        final String fieldName = oldValue;
        final Path currentPath = getContext() != null && getContext().getEditorModelContent() != null ? getContext().getEditorModelContent().getPath() : null;

        if ( originalClassName != null ) {
            modelerService.call( new RemoteCallback<List<Path>>() {

                @Override
                public void callback( List<Path> paths ) {

                    if ( paths != null && paths.size() > 0 ) {
                        //If usages for this field were detected in project assets
                        //show the confirmation message to the user.

                        ShowUsagesPopup showUsagesPopup = ShowUsagesPopup.newUsagesPopupForRenaming(
                                Constants.INSTANCE.modelEditor_confirm_renaming_of_used_class( originalClassName ),
                                paths,
                                new org.uberfire.mvp.Command() {
                                    @Override
                                    public void execute() {
                                        doClassNameChange( packageName, oldValue, newValue );
                                    }
                                },
                                new org.uberfire.mvp.Command() {
                                    @Override
                                    public void execute() {
                                        //do nothing.
                                        name.setValue( oldValue );
                                    }
                                }
                                                                                                   );

                        showUsagesPopup.setCloseVisible( false );
                        showUsagesPopup.show();

                    } else {
                        //no usages, just proceed with the class name change.
                        doClassNameChange( packageName, oldValue, newValue );
                    }
                }
            } ).findClassUsages( currentPath, originalClassName );
        } else {
            doClassNameChange( packageName, oldValue, fieldName );
        }
    }

    private void doClassNameChange( final String packageName,
                                    final String oldValue,
                                    final String newValue ) {

        final Command afterCloseCommand = new Command() {
            @Override
            public void execute() {
                nameLabel.setStyleName( TEXT_ERROR_CLASS );
                name.selectAll();
            }
        };

        // In case an invalid name (entered before), was corrected to the original value, don't do anything but reset the label style
        if ( oldValue.equals( newValue ) ) {
            nameLabel.setStyleName( DEFAULT_LABEL_CLASS );
            return;
        }
        // Otherwise validate
        validatorService.isValidIdentifier( newValue, new ValidatorCallback() {
            @Override
            public void onFailure() {
                ErrorPopup.showMessage( Constants.INSTANCE.validation_error_invalid_object_identifier( newValue ), null, afterCloseCommand );
            }

            @Override
            public void onSuccess() {
                validatorService.isUniqueEntityName( packageName, newValue, getDataModel(), new ValidatorCallback() {
                    @Override
                    public void onFailure() {
                        ErrorPopup.showMessage( Constants.INSTANCE.validation_error_object_already_exists( newValue, packageName ), null, afterCloseCommand );
                    }

                    @Override
                    public void onSuccess() {
                        nameLabel.setStyleName( DEFAULT_LABEL_CLASS );
                        dataObject.setName( newValue );
                        notifyObjectChange( "name", oldValue, newValue );
                    }
                } );
            }
        } );

    }

    @UiHandler("label")
    void labelChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() == null ) {
            return;
        }

        String oldValue = null;
        String _label = label.getValue();
        Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.LABEL_ANNOTATION );

        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _label != null && !"".equals( _label ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _label );
            } else {
                getDataObject().removeAnnotation( annotation.getClassName() );
            }
        } else {
            if ( _label != null && !"".equals( _label ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.LABEL_ANNOTATION ) );
                annotation.setValue(  AnnotationDefinitionTO.VALUE_PARAM, _label );
                getDataObject().addAnnotation( annotation );
            }
        }
        // TODO replace 'label' literal with annotation definition constant
        notifyObjectChange( "label", oldValue, _label );
    }

    @UiHandler("description")
    void descriptionChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() == null ) {
            return;
        }

        String oldValue = null;
        String _description = description.getValue();
        Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION );

        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _description != null && !"".equals( _description ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _description );
            } else {
                getDataObject().removeAnnotation( annotation.getClassName() );
            }
        } else {
            if ( _description != null && !"".equals( _description ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION ) );
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _description );
                getDataObject().addAnnotation( annotation );
            }
        }
        notifyObjectChange( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION, oldValue, _description );
    }

    private void packageChanged( ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        // Set widgets to errorpopup for styling purposes etc.
        packageNameLabel.setStyleName( DEFAULT_LABEL_CLASS );

        final String originalClassName = getContext() != null ? getContext().getEditorModelContent().getOriginalClassName() : null;
        final String newPackageName = packageSelector.isValueSelected() ? packageSelector.getPackageList().getValue() : null;
        final String oldPackageName = getContext().getDataObject().getPackageName();
        final Path currentPath = getContext() != null && getContext().getEditorModelContent() != null ? getContext().getEditorModelContent().getPath() : null;

        if ( ( oldPackageName != null && !oldPackageName.equals( newPackageName ) ) ||
                ( oldPackageName == null && newPackageName != null ) ) {
            //the user is trying to change the package name

            modelerService.call( new RemoteCallback<List<Path>>() {

                @Override
                public void callback( List<Path> paths ) {

                    if ( paths != null && paths.size() > 0 ) {
                        //If usages for this class were detected in project assets
                        //show the confirmation message to the user.

                        ShowUsagesPopup showUsagesPopup = ShowUsagesPopup.newUsagesPopupForChanging(
                                Constants.INSTANCE.modelEditor_confirm_package_change_of_used_class( originalClassName ),
                                paths,
                                new org.uberfire.mvp.Command() {
                                    @Override
                                    public void execute() {
                                        doPackageChange( oldPackageName, newPackageName );
                                    }
                                },
                                new org.uberfire.mvp.Command() {
                                    @Override
                                    public void execute() {
                                        //do nothing.
                                        packageSelector.getPackageList().setSelectedValue( oldPackageName );
                                    }
                                }
                                                                                                   );

                        showUsagesPopup.setCloseVisible( false );
                        showUsagesPopup.show();

                    } else {
                        //no usages, just proceed with the package change.
                        doPackageChange( oldPackageName, newPackageName );
                    }
                }
            } ).findClassUsages( currentPath, originalClassName );
        } else {
            doPackageChange( oldPackageName, newPackageName );
        }
    }

    private void doPackageChange( String oldPackageName,
                                  String newPackageName ) {
        getDataObject().setPackageName( newPackageName );
        notifyObjectChange( "packageName", oldPackageName, newPackageName );
    }

    private void superClassChanged( ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        // Set widgets to errorpopup for styling purposes etc.
        superclassLabel.setStyleName( DEFAULT_LABEL_CLASS );

        final String newSuperClass = superclassSelector.getSuperclassList().getValue();
        final String oldSuperClass = getDataObject().getSuperClassName();

        // No notification needed
        if ( ( ( "".equals( newSuperClass ) || SuperclassSelector.NOT_SELECTED.equals( newSuperClass ) ) && oldSuperClass == null ) ||
                newSuperClass.equals( oldSuperClass ) ) {
            superclassLabel.setStyleName( DEFAULT_LABEL_CLASS );
            return;
        }

        if ( newSuperClass != null && !"".equals( newSuperClass ) && !SuperclassSelector.NOT_SELECTED.equals( newSuperClass ) ) {
            validatorService.canExtend( getContext(), getDataObject().getClassName(), newSuperClass, new ValidatorCallback() {
                @Override
                public void onFailure() {
                    ErrorPopup.showMessage( Constants.INSTANCE.validation_error_cyclic_extension( getDataObject().getClassName(), newSuperClass ), null, new Command() {
                        @Override
                        public void execute() {
                            superclassLabel.setStyleName( TEXT_ERROR_CLASS );
                            superclassSelector.getSuperclassList().setFocus( true );
                        }
                    } );
                }

                @Override
                public void onSuccess() {
                    getDataObject().setSuperClassName( newSuperClass );

                    // Remove former extension refs if superclass has changed
                    if ( oldSuperClass != null && !"".equals( oldSuperClass ) ) {
                        getContext().getHelper().dataObjectExtended( oldSuperClass, getDataObject().getClassName(), false );
                    }
                    getContext().getHelper().dataObjectExtended( newSuperClass, getDataObject().getClassName(), true );
                    notifyObjectChange( "superClassName", oldSuperClass, newSuperClass );
                }
            } );
        } else {
            getDataObject().setSuperClassName( null );
            getContext().getHelper().dataObjectExtended( oldSuperClass, getDataObject().getClassName(), false );
            notifyObjectChange( "superClassName", oldSuperClass, newSuperClass );
        }
    }

    void roleChanged( final ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        final String _role = roleSelector.getValue();
        Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.ROLE_ANNOTATION );

        String oldValue = null;
        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _role != null && !NOT_SELECTED.equals( _role ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _role );
            } else {
                getDataObject().removeAnnotation( annotation.getClassName() );
            }
        } else {
            if ( _role != null && !NOT_SELECTED.equals( _role ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.ROLE_ANNOTATION ) );
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _role );
                getDataObject().addAnnotation( annotation );
            }
        }
        notifyObjectChange( AnnotationDefinitionTO.ROLE_ANNOTATION, oldValue, _role );
    }

    void typeSafeChanged( final ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        final String _typeSaveValue = typeSafeSelector.getValue();
        Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.TYPE_SAFE_ANNOTATION );

        String oldValue = null;
        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _typeSaveValue != null && !NOT_SELECTED.equals( _typeSaveValue ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _typeSaveValue );
            } else {
                getDataObject().removeAnnotation( annotation.getClassName() );
            }
        } else {
            if ( _typeSaveValue != null && !NOT_SELECTED.equals( _typeSaveValue ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.TYPE_SAFE_ANNOTATION ) );
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _typeSaveValue );
                getDataObject().addAnnotation( annotation );
            }
        }
        notifyObjectChange( AnnotationDefinitionTO.TYPE_SAFE_ANNOTATION, oldValue, _typeSaveValue );
    }

    void timestampChanged( final ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        final String _timestampValue = timestampFieldSelector.getValue();
        Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.TIMESTAMP_ANNOTATION );

        String oldValue = null;
        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _timestampValue != null && !NOT_SELECTED.equals( _timestampValue ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _timestampValue );
            } else {
                getDataObject().removeAnnotation( annotation.getClassName() );
            }
        } else {
            if ( _timestampValue != null && !NOT_SELECTED.equals( _timestampValue ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.TIMESTAMP_ANNOTATION ) );
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _timestampValue );
                getDataObject().addAnnotation( annotation );
            }
        }
        notifyObjectChange( AnnotationDefinitionTO.TIMESTAMP_ANNOTATION, oldValue, _timestampValue );
    }

    void durationChanged( final ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        final String _durationValue = durationFieldSelector.getValue();
        Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.DURATION_ANNOTATION );

        String oldValue = null;
        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _durationValue != null && !NOT_SELECTED.equals( _durationValue ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _durationValue );
            } else {
                getDataObject().removeAnnotation( annotation.getClassName() );
            }
        } else {
            if ( _durationValue != null && !NOT_SELECTED.equals( _durationValue ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.DURATION_ANNOTATION ) );
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _durationValue );
                getDataObject().addAnnotation( annotation );
            }
        }
        notifyObjectChange( AnnotationDefinitionTO.DURATION_ANNOTATION, oldValue, _durationValue );
    }

    @UiHandler("propertyReactiveSelector")
    void propertyReactiveChanged( final ClickEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        Boolean oldValue = null;
        Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.PROPERTY_REACTIVE_ANNOTATION );
        oldValue = annotation != null;

        final Boolean isChecked = propertyReactiveSelector.getValue();

        if ( annotation != null && !isChecked ) {
            getDataObject().removeAnnotation( annotation.getClassName() );
        } else if ( annotation == null && isChecked ) {
            annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.PROPERTY_REACTIVE_ANNOTATION ) );
            getDataObject().addAnnotation( annotation );
        }

        if ( isChecked ) {
            getDataObject().removeAnnotation( AnnotationDefinitionTO.CLASS_REACTIVE_ANNOTATION ) ;
            classReactiveSelector.setValue( false );
        }
        //TODO check if this event is needed and add validation, this annotation cannot coexist with the ClassReactiveAnnotation
        notifyObjectChange( AnnotationDefinitionTO.PROPERTY_REACTIVE_ANNOTATION, oldValue, isChecked );
    }

    @UiHandler("classReactiveSelector")
    void classReactiveChanged( final ClickEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        Boolean oldValue = null;
        Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.CLASS_REACTIVE_ANNOTATION );
        oldValue = annotation != null;

        final Boolean isChecked = classReactiveSelector.getValue();

        if ( annotation != null && !isChecked ) {
            getDataObject().removeAnnotation( annotation.getClassName() );
        } else if ( annotation == null && isChecked ) {
            annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.CLASS_REACTIVE_ANNOTATION ) );
            getDataObject().addAnnotation( annotation );
        }

        if ( isChecked ) {
            getDataObject().removeAnnotation( AnnotationDefinitionTO.PROPERTY_REACTIVE_ANNOTATION );
            propertyReactiveSelector.setValue( false );
        }
        //TODO check if this event is needed and add validation, this annotation cannot coexist with the PropertyReactiveAnnotation
        notifyObjectChange( AnnotationDefinitionTO.CLASS_REACTIVE_ANNOTATION, oldValue, isChecked );
    }

    @UiHandler("expires")
    void expiresChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() == null ) {
            return;
        }

        // Set widgets to error popup for styling purposes etc.
        expiresLabel.setStyleName( DEFAULT_LABEL_CLASS );
        final Command afterCloseCommand = new Command() {
            @Override
            public void execute() {
                expiresLabel.setStyleName( TEXT_ERROR_CLASS );
                expires.selectAll();
            }
        };

        final Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.EXPIRES_ANNOTATION );
        final String oldValue = annotation != null ? AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM ) : null;
        final String newValue = expires.getText();

        // In case an invalid expression (entered before), was corrected to the original value, don't do anything but reset the label style
        if ( oldValue != null && oldValue.equals( newValue ) ) {
            nameLabel.setStyleName( DEFAULT_LABEL_CLASS );
            return;
        }

        // Otherwise validate
        validatorService.isValidTimerInterval( newValue, new ValidatorCallback() {
            @Override
            public void onFailure() {
                ErrorPopup.showMessage( Constants.INSTANCE.validation_error_invalid_timer_expression( newValue ), null, afterCloseCommand );
            }

            @Override
            public void onSuccess() {
                if ( annotation != null ) {
                    getDataObject().removeAnnotation( annotation.getClassName() );
                }
                if ( newValue != null && !"".equals( newValue ) ) {
                    Annotation annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.EXPIRES_ANNOTATION ) );
                    annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, newValue );
                    getDataObject().addAnnotation( annotation );
                }

                notifyObjectChange( AnnotationDefinitionTO.EXPIRES_ANNOTATION, oldValue, newValue );
            }
        } );
    }

    @UiHandler("remotableSelector")
    void remotableChanged( final ClickEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        Boolean oldValue = null;
        Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.REMOTABLE_ANNOTATION );
        oldValue = annotation != null;

        final Boolean isChecked = remotableSelector.getValue();

        if ( annotation != null && !isChecked ) {
            getDataObject().removeAnnotation( annotation.getClassName() );
        } else if ( annotation == null && isChecked ) {
            annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.REMOTABLE_ANNOTATION )  );
            getDataObject().addAnnotation( annotation );
        }

        notifyObjectChange( AnnotationDefinitionTO.REMOTABLE_ANNOTATION, oldValue, isChecked );
    }

    private void loadDurationSelector( DataObject dataObject ) {
        if ( dataObject == null ) {
            return;
        }

        List<String> types = new ArrayList<String>();
        types.add( "short" );
        types.add( "int" );
        types.add( "long" );
        types.add( "java.lang.Short" );
        types.add( "java.lang.Integer" );
        types.add( "java.lang.Long" );

        String defaultValue = null;
        Annotation annotation = dataObject.getAnnotation( AnnotationDefinitionTO.DURATION_ANNOTATION );
        if ( annotation != null ) {
            defaultValue = AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
        }

        loadPropertySelector( durationFieldSelector, dataObject, types, defaultValue );
    }

    private void loadTimestampSelector( DataObject dataObject ) {
        if ( dataObject == null ) {
            return;
        }

        List<String> types = new ArrayList<String>();
        types.add( "long" );
        types.add( "java.lang.Long" );
        types.add( "java.util.Date" );
        types.add( "java.sql.Timestamp" );

        String defaultValue = null;
        Annotation annotation = dataObject.getAnnotation( AnnotationDefinitionTO.TIMESTAMP_ANNOTATION );
        if ( annotation != null ) {
            defaultValue = AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
        }

        loadPropertySelector( timestampFieldSelector, dataObject, types, defaultValue );
    }

    private void loadPropertySelector( ListBox selector,
                                       DataObject dataObject,
                                       List<String> types,
                                       String defaultValue ) {
        if ( dataObject == null ) {
            return;
        }

        List<ObjectProperty> properties = DataModelerUtils.filterPropertiesByType( dataObject.getProperties(), types, true );
        SortedMap<String, String> propertyNames = new TreeMap<String, String>();
        for ( ObjectProperty property : properties ) {
            propertyNames.put( property.getName(), property.getName() );
        }

        if ( defaultValue != null && !"".equals( defaultValue ) ) {
            propertyNames.put( defaultValue, defaultValue );
        }

        selector.clear();
        selector.addItem( "", NOT_SELECTED );
        for ( Map.Entry<String, String> propertyName : propertyNames.entrySet() ) {
            selector.addItem( propertyName.getKey(), propertyName.getValue() );
        }
        selector.setSelectedValue( NOT_SELECTED );
    }

    private void clean() {
        nameLabel.setStyleName( DEFAULT_LABEL_CLASS );
        name.setText( null );
        label.setText( null );
        description.setText( null );
        //packageNameLabel.setStyleName(DEFAULT_LABEL_CLASS);
        packageSelector.setDataObject( null );
        // TODO superclassLabel when its validation is put in place
        superclassSelector.setDataObject( null );
        roleSelector.setSelectedValue( NOT_SELECTED );
        classReactiveSelector.setValue( false );
        propertyReactiveSelector.setValue( false );
        typeSafeSelector.setSelectedValue( NOT_SELECTED );
        expires.setText( null );
        durationFieldSelector.setSelectedValue( NOT_SELECTED );
        timestampFieldSelector.setSelectedValue( NOT_SELECTED );
        remotableSelector.setValue( false );
    }
}