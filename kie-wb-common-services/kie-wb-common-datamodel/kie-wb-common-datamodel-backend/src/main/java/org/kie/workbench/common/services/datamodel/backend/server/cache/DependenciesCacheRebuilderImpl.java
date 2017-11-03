package org.kie.workbench.common.services.datamodel.backend.server.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.cache.DependenciesCacheRebuilder;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.services.backend.builder.af.KieAfBuilderClassloaderUtil;

@ApplicationScoped
public class DependenciesCacheRebuilderImpl implements DependenciesCacheRebuilder<Project> {

    @Inject
    private KieAfBuilderClassloaderUtil builderClassloaderUtil;

    @Inject
    private LRUProjectDataModelOracleCache projectDataModelOracleCache;

    @Override
    public void rebuild(final Project project) {
        projectDataModelOracleCache.invalidateCache(project);
        builderClassloaderUtil.getProjectClassloader(project);
    }
}
