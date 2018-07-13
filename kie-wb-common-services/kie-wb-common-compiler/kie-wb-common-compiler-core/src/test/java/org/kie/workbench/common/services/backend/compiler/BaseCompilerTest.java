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
import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class BaseCompilerTest {

    protected static Path tmpRoot;
    protected Path mavenRepo;
    protected Logger logger = LoggerFactory.getLogger(BaseCompilerTest.class);
    protected String alternateSettingsAbsPath;
    protected WorkspaceCompilationInfo info;
    protected AFCompiler compiler;
    protected KieCompilationResponse res;

    public BaseCompilerTest(String prjName) {
        try {
            mavenRepo = TestUtil.createMavenRepo();
            tmpRoot = Files.createTempDirectory("repo");
            alternateSettingsAbsPath = new File("src/test/settings.xml").getAbsolutePath();
            Path tmp = TestUtil.createAndCopyToDircetory(tmpRoot, "dummy", prjName);
            info = new WorkspaceCompilationInfo(Paths.get(tmp.toUri()));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public BaseCompilerTest(String prjName, KieDecorator decorator) {
        this(prjName);
        try {
            compiler = KieMavenCompilerFactory.getCompiler(decorator);
            CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                   info,
                                                                   new String[]{MavenCLIArgs.INSTALL, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                                   Boolean.FALSE);
            res = (KieCompilationResponse) compiler.compile(req);
            if (!res.isSuccessful()) {
                TestUtil.writeMavenOutputIntoTargetFolder(tmpRoot, res.getMavenOutput(),
                                                          "KieMetadataTest.compileAndloadKieJarSingleMetadataWithPackagedJar");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    protected WorkspaceCompilationInfo createdNewPrjInRepo(String dirName, String prjName) throws IOException {
        Path tmp = TestUtil.createAndCopyToDircetory(tmpRoot, dirName, prjName);
        return new WorkspaceCompilationInfo(Paths.get(tmp.toUri()));
    }

    @AfterClass
    public static void tearDown() {
        if(tmpRoot!= null) {
            TestUtil.rm(tmpRoot.toFile());
        }
    }
}
