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

import org.kie.workbench.common.services.backend.utils.LoadProjectDependencyUtil;
import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.rule.KieModuleMetaInfo;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class ClassLoaderProviderTest {

    private Path mavenRepo;
    Path tmpRoot;
    Path tmp;
    Path uberfireTmp;

    private Logger logger = LoggerFactory.getLogger(ClassLoaderProviderTest.class);

    @Before
    public void setUp() throws Exception {
        mavenRepo = TestUtil.createMavenRepo();
    }

    private CompilationResponse compileProjectInRepo(String... mavenPhases) throws IOException {
        //we use NIO for this part of the test because Uberfire lack the implementation to copy a tree
        tmpRoot = Files.createTempDirectory("repo");
        tmp = TestUtil.createAndCopyToDircetory(tmpRoot, "dummy", ResourcesConstants.DUMMY_KIE_MULTIMODULE_CLASSLOADER_DIR);

        uberfireTmp = Paths.get(tmp.toAbsolutePath().toString());

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.NONE);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(uberfireTmp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               mavenPhases,
                                                               Boolean.FALSE);
        return compiler.compile(req);
    }

    @Test
    public void loadProjectClassloaderTest() throws Exception {
        CompilationResponse res = compileProjectInRepo(MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE, MavenCLIArgs.INSTALL);

        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(tmp, res.getMavenOutput(),
                                                      "ClassLoaderProviderTest.loadProjectClassloaderTest");
        }
        assertThat(res.isSuccessful()).isTrue();

        List<String> pomList = MavenUtils.searchPoms(Paths.get(ResourcesConstants.DUMMY_KIE_MULTIMODULE_CLASSLOADER_DIR));
        Optional<ClassLoader> clazzLoader = CompilerClassloaderUtils.loadDependenciesClassloaderFromProject(pomList,
                                                                                                            mavenRepo.toAbsolutePath().toString());
        assertThat(clazzLoader).isNotNull();
        assertThat(clazzLoader).isPresent();

        LoadProjectDependencyUtil.loadLoggerFactory(clazzLoader.get());

        if (tmpRoot != null) {
            TestUtil.rm(tmpRoot.toFile());
        }
    }

    @Test
    public void loadProjectClassloaderFromStringTest() throws Exception {
        CompilationResponse res = compileProjectInRepo(MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE, MavenCLIArgs.INSTALL);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(tmp, res.getMavenOutput(),
                                                      "ClassLoaderProviderTest.loadProjectClassloaderFromStringTest");
        }
        assertThat(res.isSuccessful()).isTrue();

        Optional<ClassLoader> clazzLoader = CompilerClassloaderUtils.loadDependenciesClassloaderFromProject(uberfireTmp.toAbsolutePath().toString(),
                                                                                                            mavenRepo.toAbsolutePath().toString());
        assertThat(clazzLoader).isNotNull();
        assertThat(clazzLoader.isPresent()).isTrue();

        LoadProjectDependencyUtil.loadLoggerFactory(clazzLoader.get());

        if (tmpRoot != null) {
            TestUtil.rm(tmpRoot.toFile());
        }
    }

    @Test
    public void loadTargetFolderClassloaderTest() throws Exception {
        CompilationResponse res = compileProjectInRepo(MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE, MavenCLIArgs.INSTALL);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(tmp, res.getMavenOutput(),
                                                      "ClassLoaderProviderTest.loadTargetFolderClassloaderTest");
        }
        assertThat(res.isSuccessful()).isTrue();

        List<String> pomList = MavenUtils.searchPoms(uberfireTmp);
        Optional<ClassLoader> clazzLoader = CompilerClassloaderUtils.getClassloaderFromProjectTargets(pomList);
        assertThat(clazzLoader).isNotNull();
        assertThat(clazzLoader.isPresent()).isTrue();

        LoadProjectDependencyUtil.loadDummyB(clazzLoader.get());

        if (tmpRoot != null) {
            TestUtil.rm(tmpRoot.toFile());
        }
    }

    @Test
    public void getClassloaderFromAllDependenciesTestSimple() {
        Path path = Paths.get(".").resolve(ResourcesConstants.DUMMY_DEPS_SIMPLE_DIR);
        Optional<ClassLoader> classloaderOptional = CompilerClassloaderUtils.getClassloaderFromAllDependencies(path.toAbsolutePath().toString(),
                                                                                                               mavenRepo.toAbsolutePath().toString());
        assertThat(classloaderOptional.isPresent()).isTrue();
        ClassLoader classloader = classloaderOptional.get();
        URLClassLoader urlsc = (URLClassLoader) classloader;
        assertThat(urlsc.getURLs()).hasSize(4);
    }

    @Test
    public void getClassloaderFromAllDependenciesTestComplex() {
        Path path = Paths.get(".").resolve(ResourcesConstants.DUMMY_DEPS_COMPLEX_DIR);
        Optional<ClassLoader> classloaderOptional = CompilerClassloaderUtils.getClassloaderFromAllDependencies(path.toAbsolutePath().toString(),
                                                                                                               mavenRepo.toAbsolutePath().toString());
        assertThat(classloaderOptional.isPresent()).isTrue();
        ClassLoader classloader = classloaderOptional.get();
        URLClassLoader urlsc = (URLClassLoader) classloader;
        assertThat(urlsc.getURLs()).hasSize(7);
    }

    @Test
    public void getResourcesFromADroolsPRJ() throws Exception {
        /**
         * If the test fail check if the Drools core classes used, KieModuleMetaInfo and TypeMetaInfo implements Serializable
         * */
        String alternateSettingsAbsPath = new File("src/test/settings.xml").getAbsolutePath();
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = TestUtil.createAndCopyToDircetory(tmpRoot,"dummy", ResourcesConstants.KJAR_2_SINGLE_RESOURCES);

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.KIE_AND_CLASSPATH_AFTER_DEPS);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(tmp.toUri()));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);
        KieCompilationResponse res = (KieCompilationResponse) compiler.compile(req);
        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(tmp, res.getMavenOutput(),
                                                      "KieMetadataTest.compileAndloadKieJarSingleMetadataWithPackagedJar");
        }
        if (!res.isSuccessful()) {
            List<String> msgs = res.getMavenOutput();
            for (String msg : msgs) {
                logger.info(msg);
            }
        }

        assertThat(res.isSuccessful()).isTrue();

        Optional<KieModuleMetaInfo> metaDataOptional = res.getKieModuleMetaInfo();
        assertThat(metaDataOptional.isPresent()).isTrue();
        KieModuleMetaInfo kieModuleMetaInfo = metaDataOptional.get();
        assertThat(kieModuleMetaInfo).isNotNull();

        Map<String, Set<String>> rulesBP = kieModuleMetaInfo.getRulesByPackage();
        assertThat(rulesBP).hasSize(1);

        Optional<KieModule> kieModuleOptional = res.getKieModule();
        assertThat(kieModuleOptional.isPresent()).isTrue();
        KieModule kModule = kieModuleOptional.get();

        assertThat(res.getDependenciesAsURI()).hasSize(4);

        KieModuleMetaData kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) kModule,
                                                                        res.getDependenciesAsURI());

        assertThat(kieModuleMetaData).isNotNull();

        List<String> resources = CompilerClassloaderUtils.getStringFromTargets(tmpRoot);
        assertThat(resources).hasSize(3);
        TestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void getResourcesFromADroolsPRJWithError() throws Exception {
        /**
         * If the test fail check if the Drools core classes used, KieModuleMetaInfo and TypeMetaInfo implements Serializable
         * */
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = TestUtil.createAndCopyToDircetory(tmpRoot,"dummy", ResourcesConstants.KJAR_2_SINGLE_RESOURCES_WITH_ERROR);

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.KIE_AFTER);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(tmp.toUri()));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL},
                                                               Boolean.FALSE);
        KieCompilationResponse res = (KieCompilationResponse) compiler.compile(req);

        if (!res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(tmp, res.getMavenOutput(),
                                                      "KieMetadataTest.getResourcesFromADroolsPRJWithError");
        }
        if (!res.isSuccessful()) {
            List<String> msgs = res.getMavenOutput();
            for (String msg : msgs) {
                logger.info(msg);
            }
        }

        assertThat(res.isSuccessful()).isTrue();

        Optional<KieModuleMetaInfo> metaDataOptional = res.getKieModuleMetaInfo();
        assertThat(metaDataOptional.isPresent()).isTrue();
        KieModuleMetaInfo kieModuleMetaInfo = metaDataOptional.get();
        assertThat(kieModuleMetaInfo).isNotNull();

        Map<String, Set<String>> rulesBP = kieModuleMetaInfo.getRulesByPackage();
        assertThat(rulesBP).hasSize(1);

        Optional<KieModule> kieModuleOptional = res.getKieModule();
        assertThat(kieModuleOptional.isPresent()).isTrue();

        List<String> classloaderOptional = CompilerClassloaderUtils.getStringFromTargets(tmpRoot);
        assertThat(classloaderOptional).hasSize(3);
        TestUtil.rm(tmpRoot.toFile());
    }
}
