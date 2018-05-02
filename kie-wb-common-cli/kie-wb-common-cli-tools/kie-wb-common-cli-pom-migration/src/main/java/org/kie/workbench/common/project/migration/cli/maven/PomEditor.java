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
package org.kie.workbench.common.project.migration.cli.maven;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.StandardOpenOption;

public class PomEditor {

    private static final String PROPERTIES_FILE = "PomMigration.properties";
    private static final String JSON_POM_MIGRATION = "pom-migration.json";
    private static final String JSON_POM_MANDATORY_DEPS = "pom-mandatory.json";
    private static final String KIE_VERSION_KEY = "KIE_VERSION";
    private final Logger logger = LoggerFactory.getLogger(PomEditor.class);
    private String FILE_URI = "file://";
    private MavenXpp3Reader reader;
    private MavenXpp3Writer writer;
    private String kieVersion;
    private String KIE_PKG = "org.kie";
    private String JBPM_PKG = "org.jbpm";
    private String DROOLS_PKG = "org.drools";
    private String OPTAPLANNER_PKG = "org.optaplanner";
    private String KJAR_PKG = "kjar";
    private String KIE_MAVEN_PLUGIN_ARTIFACT_ID = "kie-maven-plugin";
    private JSONDTO jsonConf;
    private PomJsonReader jsonReader, jsonMandatoryDepsReader;
    private SystemAccess system;

    private Properties props;

    public PomEditor(SystemAccess system) {
        this.system = system;
        reader = new MavenXpp3Reader();
        writer = new MavenXpp3Writer();
        props = loadProperties(PROPERTIES_FILE);
        jsonMandatoryDepsReader = new PomJsonReader(getClass().getClassLoader().getResourceAsStream(JSON_POM_MANDATORY_DEPS));
        kieVersion = props.getProperty(KIE_VERSION_KEY);
        if (kieVersion == null) {
            throw new RuntimeException("Kie version missing in configuration files");
        }

    }

    public Model updatePom(Path pom) {
        try {
            Model model = getModel(pom);
            Build build = getBuild(model);
            updatePackaging(model);
            updateBuildTag(build);
            updateDependenciesTag(model);
            updateRepositories(model);
            updatePluginRepositories(model);
            boolean written = write(model, pom.toAbsolutePath().toString());
            if (written) {
                return model;
            } else {
                return new Model();
            }
        } catch (Exception e) {
            system.err().println("Error occurred during POMs migration:" + e.getMessage());
            logger.error(e.getMessage());
            return new Model();
        }
    }

    public Model updatePom(Path pom, String pathJsonFile) {
        try {
            jsonReader = new PomJsonReader(pathJsonFile, JSON_POM_MIGRATION);
            jsonConf = jsonReader.readDepsAndRepos();
            return updatePom(pom);
        } catch (Exception e) {
            system.err().println("Error occurred during POMs migration:" + e.getMessage());
            logger.error(e.getMessage());
            return new Model();
        }
    }

    private void updatePackaging(Model model) {
        String packaging = model.getPackaging();
        if (packaging == null || !packaging.equals(KJAR_PKG)) {
            model.setPackaging(KJAR_PKG);
        }
    }

    private Properties loadProperties(String propName) {
        Properties prop = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream(propName);
        if (in == null) {
            logger.info("{} not available with the classloader, skip to the next ConfigurationStrategy. \n", propName);
        } else {
            try {
                prop.load(in);
            } catch (IOException e) {
                logger.error(e.getMessage());
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    //suppressed
                }
            }
        }
        return prop;
    }

    /***************************************** Build TAG *****************************************/

    private void updateBuildTag(Build build) {
        List<Plugin> buildPlugins = getBuildPlugins(build);
        processKieMavenCompiler(buildPlugins);
    }

    private void processKieMavenCompiler(List<Plugin> buildPlugins) {
        PluginPresence kieMavenCompiler = getPluginPresence(buildPlugins,
                                                            KIE_PKG,
                                                            KIE_MAVEN_PLUGIN_ARTIFACT_ID);
        if (!kieMavenCompiler.isPresent()) {
            buildPlugins.add(getKieMavenPlugin());
        }
    }

    public Model getModel(Path pom) throws Exception {
        return reader.read(new ByteArrayInputStream(Files.readAllBytes(pom)));
    }

    private Build getBuild(Model model) {
        Build build = model.getBuild();
        if (build == null) {  //pom without build tag
            model.setBuild(new Build());
            build = model.getBuild();
        }
        return build;
    }

    private PluginPresence getPluginPresence(List<Plugin> plugins, String groupID, String artifactID) {
        boolean result = false;
        int i = 0;
        for (Plugin plugin : plugins) {
            if (plugin.getGroupId().equals(groupID) && plugin.getArtifactId().equals(artifactID)) {
                result = true;
                break;
            }
            i++;
        }
        return new PluginPresence(result, i);
    }

    private List<Plugin> getBuildPlugins(Build build) {
        return build.getPlugins();
    }

    private Plugin getKieMavenPlugin() {
        Plugin kieMavenPlugin = new Plugin();
        kieMavenPlugin.setGroupId(KIE_PKG);
        kieMavenPlugin.setArtifactId(KIE_MAVEN_PLUGIN_ARTIFACT_ID);
        kieMavenPlugin.setVersion(kieVersion);
        kieMavenPlugin.setExtensions(true);
        return kieMavenPlugin;
    }

    /***************************************** END Build TAG *****************************************/
    /***************************************** Start Dependencies TAG *****************************************/

    private void updateDependenciesTag(Model model) {
        // Order matter !!!!
        DependenciesCollection coll = new DependenciesCollection();
        coll.addDependencies(jsonMandatoryDepsReader.readDeps().getDependencies());
        coll.addDependenciesKeys(getChangedCurrentDependencies(model.getDependencies()));
        model.setDependencies(coll.getAsDependencyList());
    }

    private List<DependencyKey> getChangedCurrentDependencies(List<Dependency> dependencies) {
        List<DependencyKey> newDeps = new ArrayList<>();
        for (Dependency dep : dependencies) {
            Dependency newDep = new Dependency();
            newDep.setGroupId(dep.getGroupId());
            newDep.setArtifactId(dep.getArtifactId());
            if (dep.getClassifier() != null) {
                newDep.setClassifier(dep.getClassifier());
            }
            if (dep.getVersion() == null && isKieGroupDependency(dep.getGroupId())) {
                newDep.setVersion(kieVersion);
            }
            if (dep.getScope() != null) {
                newDep.setScope(dep.getScope());
            }
            newDeps.add(new DependencyKey(newDep));
        }
        return newDeps;
    }

    private boolean isKieGroupDependency(String groupID) {
        return (groupID.startsWith(KIE_PKG)
                || groupID.startsWith(OPTAPLANNER_PKG)
                || groupID.startsWith(DROOLS_PKG)
                || groupID.startsWith(JBPM_PKG));
    }

    /***************************************** End Dependencies TAG *****************************************/
    /***************************************** Start Repositories TAG *****************************************/

    private void updateRepositories(Model model) {
        List<Repository> repos = model.getRepositories();
        if (repos == null || repos.isEmpty()) {
            repos = new ArrayList<>();
        }
        applyMandatoryRepos(repos);
        model.setRepositories(repos);
    }

    private void applyMandatoryRepos(List<Repository> repos) {
        if (jsonConf != null) {
            for (Repository repoFromJson : jsonConf.getRepositories()) {
                repos.add(repoFromJson);
            }
        }
    }

    /***************************************** End Repositories TAG *****************************************/
    /***************************************** Start PluginRepositories TAG *****************************************/

    private void updatePluginRepositories(Model model) {
        List<Repository> repos = model.getPluginRepositories();
        if (repos == null || repos.isEmpty()) {
            repos = new ArrayList<>();
        }
        applyMandatoryPluginRepos(repos, jsonConf);
        model.setPluginRepositories(repos);
    }

    private void applyMandatoryPluginRepos(List<Repository> repos, JSONDTO jsonConf) {
        if (jsonConf != null) {
            for (Repository repoFromJson : jsonConf.getPluginRepositories()) {
                repos.add(repoFromJson);
            }
        }
    }

    /***************************************** Start PluginRepositories TAG *****************************************/

    private boolean write(Model model, String absolutePath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            writer.write(baos, model);
            if (logger.isDebugEnabled()) {
                logger.info("Pom changed:{}",
                            new String(baos.toByteArray(),
                                       StandardCharsets.UTF_8));
            }

            Path pomParent = Paths.get(URI.create(
                    new StringBuffer().
                            append(FILE_URI).
                            append(absolutePath).toString()));
            Files.delete(pomParent);
            Files.write(pomParent, baos.toByteArray(), StandardOpenOption.CREATE_NEW);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                //suppressed
            }
        }
    }
}
