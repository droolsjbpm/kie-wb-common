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

package org.kie.workbench.common.services.backend.builder;

import java.util.ArrayList;
import java.util.Set;

import org.junit.Test;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.io.IOService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test splitting text into lines originating from different platforms.
 * <p/>
 * A line is considered to be terminated by any one of a line feed ('\n'),
 * a carriage return ('\r'), or a carriage return followed immediately by
 * a linefeed.
 * <p/>
 * See See https://en.wikipedia.org/wiki/Newline#Representations
 **/
public class PackageNameWhiteListTest {

    @Test
    public void testWindowsEncoding() {
        final PackageNameWhiteList packageNameWhiteList = new MockPackageNameWhiteList( "a.**\r\nb\r\n" );
        final Set<String> results = packageNameWhiteList.filterPackageNames( mock( KieProject.class ),
                                                                             new ArrayList<String>() {{
                                                                                 add( "a" );
                                                                                 add( "b" );
                                                                                 add( "a.b" );
                                                                             }} );
        assertEquals( 3,
                      results.size() );
        assertContains( "a",
                        results );
        assertContains( "b",
                        results );
        assertContains( "a.b",
                        results );
    }

    @Test
    public void testUnixEncoding() {
        final PackageNameWhiteList packageNameWhiteList = new MockPackageNameWhiteList( "a.**\nb\n" );
        final Set<String> results = packageNameWhiteList.filterPackageNames( mock( KieProject.class ),
                                                                             new ArrayList<String>() {{
                                                                                 add( "a" );
                                                                                 add( "b" );
                                                                                 add( "a.b" );
                                                                             }} );
        assertEquals( 3,
                      results.size() );
        assertContains( "a",
                        results );
        assertContains( "b",
                        results );
        assertContains( "a.b",
                        results );
    }

    private void assertContains( final String expected,
                                 final Set<String> actual ) {
        for ( String a : actual ) {
            if ( expected.equals( a ) ) {
                return;
            }
        }
        fail( "Expected pattern '" + expected + "' was not found in actual." );
    }

    private static class MockPackageNameWhiteList extends PackageNameWhiteList {

        private String content;

        public MockPackageNameWhiteList( final String content ) {
            super( mock( IOService.class ) );
            this.content = content;
        }

        @Override
        protected String readPackageNameWhiteList( final KieProject project ) {
            return content;
        }

    }

}