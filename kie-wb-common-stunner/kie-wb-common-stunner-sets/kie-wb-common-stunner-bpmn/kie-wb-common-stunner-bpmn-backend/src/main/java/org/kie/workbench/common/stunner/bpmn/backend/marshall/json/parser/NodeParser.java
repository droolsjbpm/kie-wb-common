/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser;

import java.util.LinkedList;
import java.util.List;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.ArrayParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.IntegerFieldParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.ObjectParser;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common.StringFieldParser;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public class NodeParser extends ElementParser<Node<View, Edge>> {

    private final List<Parser> children = new LinkedList<>();

    public NodeParser( String name, Node<View, Edge> element ) {
        super( name, element );
    }

    public NodeParser addChild( Parser parser ) {
        children.add( parser );
        return this;
    }

    @Override
    public void initialize( Context context ) {
        super.initialize( context );
        // Children.
        ArrayParser childrenParser = new ArrayParser( "childShapes" );
        for ( Parser childParser : children ) {
            if ( childParser instanceof ContextualParser ) {
                ( ( ContextualParser ) childParser ).initialize( context );
                childrenParser.addParser( childParser );
            }
        }
        super.addParser( childrenParser );
        // Outgoing.
        ArrayParser outgoingParser = new ArrayParser( "outgoing" );
        super.addParser( outgoingParser );
        List<Edge> outEdges = element.getOutEdges();
        if ( null != outEdges && !outEdges.isEmpty() ) {
            for ( Edge edge : outEdges ) {
                String outId = null;
                if ( isViewEdge( edge ) ) {
                    // View connectors, such as sequence flows.
                    outId = edge.getUUID();

                } else if ( isDockEdge( edge ) ) {
                    // Docked nodes. Oryx marshallers do not expect an outgoing sequence flow id here, it expects the
                    // id of the docked node.
                    Node docked = edge.getTargetNode();
                    outId = docked.getUUID();
                }
                if ( null != outId ) {
                    outgoingParser.addParser( new ObjectParser( "" ).addParser( new StringFieldParser( "resourceId", outId ) ) );

                }

            }

        }
        // Dockers - Only use if this node is docked.
        if ( isDocked( element ) ) {
            Bounds.Bound ul = element.getContent().getBounds().getUpperLeft();
            ObjectParser docker1ObjParser = new ObjectParser( "" )
                    .addParser( new IntegerFieldParser( "x", ul.getX().intValue() ) )
                    .addParser( new IntegerFieldParser( "y", ul.getY().intValue() ) );
            ArrayParser dockersParser = new ArrayParser( "dockers" )
                    .addParser( docker1ObjParser );
            super.addParser( dockersParser );

        }

    }

    @Override
    protected void parseBounds( Bounds.Bound ul, Bounds.Bound lr ) {
        Node<View, Edge> dockSource = getDockSourceNode( element );
        if ( null == dockSource ) {
            super.parseBounds( ul, lr );

        } else {
            Bounds.Bound parentUl = dockSource.getContent().getBounds().getUpperLeft();
            Bounds.Bound parentLr = dockSource.getContent().getBounds().getLowerRight();
            double bbW = lr.getX() - ul.getX();
            double bbH = lr.getY() - ul.getY();
            double ulx = parentUl.getX() + ul.getX() - ( bbW / 2 );
            double uly = parentUl.getY() + ul.getY() - ( bbH / 2 );
            double lrx = ulx + ( bbW / 2 );
            double lry = uly + ( bbH / 2 );
            Bounds.Bound newUl = new BoundImpl( ulx, uly );
            Bounds.Bound newLr = new BoundImpl( lrx, lry );
            super.parseBounds( newUl, newLr );

        }

    }

    private boolean isDocked( Node<View, Edge> node ) {
        return null != getDockSourceNode( node );
    }

    @SuppressWarnings( "unchecked" )
    private Node<View, Edge> getDockSourceNode( Node<View, Edge> node ) {
        List<Edge> inEdges = node.getInEdges();
        if ( null != inEdges && !inEdges.isEmpty() ) {
            for ( Edge edge : inEdges ) {
                if ( isDockEdge( edge ) ) {
                    return edge.getSourceNode();
                }
            }
        }
        return null;
    }

    private boolean isViewEdge( Edge edge ) {
        return edge.getContent() instanceof ViewConnector;
    }

    private boolean isDockEdge( Edge edge ) {
        return edge.getContent() instanceof Dock;
    }

}
