/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.kie;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class KieDefaultMavenIncrementalCompilerTest {

    private Path mavenRepo;
    private Path tmpRoot;
    private Path temp;
    private Logger logger = LoggerFactory.getLogger(KieDefaultMavenIncrementalCompilerTest.class);

    @Before
    public void setUp() throws Exception {
        mavenRepo = TestUtil.createMavenRepo();
    }

    @After
    public void cleanUp() {
        if (tmpRoot != null) {
            TestUtil.rm(tmpRoot.toFile());
        }
    }

    private CompilationResponse compileProjectInRepo(String... mavenPhases) throws IOException {
        tmpRoot = Files.createTempDirectory("repo");
        temp = TestUtil.createAndCopyToDircetory(tmpRoot, "dummy", ResourcesConstants.DUMMY_DIR);

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.NONE);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               mavenPhases,
                                                               Boolean.FALSE);
        return compiler.compile(req);
    }

    @Test
    public void testIsValidMavenHome() throws Exception {
        CompilationResponse res = compileProjectInRepo(MavenCLIArgs.VERSION);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(temp, res.getMavenOutput(),
                                                      "KieDefaultMavenIncrementalCompilerTest.testIsValidMavenHome");
        }
        assertThat(res.isSuccessful()).isTrue();
    }

    @Test
    public void testIncrementalWithPluginEnabled() throws Exception {
        CompilationResponse res = compileProjectInRepo(MavenCLIArgs.COMPILE);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(temp, res.getMavenOutput(),
                                                      "KieDefaultMavenIncrementalCompilerTest.testIncrementalWithPluginEnabled");
        }
        assertThat(res.isSuccessful()).isTrue();

        Path incrementalConfiguration = Paths.get(temp.toAbsolutePath().toString(),
                                                  "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        assertThat(incrementalConfiguration.toFile().exists()).isTrue();
    }

    @Test
    public void testIncrementalWithPluginEnabledThreeTime() throws Exception {
        tmpRoot = Files.createTempDirectory("repo");
        //NIO creation and copy content
        Path temp = TestUtil.createAndCopyToDircetory(tmpRoot,"dummy",ResourcesConstants.DUMMY_DIR);
        //end NIO

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.NONE);

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.FALSE);
        CompilationResponse res = compiler.compile(req);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(temp, res.getMavenOutput(),
                                                      "KieDefaultMavenIncrementalCompilerTest.testIncrementalWithPluginEnabledThreeTime");
        }
        assertThat(res.isSuccessful()).isTrue();

        res = compiler.compile(req);
        assertThat(res.isSuccessful()).isTrue();

        res = compiler.compile(req);
        assertThat(res.isSuccessful()).isTrue();

        Path incrementalConfiguration = Paths.get(temp.toAbsolutePath().toString(),
                                                  "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        assertThat(incrementalConfiguration.toFile()).exists();
    }

    @Test
    public void testCheckIncrementalWithChanges() throws Exception {
        String alternateSettingsAbsPath = new File("src/test/settings.xml").getAbsolutePath();
        tmpRoot = Files.createTempDirectory("repo");
        //NIO creation and copy content
        Path temp = TestUtil.createAndCopyToDircetory(tmpRoot, "dummy", ResourcesConstants.DUMMY_KIE_INCREMENTAL);
        //end NIO

        //compiler
        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.TRUE);
        CompilationResponse res = compiler.compile(req);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(temp, res.getMavenOutput(),
                                                      "KieDefaultMavenIncrementalCompilerTest.testCheckIncrementalWithChanges");
        }

        //checks
        assertThat(res.isSuccessful()).isTrue();

        List<String> fileNames = new ArrayList<>();
        //impl
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(temp + "/target/classes/dummy"))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }
        assertThat(fileNames).hasSize(2);
        String dummyJava;
        if (fileNames.get(0).endsWith("Dummy.class")) {
            dummyJava = fileNames.get(0);
        } else {
            dummyJava = fileNames.get(1);
        }
        long dummyJavaSize = Paths.get(dummyJava).toFile().length();

        List<String> output = res.getMavenOutput();
        assertThat(isPresent(output,"Previous incremental build state does not exist, performing full build")).isTrue();
        assertThat(isPresent(output,
                                    "Compiled 2 out of 2 sources ")).isTrue();

        Files.delete(Paths.get(temp + "/src/main/java/dummy/DummyA.java"));
        //overwrite the class with a new version with two additional methods and one int variable
        Files.write(Paths.get(temp + "/src/main/java/dummy/Dummy.java"),
                    Files.readAllBytes(Paths.get("src/test/projects/Dummy.java")));

        //second compilation
        res = compiler.compile(req);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(temp, res.getMavenOutput(),
                                                      "KieDefaultMavenIncrementalCompilerTest.testCheckIncrementalWithChanges");
        }

        //checks
        assertThat(res.isSuccessful()).isTrue();

        fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(temp + "/target/classes/dummy"))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }

        assertThat(fileNames).hasSize(1);
        assertThat(fileNames.get(0)).endsWith("Dummy.class");
        long dummyJavaSizeAfterChanges = Paths.get(dummyJava).toFile().length();
        assertThat(dummyJavaSize).isLessThan(dummyJavaSizeAfterChanges);

        output = res.getMavenOutput();
        assertThat(isPresent(output,
                "Performing incremental build")).isTrue();
        assertThat(isPresent(output,
                "Compiled 1 out of 1 sources ")).isTrue();
    }

    private boolean isPresent(List<String> output,
                              String text) {
        return output.stream().anyMatch(s -> s.contains(text));
    }
}
