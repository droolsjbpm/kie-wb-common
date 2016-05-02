/*
 * Copyright 2014 JBoss Inc
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
package org.kie.workbench.common.widgets.client.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.model.LazyModelField;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AsyncPackageDataModelOracleImplTest {

    private AsyncPackageDataModelOracle oracle;
    private PackageDataModelOracleIncrementalPayload personPayload;
    private PackageDataModelOracleIncrementalPayload addressPayload;
    private PackageDataModelOracleIncrementalPayload giantPayload;
    private PackageDataModelOracleIncrementalPayload stringPayload;
    private PackageDataModelOracleIncrementalPayload defaultPayload;

    @Before
    public void setUp() throws Exception {
        AsyncPackageDataModelOracleImpl oracle = new AsyncPackageDataModelOracleImpl();
        oracle.service = new Service();

        personPayload = createPersonPayload();
        addressPayload = createAddressPayload();
        giantPayload = createGiantPayload();
        stringPayload = createStringPayload();
        defaultPayload = createDefaultPayload();

        oracle.addGlobals(createGlobals());
        oracle.setPackageName( "org" );
        oracle.addModelFields( createProjectModelFields() );
        oracle.addMethodInformation( createProjectMethodInformation() );
        oracle.addSuperTypes( createSuperTypes() );
        oracle.addPackageNames( createPackageNames() );

        oracle.filter(createImports());

        this.oracle = oracle;
    }

    private Map<String, List<MethodInfo>> createProjectMethodInformation() {
        HashMap<String, List<MethodInfo>> map = new HashMap<String, List<MethodInfo>>();

        map.put( "org.globals.GiantContainerOfInformation", Collections.EMPTY_LIST );

        ArrayList<MethodInfo> stringMethodInfos = new ArrayList<MethodInfo>();
        ArrayList<String> valueOfParams = new ArrayList<String>();
        valueOfParams.add( "Integer" );
        stringMethodInfos.add( new MethodInfo( "valueOf",
                                               valueOfParams,
                                               "java.lang.String",
                                               null,
                                               "String" ) );
        map.put( "java.lang.String",
                 stringMethodInfos );

        ArrayList<MethodInfo> personMethodInfos = new ArrayList<MethodInfo>();
        ArrayList<String> build1Params = new ArrayList<String>();
        build1Params.add( "String" );
        build1Params.add( "Integer" );
        personMethodInfos.add( new MethodInfo( "build",
                                               build1Params,
                                               "org.test.Person",
                                               null,
                                               null ) );
        ArrayList<String> getNameParams = new ArrayList<String>();
        personMethodInfos.add( new MethodInfo( "getName",
                                               getNameParams,
                                               "java.lang.String",
                                               null,
                                               null ) );
        ArrayList<String> build2Params = new ArrayList<String>();
        build2Params.add( "String" );
        personMethodInfos.add( new MethodInfo( "build",
                                               build2Params,
                                               "org.test.Person",
                                               null,
                                               null ) );
        ArrayList<String> build3Params = new ArrayList<String>();
        build3Params.add( "Integer" );
        personMethodInfos.add( new MethodInfo( "build",
                                               build3Params,
                                               "org.test.Person",
                                               null,
                                               null ) );
        map.put( "org.test.Person",
                 personMethodInfos );

        return map;
    }

    private HashMap<String, ModelField[]> createProjectModelFields() {
        HashMap<String, ModelField[]> map = new HashMap<String, ModelField[]>();
        map.put( "org.test.Person",
                 new ModelField[]{ getLazyThisField( "org.test.Person" ) } );
        map.put( "java.lang.String",
                 new ModelField[]{ getLazyThisField( "java.lang.String" ) } );
        map.put( "org.Address",
                 new ModelField[]{ getLazyThisField( "org.Address" ) } );
        map.put( "org.Document",
                 new ModelField[]{ getLazyThisField( "org.Document" ) } );
        map.put( "org.globals.GiantContainerOfInformation",
                 new ModelField[]{ getLazyThisField( "org.globals.GiantContainerOfInformation" ) } );

        return map;
    }

    private Imports createImports() {
        Imports imports = new Imports();
        imports.addImport(new Import("org.test.Person"));
        imports.addImport(new Import("java.lang.String"));
        imports.addImport(new Import("org.globals.GiantContainerOfInformation"));
        return imports;
    }

    private PackageDataModelOracleIncrementalPayload createDefaultPayload() {
        return new PackageDataModelOracleIncrementalPayload();
    }

    private PackageDataModelOracleIncrementalPayload createGiantPayload() {
        PackageDataModelOracleIncrementalPayload payload = new PackageDataModelOracleIncrementalPayload();
        HashMap<String, ModelField[]> addressModelFields = new HashMap<String, ModelField[]>();
        addressModelFields.put("org.globals.GiantContainerOfInformation", new ModelField[]{
                new ModelField("this", "org.globals.GiantContainerOfInformation", ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS, ModelField.FIELD_ORIGIN.SELF, FieldAccessorsAndMutators.ACCESSOR, "this")});
        payload.setModelFields(addressModelFields);

        return payload;
    }

    private PackageDataModelOracleIncrementalPayload createStringPayload() {
        PackageDataModelOracleIncrementalPayload payload = new PackageDataModelOracleIncrementalPayload();
        HashMap<String, ModelField[]> addressModelFields = new HashMap<String, ModelField[]>();
        addressModelFields.put("java.lang.String", new ModelField[]{
                new ModelField("this", "java.lang.String", ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS, ModelField.FIELD_ORIGIN.SELF, FieldAccessorsAndMutators.ACCESSOR, "this")});
        payload.setModelFields(addressModelFields);

        return payload;
    }

    private PackageDataModelOracleIncrementalPayload createAddressPayload() {
        PackageDataModelOracleIncrementalPayload payload = new PackageDataModelOracleIncrementalPayload();
        HashMap<String, ModelField[]> addressModelFields = new HashMap<String, ModelField[]>();
        addressModelFields.put("org.Address", new ModelField[]{
                new ModelField("this", "org.Address", ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS, ModelField.FIELD_ORIGIN.SELF, FieldAccessorsAndMutators.ACCESSOR, "this"),
                new ModelField("street", "String", ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS, ModelField.FIELD_ORIGIN.DECLARED, FieldAccessorsAndMutators.BOTH, DataType.TYPE_STRING),
                new ModelField("homeAddress", "Boolean", ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS, ModelField.FIELD_ORIGIN.DECLARED, FieldAccessorsAndMutators.BOTH, DataType.TYPE_BOOLEAN)});
        payload.setModelFields(addressModelFields);

        return payload;
    }

    private PackageDataModelOracleIncrementalPayload createPersonPayload() {
        PackageDataModelOracleIncrementalPayload payload = new PackageDataModelOracleIncrementalPayload();
        HashMap<String, ModelField[]> personModelFields = new HashMap<String, ModelField[]>();
        personModelFields.put("org.test.Person", new ModelField[]{
                new ModelField("this", "org.test.Person", ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS, ModelField.FIELD_ORIGIN.SELF, FieldAccessorsAndMutators.ACCESSOR, "this"),
                new ModelField("address", "org.Address", ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS, ModelField.FIELD_ORIGIN.DECLARED, FieldAccessorsAndMutators.BOTH, "Address")});

        payload.setModelFields(personModelFields);

        return payload;
    }

    private HashMap<String, String> createGlobals() {
        HashMap<String, String> globals = new HashMap<String, String>();
        globals.put( "giant",
                     "org.globals.GiantContainerOfInformation" );
        globals.put( "person",
                     "org.test.Person" );

        return globals;
    }

    private Map<String, List<String>> createSuperTypes() {
        Map<String, List<String>> superTypes = new HashMap<String, List<String>>();
        superTypes.put( "org.test.Person", Arrays.asList( new String[]{ "org.test.Parent", "org.test.GrandParent" } ) );
        superTypes.put( "org.Address", Arrays.asList( new String[]{ "org.Location" } ) );

        return superTypes;
    }

    private List<String> createPackageNames() {
        List<String> packageNames = new ArrayList<String>();
        packageNames.add( "io.test" );
        packageNames.add( "org.test" );
        packageNames.add( "com.test" );

        return packageNames;
    }

    @Test
    public void testIsFactTypeRecognized() {
        assertTrue( oracle.isFactTypeRecognized( "org.Address" ) );
    }

    @Test
    public void testFactTypes() {
        final String[] types = oracle.getFactTypes();

        assertEquals( 5, types.length );
        assertEquals( "Address", types[ 0 ] );
        assertEquals( "Document", types[ 1 ] );
        assertEquals( "GiantContainerOfInformation", types[ 2 ] );
        assertEquals( "Person", types[ 3 ] );
        assertEquals( "String", types[ 4 ] );
    }

    @Test
    public void testAllFactTypes() {
        final String[] types = oracle.getAllFactTypes();

        assertEquals( 5, types.length );
        assertEquals( "java.lang.String", types[ 0 ] );
        assertEquals( "org.Address", types[ 1 ] );
        assertEquals( "org.Document", types[ 2 ] );
        assertEquals( "org.globals.GiantContainerOfInformation", types[ 3 ] );
        assertEquals( "org.test.Person", types[ 4 ] );
    }

    @Test
    public void testExternalFactTypes() {
        final String[] types = oracle.getExternalFactTypes();

        assertEquals( 3, types.length );
        assertEquals( "java.lang.String", types[ 0 ] );
        assertEquals( "org.globals.GiantContainerOfInformation", types[ 1 ] );
        assertEquals( "org.test.Person", types[ 2 ] );
    }

    @Test
    public void testName() throws Exception {
        assertEquals("org.test.Person", oracle.getFQCNByFactName("Person"));
        assertEquals("Person", oracle.getFieldClassName("Person", "this"));
    }

    @Test
    public void testGetFieldCompletitions() throws Exception {
        Callback<ModelField[]> callback = spy(new Callback<ModelField[]>() {
            @Override
            public void callback(ModelField[] result) {
                assertEquals(2, result.length);

                assertEquals("Address", oracle.getFieldType("Person", "address"));
                assertEquals("Address", oracle.getFieldClassName("Person", "address"));

            }
        });

        oracle.getFieldCompletions("Person", callback);

        verify(callback).callback(any(ModelField[].class));
    }

    @Test
    public void testGetFieldCompletitionsForField() throws Exception {
        Callback<ModelField[]> callback = spy(new Callback<ModelField[]>() {
            @Override
            public void callback(ModelField[] result) {
                assertEquals(3, result.length);

                assertEquals("Address", oracle.getFieldClassName("Address", "this"));
                assertEquals("this", oracle.getFieldType("Address", "this"));
                assertEquals("String", oracle.getFieldClassName("Address", "street"));
                assertEquals("String", oracle.getFieldType("Address", "street"));
                assertEquals("Boolean", oracle.getFieldClassName("Address", "homeAddress"));
                assertEquals("Boolean", oracle.getFieldType("Address", "homeAddress"));
            }
        });

        oracle.getFieldCompletions("Address", callback);

        verify(callback).callback(any(ModelField[].class));

    }

    @Test
    public void testGetFieldCompletitionsForSomethingThatDoesNotReturnFields() throws Exception {
        Callback<ModelField[]> callback = spy(new Callback<ModelField[]>() {
            @Override
            public void callback(ModelField[] result) {
                assertEquals(0, result.length);
            }
        });

        oracle.getFieldCompletions("I.do.not.Exist", callback);

        verify(callback).callback(any(ModelField[].class));

    }

    @Test
    public void testGetFieldCompletitionsMutators() throws Exception {
        Callback<ModelField[]> callback = spy(new Callback<ModelField[]>() {
            @Override
            public void callback(ModelField[] result) {
                assertEquals(2, result.length);
            }
        });

        oracle.getFieldCompletions("Address", FieldAccessorsAndMutators.MUTATOR, callback);

        verify(callback).callback(any(ModelField[].class));

    }

    @Test
    public void testGetMethodInfosForGlobalVariable() throws Exception {

        Callback<List<MethodInfo>> callback = spy(new Callback<List<MethodInfo>>() {
            @Override
            public void callback(List<MethodInfo> result) {
                assertEquals(0, result.size());
            }
        });

        oracle.getMethodInfosForGlobalVariable("giant", callback);

        verify(callback).callback(anyList());
    }

    @Test
    public void testGetMethodInfosSortedForGlobalVariable() {
        Callback<List<MethodInfo>> callback = spy( new Callback<List<MethodInfo>>() {
            @Override
            public void callback( List<MethodInfo> result ) {
                assertEquals( 4, result.size() );

                assertEquals( "build", result.get( 0 ).getName() );
                assertEquals( 1, result.get( 0 ).getParams().size() );
                assertEquals( "Integer", result.get( 0 ).getParams().get( 0 ) );

                assertEquals( "build", result.get( 1 ).getName() );
                assertEquals( 1, result.get( 1 ).getParams().size() );
                assertEquals( "String", result.get( 1 ).getParams().get( 0 ) );

                assertEquals( "build", result.get( 2 ).getName() );
                assertEquals( 2, result.get( 2 ).getParams().size() );
                assertEquals( "String", result.get( 2 ).getParams().get( 0 ) );
                assertEquals( "Integer", result.get( 2 ).getParams().get( 1 ) );

                assertEquals( "getName", result.get( 3 ).getName() );
                assertEquals( 0, result.get( 3 ).getParams().size() );
            }
        } );

        oracle.getMethodInfosForGlobalVariable( "person",
                                                callback );

        verify( callback ).callback( anyList() );
    }

    @Test
    public void testGetFieldCompletionsForGlobalVariable() throws Exception {
        Callback<ModelField[]> callback = spy(new Callback<ModelField[]>() {
            @Override
            public void callback(ModelField[] result) {
                assertEquals(1, result.length);
            }
        });

        oracle.getFieldCompletionsForGlobalVariable("giant", callback);

        verify(callback).callback(any(ModelField[].class));
    }

    @Test
    public void testGetMethodParamsString() throws Exception {
        Callback<List<String>> callback = spy(new Callback<List<String>>() {
            @Override
            public void callback(List<String> result) {
                // In real life this returns 58 values. Using a mock here
                assertEquals(1, result.size());
            }
        });
        oracle.getMethodParams("String", "valueOf(Integer)", callback);
        verify(callback).callback(anyList());
    }

    @Test
    /**
     * Person is imported, but Person has an address that is not. Asking connective operator completions for Address.street should still work.
     */
    public void testGetConnectiveOperatorCompletions() throws Exception {
        final Callback<String[]> connectiveOperatorsCallback = spy(new Callback<String[]>() {
            @Override public void callback(String[] result) {
                assertEquals(OperatorsOracle.STRING_CONNECTIVES.length, result.length);
                for (String connective : OperatorsOracle.STRING_CONNECTIVES) {
                    boolean foundIt = false;
                    for (String resultConnective : result) {
                        if (connective.equals(resultConnective)) {
                            foundIt = true;
                            break;
                        }
                    }
                    assertTrue(foundIt);
                }
            }
        });

        Callback<ModelField[]> fieldCompletionsCallback = spy(new Callback<ModelField[]>() {
            @Override public void callback(ModelField[] result) {

                // We can't really know about the fields of Address before we query them.
                // This is why we call getFieldCompletions() before getConnectiveOperatorCompletions()
                oracle.getConnectiveOperatorCompletions("Address", "street", connectiveOperatorsCallback);

            }
        });

        oracle.getFieldCompletions("Address", fieldCompletionsCallback);

        verify(fieldCompletionsCallback).callback(any(ModelField[].class));
        verify(connectiveOperatorsCallback).callback(any(String[].class));
    }

    @Test
    public void testGetSuperTypes() {

        Callback<List<String>> getSuperTypesCallback = spy( new Callback<List<String>>() {
            @Override
            public void callback( List<String> result ) {

                assertEquals( 2, result.size() );
                assertEquals( "org.test.GrandParent", result.get( 0 ) );
                assertEquals( "org.test.Parent", result.get( 1 ) );
            }
        } );

        oracle.getSuperTypes( "Person", getSuperTypesCallback );
        verify( getSuperTypesCallback ).callback( anyList() );
    }

    @Test
    public void testGetPackageNames() {
        List<String> packageNames = oracle.getPackageNames();

        assertEquals( 3, packageNames.size() );
        assertEquals( "com.test", packageNames.get( 0 ) );
        assertEquals( "io.test", packageNames.get( 1 ) );
        assertEquals( "org.test", packageNames.get( 2 ) );
    }

    private LazyModelField getLazyThisField( String clazz ) {
        return new LazyModelField( "this",
                                   clazz,
                                   ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                   ModelField.FIELD_ORIGIN.SELF,
                                   FieldAccessorsAndMutators.ACCESSOR,
                                   "this" );
    }

    private class Service
            implements Caller<IncrementalDataModelService> {

        private RemoteCallback<PackageDataModelOracleIncrementalPayload> callback;
        private IncrementalDataModelService service = new IncrementalDataModelService() {
            @Override
            public PackageDataModelOracleIncrementalPayload getUpdates(Path resourcePath, Imports imports, String factType) {
                if (factType.equals("org.test.Person")) {
                    callback.callback(personPayload);
                } else if (factType.equals("java.lang.String")) {
                    callback.callback(stringPayload);
                } else if (factType.equals("org.Address")) {
                    callback.callback(addressPayload);
                } else if (factType.equals("org.globals.GiantContainerOfInformation")) {
                    callback.callback(giantPayload);
                } else {
                    callback.callback(defaultPayload);
                }

                return null;
            }
        };

        @Override
        public IncrementalDataModelService call() {
            return service;
        }

        @Override
        public IncrementalDataModelService call(RemoteCallback<?> remoteCallback) {
            callback = (RemoteCallback<PackageDataModelOracleIncrementalPayload>) remoteCallback;
            return service;
        }

        @Override
        public IncrementalDataModelService call(RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback) {
            callback = (RemoteCallback<PackageDataModelOracleIncrementalPayload>) remoteCallback;
            return service;
        }
    }
}
