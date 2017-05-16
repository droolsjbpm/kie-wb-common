/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.refactoring.backend.server.query.findresources;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllLibraryAssetsQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FindAllLibraryAssetsSortedQueryTest
        extends BaseIndexingTest<TestDrlFileTypeDefinition> {

    private static final String SOME_OTHER_PROJECT_ROOT = "some/other/projectRoot";
    private static final String SOME_OTHER_PROJECT_NAME = "other-mock-project";

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add(new FindAllLibraryAssetsQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder(ioService());
                }
            });
        }};
    }

    @Override
    protected KieProjectService getProjectService() {

        final KieProjectService mock = super.getProjectService();

        when(mock.resolveProject(any(Path.class)))
                .thenAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        Path resource = (Path) invocationOnMock.getArguments()[0];
                        if (resource.toURI().contains(TEST_PROJECT_ROOT)) {
                            return getKieProjectMock(TEST_PROJECT_ROOT,
                                                     TEST_PROJECT_NAME);
                        } else if (resource.toURI().contains(SOME_OTHER_PROJECT_ROOT)) {
                            return getKieProjectMock(SOME_OTHER_PROJECT_ROOT,
                                                     SOME_OTHER_PROJECT_NAME);
                        } else {
                            return null;
                        }
                    }
                });

        return mock;
    }

    @Test
    public void listAllInProjectSorted() throws IOException, InterruptedException {

        //Add test files
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "rule3.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "functions.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl3.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl2.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl1.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "RULE4.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "DRL4.drl");

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new ValueProjectRootPathIndexTerm(BaseIndexingTest.TEST_PROJECT_ROOT,
                                                                                                                        TermSearchType.WILDCARD));
                                                                              }},
                                                                              0,
                                                                              10);

            try {
                final PageResponse<RefactoringPageRow> response = service.query(request);
                assertNotNull(response);

                for (RefactoringPageRow refactoringPageRow : response.getPageRowList()) {
                    System.out.println(((Path) refactoringPageRow.getValue()).getFileName());
                }

                assertEquals(7,
                             response.getPageRowList().size());
                assertEquals("drl1.drl",
                             ((Path) response.getPageRowList().get(0).getValue()).getFileName());
                assertEquals("drl2.drl",
                             ((Path) response.getPageRowList().get(1).getValue()).getFileName());
                assertEquals("drl3.drl",
                             ((Path) response.getPageRowList().get(2).getValue()).getFileName());
                assertEquals("DRL4.drl",
                             ((Path) response.getPageRowList().get(3).getValue()).getFileName());
                assertEquals("functions.drl",
                             ((Path) response.getPageRowList().get(4).getValue()).getFileName());
                assertEquals("rule3.drl",
                             ((Path) response.getPageRowList().get(5).getValue()).getFileName());
                assertEquals("RULE4.drl",
                             ((Path) response.getPageRowList().get(6).getValue()).getFileName());
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    @Test
    public void listAllInProjectSortedPaged() throws IOException, InterruptedException {

        //Add test files
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "rule3.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "functions.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl3.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl2.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl1.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "RULE4.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "DRL4.drl");

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request1 = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add(new ValueProjectRootPathIndexTerm(BaseIndexingTest.TEST_PROJECT_ROOT,
                                                                                                                         TermSearchType.WILDCARD));
                                                                               }},
                                                                               0,
                                                                               4);
            final RefactoringPageRequest request2 = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add(new ValueProjectRootPathIndexTerm(BaseIndexingTest.TEST_PROJECT_ROOT,
                                                                                                                         TermSearchType.WILDCARD));
                                                                               }},
                                                                               4,
                                                                               4);

            try {
                final PageResponse<RefactoringPageRow> response1 = service.query(request1);
                assertNotNull(response1);

                for (RefactoringPageRow refactoringPageRow : response1.getPageRowList()) {
                    System.out.println(((Path) refactoringPageRow.getValue()).getFileName());
                }

                assertEquals(4,
                             response1.getPageRowList().size());
                assertEquals("drl1.drl",
                             ((Path) response1.getPageRowList().get(0).getValue()).getFileName());
                assertEquals("drl2.drl",
                             ((Path) response1.getPageRowList().get(1).getValue()).getFileName());
                assertEquals("drl3.drl",
                             ((Path) response1.getPageRowList().get(2).getValue()).getFileName());
                assertEquals("DRL4.drl",
                             ((Path) response1.getPageRowList().get(3).getValue()).getFileName());

                final PageResponse<RefactoringPageRow> response2 = service.query(request2);
                assertNotNull(response2);

                for (RefactoringPageRow refactoringPageRow : response2.getPageRowList()) {
                    System.out.println(((Path) refactoringPageRow.getValue()).getFileName());
                }

                assertEquals(3,
                             response2.getPageRowList().size());
                assertEquals("functions.drl",
                             ((Path) response2.getPageRowList().get(0).getValue()).getFileName());
                assertEquals("rule3.drl",
                             ((Path) response2.getPageRowList().get(1).getValue()).getFileName());
                assertEquals("RULE4.drl",
                             ((Path) response2.getPageRowList().get(2).getValue()).getFileName());
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestDrlFileIndexer();
    }

    @Override
    protected TestDrlFileTypeDefinition getResourceTypeDefinition() {
        return new TestDrlFileTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }
}
