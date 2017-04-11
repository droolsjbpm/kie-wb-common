/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.project;

import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;

@RunWith(WeldJUnitRunner.class)
public class ModuleServiceImplResolveModuleInvalidNoPOMTest
        extends ModuleTestBase {

    @Test
    public void testModuleServiceInstantiation() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);
        assertNotNull(moduleService);
    }

    @Test
    public void testResolveModuleWithRootPath() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        final Module result = moduleService.resolveModule(rootPath);
        assertEquals("org.kie.workbench.services", result.getPom().getGav().getGroupId());
    }

    @Test
    public void testResolveModuleWithChildPath() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM/src");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        final Module result = moduleService.resolveModule(testPath);
        assertEquals("org.kie.workbench.services", result.getPom().getGav().getGroupId());
    }

    @Test
    public void testResolveModuleWithJavaFile() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM/src/main/java/org/kie/test/Bean.java");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        final Module result = moduleService.resolveModule(testPath);
        assertEquals("org.kie.workbench.services", result.getPom().getGav().getGroupId());
    }

    @Test
    public void testResolveModuleWithResourcesFile() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM/src/main/resources/rule1.drl");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        final Module result = moduleService.resolveModule(testPath);
        assertEquals("org.kie.workbench.services", result.getPom().getGav().getGroupId());
    }
}
