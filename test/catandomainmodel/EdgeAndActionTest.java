package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class EdgeAndActionTest {

    private static final int DEFAULT_TIMEOUT_MS = 2000;

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testEdgeAddNodeBoundaryAllowsOnlyTwoNodes() {
        Edge e = new Edge(1);

        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);

        e.addNode(n1);
        e.addNode(n2);
        e.addNode(n3);

        assertEquals(2, e.getNodes().size(), "edge should keep at most 2 nodes");
        assertEquals(n1, e.getNodes().get(0), "first node kept");
        assertEquals(n2, e.getNodes().get(1), "second node kept");
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testActionToStringMatchesConsoleFormat() {
        Action a = new Action(12, 4, "PASS");
        assertEquals("[12] / [4]: PASS", a.toString(), "action string format");
    }
}