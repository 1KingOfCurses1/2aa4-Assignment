package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class BoardTest {

    private static final int DEFAULT_TIMEOUT_MS = 2000;
    private static final String STUBS_SHOULD_RETURN_FALSE = "stubs should return false";
    private Board board;
    private List<Tile> tiles;
    private List<Node> nodes;
    private List<Edge> edges;

    @BeforeEach
    void setup() {
        tiles = List.of(new Tile(1, ResourceType.BRICK, 6), new Tile(2, ResourceType.ORE, 8));
        nodes = List.of(new Node(10), new Node(11));
        edges = List.of(new Edge(100), new Edge(101));
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
    void testIsValidPlacementsCurrentlyReturnFalse() {
        // These are currently stubs being tested for their default behavior (R1.1)
        Player p = new Player(1);
        assertFalse(board.isValidSettlementPlacement(nodes.get(0), p), STUBS_SHOULD_RETURN_FALSE);
        assertFalse(board.isValidRoadPlacement(edges.get(0), p), STUBS_SHOULD_RETURN_FALSE);
        assertFalse(board.isValidCityPlacement(nodes.get(0), p), STUBS_SHOULD_RETURN_FALSE);
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testGetAdjacentCurrentlyReturnEmpty() {
        // More stubs
        assertTrue(board.getAdjacentNodes(nodes.get(0)).isEmpty(), "stubs should return empty list");
        assertTrue(board.getAdjacentEdges(nodes.get(0)).isEmpty(), "stubs should return empty list");
        assertFalse(board.hasAdjacentStructures(nodes.get(0), new Player(1)), "stub should return false");
    }
}
