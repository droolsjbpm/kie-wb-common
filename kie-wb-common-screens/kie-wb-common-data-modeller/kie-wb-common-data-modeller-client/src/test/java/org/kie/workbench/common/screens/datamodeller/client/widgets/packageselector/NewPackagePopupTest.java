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

package org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector;

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class NewPackagePopupTest {

    protected ValidatorService validatorService;

    @Mock
    protected ValidationService validationService;

    protected CallerMock<ValidationService> validationServiceCallerMock;

    @Mock
    private NewPackagePopupView view;

    @Before
    public void initTest() {
        validationServiceCallerMock = new CallerMock<ValidationService>( validationService );
        validatorService = new ValidatorService( validationServiceCallerMock );
    }

    @Test
    public void showAndCreateValidPackageTest() {

        NewPackagePopup newPackagePopup = new NewPackagePopup( view, validatorService );

        final boolean commandExecuted[] = {false};

        newPackagePopup.show( new Command() {
            @Override public void execute() {
                commandExecuted[ 0 ] = true;
            }
        } );


        Map<String, Boolean> validationResult = new HashMap<String, Boolean>();
        validationResult.put( "somePackageName", true );
        when( validationService.evaluateJavaIdentifiers( any( String[].class ) ) ).thenReturn( validationResult );

        when( view.getPackageName() ).thenReturn( "somePackageName" );

        newPackagePopup.onCreatePackage();

        //the command is executed only when a valid package name was provided.
        verify( view, times( 1 ) ).hide();
        assertEquals( true, commandExecuted[0] );
    }
}
