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
package org.kie.workbench.common.services.backend.compiler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.MavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class DefaultMavenIncrementalCompilerTest {

    private Path mavenRepo;
    private Logger logger = LoggerFactory.getLogger(DefaultMavenIncrementalCompilerTest.class);

    @Before
    public void setUp() throws Exception {

        mavenRepo = Paths.get(System.getProperty("user.home"),
                              "/.m2/repository");

        if (!Files.exists(mavenRepo)) {
            logger.info("Creating a m2_repo into " + mavenRepo);
            if (!Files.exists(Files.createDirectories(mavenRepo))) {
                throw new Exception("Folder not writable in the project");
            }
        }
    }

    @Test
    public void testIsValidMavenHome() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        //NIO creation and copy content
        Path temp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                      "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy"),
                          temp);
        //end NIO

        AFCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.NONE);

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.VERSION},
                                                               Boolean.FALSE, Boolean.TRUE);
        CompilationResponse res = compiler.compileSync(req);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput(),
                                                      "DefaultMavenIncrementalCompilerTest.testIsValidMavenHome");
        }
        Assert.assertTrue(res.isSuccessful());

        TestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testIncrementalWithPluginEnabled() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        //NIO creation and copy content
        Path temp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                      "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy"),
                          temp);
        //end NIO

        AFCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.NONE);

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                               Boolean.FALSE, Boolean.TRUE);
        CompilationResponse res = compiler.compileSync(req);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput(),
                                                      "DefaultMavenIncrementalCompilerTest.testIncrementalWithPluginEnabled");
        }
        Assert.assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(temp.toAbsolutePath().toString(),
                                                  "/target/incremental/kie.io.takari.maven.plugins_kie-takari-lifecycle-plugin_compile_compile");
        Assert.assertTrue(incrementalConfiguration.toFile().exists());

        TestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testIncrementalWithPluginEnabledThreeTime() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        //NIO creation and copy content
        Path temp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                      "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy"),
                          temp);
        //end NIO

        AFCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.NONE);

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                               Boolean.FALSE, Boolean.TRUE);
        CompilationResponse res = compiler.compileSync(req);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput(),
                                                      "DefaultMavenIncrementalCompilerTest.testIncrementalWithPluginEnabledThreeTime");
        }
        Assert.assertTrue(res.isSuccessful());

        res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(temp.toAbsolutePath().toString(),
                                                  "/target/incremental/kie.io.takari.maven.plugins_kie-takari-lifecycle-plugin_compile_compile");
        Assert.assertTrue(incrementalConfiguration.toFile().exists());

        TestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testCheckIncrementalWithChanges() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        //NIO creation and copy content
        Path temp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                      "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy_incremental"),
                          temp);
        //end NIO

        //compiler
        AFCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE, Boolean.TRUE);
        CompilationResponse res = compiler.compileSync(req);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput(),
                                                      "DefaultMavenIncrementalCompilerTest.testCheckIncrementalWithChanges");
        }

        //checks
        Assert.assertTrue(res.isSuccessful());

        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(temp + "/target/classes/dummy"))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }
        Assert.assertTrue(fileNames.size() == 2);
        String dummyJava;
        if (fileNames.get(0).endsWith("Dummy.class")) {
            dummyJava = fileNames.get(0);
        } else {
            dummyJava = fileNames.get(1);
        }
        long dummyJavaSize = Paths.get(dummyJava).toFile().length();

        List<String> output = res.getMavenOutput();
        Assert.assertTrue(isPresent(output,
                                    "Previous incremental build state does not exist, performing full build"));
        Assert.assertTrue(isPresent(output,
                                    "Compiled 2 out of 2 sources "));

        Files.delete(Paths.get(temp + "/src/main/java/dummy/DummyA.java"));
        //overwrite the class with a new version with two additional methods and one int variable
        Files.write(Paths.get(temp + "/src/main/java/dummy/Dummy.java"),
                    Files.readAllBytes(Paths.get("src/test/projects/Dummy.java")));

        //second compilation
        res = compiler.compileSync(req);

        //checks
        Assert.assertTrue(res.isSuccessful());

        fileNames = new ArrayList<>();
        //impl
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(temp + "/target/classes/dummy"))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }

        Assert.assertTrue(fileNames.size() == 1);
        Assert.assertTrue(fileNames.get(0).endsWith("Dummy.class"));
        long dummyJavaSizeAfterChanges = Paths.get(dummyJava).toFile().length();
        Assert.assertTrue(dummyJavaSize < dummyJavaSizeAfterChanges);

        output = res.getMavenOutput();
        Assert.assertTrue(isPresent(output,
                                    "Performing incremental build"));
        Assert.assertTrue(isPresent(output,
                                    "Compiled 1 out of 1 sources "));

        TestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testError() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        //NIO creation and copy content
        Path temp = Files.createDirectories(Paths.get(tmpRoot.toString(), "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy_kie_multimodule_untouched_with_error"),
                          temp);
        //end NIO

        //compiler
        AFCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.FAIL_NEVER, MavenCLIArgs.DEBUG},
                                                               Boolean.TRUE, Boolean.TRUE);
        CompilationResponse res = compiler.compileSync(req);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput(),
                                                      "DefaultMavenIncrementalCompilerTest.testError");
        }

        //checks
        Assert.assertTrue(res.isSuccessful());

        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(temp + "/dummyA/target/classes/dummy"))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }
        Assert.assertTrue(fileNames.size() == 1);
        String dummyAJava = null;
        if (fileNames.get(0).endsWith("DummyA.class")) {
            dummyAJava = fileNames.get(0);
        }
        Assert.assertTrue(dummyAJava != null);

        fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(temp + "/dummyB/target/classes/dummy"))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }

        Assert.assertTrue(fileNames.size() == 1);
        String dummyBJava = null;
        if (fileNames.get(0).endsWith("DummyB.class")) {
            dummyBJava = fileNames.get(0);
        }
        Assert.assertTrue(dummyBJava != null);

        List<String> output = res.getMavenOutput();
        Assert.assertTrue(isPresent(output,
                                    "Previous incremental build state does not exist, performing full build"));
        Assert.assertTrue(isPresent(output,
                                    "Compiled 2 out of 2 sources "));

        Assert.assertTrue(isPresent(output,
                                    "Compiled 1 out of 1 sources "));

        TestUtil.rm(tmpRoot.toFile());
    }



    private boolean isPresent(List<String> output,
                              String text) {
        return output.stream().anyMatch(s -> s.contains(text));
    }
}
