package org.kie.workbench.common.services.backend.compiler.configuration;

public class MavenConfig {

    public static final String DEPS_BUILD_CLASSPATH = "dependency:build-classpath";

    public static String DEPS_FILENAME = "module";

    public static String CLASSPATH_EXT = ".cpath";

    public static String MAVEN_DEP_PLUGING_OUTPUT_FILE = "-Dmdep.outputFile=";

    public static String MAVEN_DEP_ARG_CLASSPATH = MAVEN_DEP_PLUGING_OUTPUT_FILE + DEPS_FILENAME + CLASSPATH_EXT;

    public static String MAVEN_PLUGIN_CONFIGURATION = "configuration";

    public static String MAVEN_COMPILER_ID = "compilerId";

    public static String MAVEN_SKIP = "skip";

    public static String MAVEN_SOURCE = "source";

    public static String MAVEN_TARGET = "target";

    public static String FAIL_ON_ERROR = "failOnError";

    public static String MAVEN_SKIP_MAIN = "skipMain";

    public static String MAVEN_DEFAULT_COMPILE = "default-compile";

    public static String MAVEN_PHASE_NONE = "none";
}
