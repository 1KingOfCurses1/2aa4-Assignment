package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class BoardTest {

    private static final int DEFAULT_TIMEOUT_MS = 2000;
    private Board board;
    private List<Tile> tiles;
    private List<Node> nodes;
    private List<Edge> edges;

    @BeforeEach
    void setup() {
        tiles = List.of(new Tile(1, ResourceType.BRICK, 6), new Tile(2, ResourceType.ORE, 8));
        nodes = List.of(new Node(10), new Node(11), new Node(12));
        edges = List.of(new Edge(100), new Edge(101));

        // Setup topology: Node 10 --- (Edge 100) --- Node 11 --- (Edge 101) --- Node 12
        edges.get(0).addNode(nodes.get(0)); // 10
        edges.get(0).addNode(nodes.get(1)); // 11

        edges.get(1).addNode(nodes.get(1)); // 11
        edges.get(1).addNode(nodes.get(2)); // 12

        board = new Board(tiles, nodes, edges);
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testGetTileNodeEdgeReturnsCorrectObjectById() {
        assertEquals(tiles.get(0), board.getTile(1), "should find tile 1");
        assertNull(board.getTile(99), "should not find non-existent tile");

        assertEquals(nodes.get(1), board.getNode(11), "should find node 11");
        assertNull(board.getNode(99), "should not find non-existent node");

        assertEquals(edges.get(0), board.getEdge(100), "should find edge 100");
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testIsValidPlacements() {
        Player p = new Player(1);

        // 1. Valid Settlement Placement (Setup phase - player has no structures)
        assertTrue(board.isValidSettlementPlacement(nodes.get(0), p));

        // Place a settlement on node 10
        Settlement s = new Settlement(p, nodes.get(0));
        p.addStructure(s);

        // Node 10 is no longer valid
        assertFalse(board.isValidSettlementPlacement(nodes.get(0), p));

        // Node 11 is not valid due to distance rule
        assertFalse(board.isValidSettlementPlacement(nodes.get(1), p));

        // Node 12 is valid distance, but player needs a connecting road now (not setup
        // phase)
        assertFalse(board.isValidSettlementPlacement(nodes.get(2), p));

        // 2. Valid Road Placement
        // Edge 100 valid because it connects to node 10 which has player's settlement
        assertTrue(board.isValidRoadPlacement(edges.get(0), p));

        // Place road on edge 100
        Road r = new Road(p, edges.get(0));
        edges.get(0).setRoad(r);

        // Edge 100 no longer valid (occupied)
        assertFalse(board.isValidRoadPlacement(edges.get(0), p));

        // Edge 101 valid because it connects to player's road on edge 100
        assertTrue(board.isValidRoadPlacement(edges.get(1), p));

        // 3. Valid City Placement
        // Valid on Node 10 (has settlement owned by p)
        assertTrue(board.isValidCityPlacement(nodes.get(0), p));

        // Invalid on Node 11 (no structure)
        assertFalse(board.isValidCityPlacement(nodes.get(1), p));
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testAdjacency() {
        // Node 10 adjacent to Node 11
        List<Node> adjNodes10 = board.getAdjacentNodes(nodes.get(0));
        assertEquals(1, adjNodes10.size());
        assertTrue(adjNodes10.contains(nodes.get(1)));

        // Node 10 connected to Edge 100
        List<Edge> adjEdges10 = board.getAdjacentEdges(nodes.get(0));
        assertEquals(1, adjEdges10.size());
        assertTrue(adjEdges10.contains(edges.get(0)));

        // Node 11 adjacent to Node 10 and 12
        List<Node> adjNodes11 = board.getAdjacentNodes(nodes.get(1));
        assertEquals(2, adjNodes11.size());
        assertTrue(adjNodes11.contains(nodes.get(0)));
        assertTrue(adjNodes11.contains(nodes.get(2)));

        // Test hasAdjacentStructures
        Player p = new Player(1);
        Settlement s = new Settlement(p, nodes.get(0));
        p.addStructure(s); // this sets the structure on node 10

        // Node 11 should have an adjacent structure
        assertTrue(board.hasAdjacentStructures(nodes.get(1)));
        // Node 12 should not
        assertFalse(board.hasAdjacentStructures(nodes.get(2)));
    }
}
