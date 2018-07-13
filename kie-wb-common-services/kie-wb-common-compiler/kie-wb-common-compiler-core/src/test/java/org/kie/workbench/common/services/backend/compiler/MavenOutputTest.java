/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler;

import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class MavenOutputTest {

    private Path mavenRepo;

    @Before
    public void setUp() throws Exception {
        mavenRepo = TestUtil.createMavenRepo();
    }

    @Test
    public void testOutputWithTakari() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmpNio = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                        "dummy"));
        TestUtil.copyTree(Paths.get(ResourcesConstants.DUMMY_DIR),
                          tmpNio);

        Path tmp = Paths.get(tmpNio.toAbsolutePath().toString());

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE);
        CompilationResponse res = compiler.compile(req);

        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(tmpNio, res.getMavenOutput(),
                                                      "MavenOutputTest.testOutputWithTakari");
        }
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getMavenOutput().size()).isGreaterThan(0);

        TestUtil.rm(tmpRoot.toFile());
    }
}
