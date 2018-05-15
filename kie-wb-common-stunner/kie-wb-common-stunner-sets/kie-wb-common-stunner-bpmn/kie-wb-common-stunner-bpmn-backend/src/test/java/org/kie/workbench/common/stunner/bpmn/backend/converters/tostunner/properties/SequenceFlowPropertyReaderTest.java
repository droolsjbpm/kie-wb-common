package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.dc;

public class SequenceFlowPropertyReaderTest {

    private final String SEQ_ID = "SEQ_ID", SOURCE_ID = "SOURCE_ID", TARGET_ID = "TARGET_ID";

    @Test
    public void getConnectionsNoWaypoints() {
        TestDefinitionsWriter d = new TestDefinitionsWriter();
        PropertyReaderFactory factory = new PropertyReaderFactory(d.getDefinitionResolver());

        Bounds sourceBounds = boundsOf(10, 10, 50, 50);
        FlowNode source = d.mockNode(SOURCE_ID, sourceBounds);
        Bounds targetBounds = boundsOf(100, 100, 60, 60);
        FlowNode target = d.mockNode(TARGET_ID, targetBounds);
        List<Point> noWaypoints = Collections.emptyList();

        SequenceFlow el = d.sequenceFlowOf(SEQ_ID, source, target, noWaypoints);

        SequenceFlowPropertyReader p = factory.of(el);

        // this is inferred from behavior of the old marshallers
        Connection sourceConnection = p.getSourceConnection();
        assertEquals(sourceBounds.getWidth(), (float) sourceConnection.getLocation().getX());
        assertEquals(sourceBounds.getHeight() / 2f, (float) sourceConnection.getLocation().getY());

        Connection targetConnection = p.getTargetConnection();
        assertEquals(0.0f, (float) targetConnection.getLocation().getX());
        assertEquals(targetBounds.getHeight() / 2.0f, (float) targetConnection.getLocation().getY());
    }

    @Test
    public void getConnectionsWithWaypoints() {
        TestDefinitionsWriter d = new TestDefinitionsWriter();
        PropertyReaderFactory factory = new PropertyReaderFactory(d.getDefinitionResolver());

        Bounds sourceBounds = boundsOf(10, 10, 50, 50);
        FlowNode source = d.mockNode(SOURCE_ID, sourceBounds);
        Bounds targetBounds = boundsOf(100, 100, 60, 60);
        FlowNode target = d.mockNode(TARGET_ID, targetBounds);
        Point sourcePoint = pointOf(10, 20);
        Point targetPoint = pointOf(100, 120);
        List<Point> waypoints = asList(sourcePoint, targetPoint);

        SequenceFlow el = d.sequenceFlowOf(SEQ_ID, source, target, waypoints);

        SequenceFlowPropertyReader p = factory.of(el);

        Connection sourceConnection = p.getSourceConnection();
        assertEquals(sourcePoint.getX() - sourceBounds.getX(), (float) sourceConnection.getLocation().getX());
        assertEquals(sourcePoint.getY() - sourceBounds.getY(), (float) sourceConnection.getLocation().getY());

        Connection targetConnection = p.getTargetConnection();
        assertEquals(targetPoint.getX() - targetBounds.getX(), (float) targetConnection.getLocation().getX());
        assertEquals(targetPoint.getY() - targetBounds.getY(), (float) targetConnection.getLocation().getY());
    }

    @Test
    public void getWaypoints() {
        TestDefinitionsWriter d = new TestDefinitionsWriter();
        PropertyReaderFactory factory = new PropertyReaderFactory(d.getDefinitionResolver());

        Bounds sourceBounds = boundsOf(10, 10, 50, 50);
        FlowNode source = d.mockNode(SOURCE_ID, sourceBounds);
        Bounds targetBounds = boundsOf(100, 100, 60, 60);
        FlowNode target = d.mockNode(TARGET_ID, targetBounds);
        Point sourcePoint = pointOf(10, 20);
        Point mid1 = pointOf(15, 25);
        Point mid2 = pointOf(20, 30);
        Point targetPoint = pointOf(100, 120);
        List<Point> waypoints = asList(sourcePoint,
                                       mid1,
                                       mid2,
                                       targetPoint);

        SequenceFlow el = d.sequenceFlowOf(SEQ_ID, source, target, waypoints);

        SequenceFlowPropertyReader p = factory.of(el);
        List<Point2D> controlPoints = p.getControlPoints();
        List<Point2D> expected = asList(
                Point2D.create(mid1.getX(), mid1.getY()),
                Point2D.create(mid2.getX(), mid2.getY()));

        assertEquals(expected, controlPoints);
    }

    private Point pointOf(float x, float y) {
        Point point = dc.createPoint();
        point.setX(x);
        point.setY(y);
        return point;
    }

    private Bounds boundsOf(float x, float y, float width, float height) {
        Bounds bounds = dc.createBounds();
        bounds.setX(x);
        bounds.setY(y);
        bounds.setWidth(width);
        bounds.setHeight(height);
        return bounds;
    }
}
