/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.service;

import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.uberfire.backend.vfs.Path;

@Remote
public interface ProjectScreenService {

    public ProjectScreenModel load( Path path );

    void save( Path pathToPomXML,
               ProjectScreenModel model,
               String comment );

    ProjectScreenModel rename( final Path pathToPomXML,
                               final String renameModel,
                               final String comment );

    void delete( final Path pomXMLPath,
                 final String comment );

    void copy( final Path pomXMLPath,
               final String newFileName,
               final String commitMessage );

    /**
     * Validate whole POM
     * @param pom
     * @return true if valid
     */
    boolean validate( final POM pom );

    /**
     * Validate GroupID element of POM's GAV
     * @param groupId
     * @return true if valid
     */
    boolean validateGroupId( final String groupId );

    /**
     * Validate ArtifactID element of POM's GAV
     * @param artifactId
     * @return true if valid
     */
    boolean validateArtifactId( final String artifactId );

    /**
     * Validate Version element of POM's GAV
     * @param version
     * @return true if valid
     */
    boolean validateVersion( final String version );

}
