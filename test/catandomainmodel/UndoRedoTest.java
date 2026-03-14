package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.lang.reflect.Method;

class UndoRedoTest {

    private Game game;
    private Board board;
    private List<Player> players;
    private CommandManager commandManager;
    private ResourceBank resourceBank;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() throws Exception {
        Method createTiles = Demonstrator.class.getDeclaredMethod("createTiles");
        createTiles.setAccessible(true);
        List<Tile> tiles = (List<Tile>) createTiles.invoke(null);

        Method createNodes = Demonstrator.class.getDeclaredMethod("createNodes");
        createNodes.setAccessible(true);
        List<Node> nodes = (List<Node>) createNodes.invoke(null);

        Method map = Demonstrator.class.getDeclaredMethod("mapNodesToTiles", List.class, List.class);
        map.setAccessible(true);
        map.invoke(null, tiles, nodes);

        Method createEdges = Demonstrator.class.getDeclaredMethod("createEdges", List.class);
        createEdges.setAccessible(true);
        List<Edge> edges = (List<Edge>) createEdges.invoke(null, nodes);

        board = new Board(tiles, nodes, edges);
        players = List.of(new Player(1), new Player(2));
        List<IAgent> agents = List.of(new Agent(players.get(0)), new Agent(players.get(1)));
        game = new Game(board, players, agents);
        resourceBank = game.getResourceBank();
        
        // Use reflection to get commandManager from game
        java.lang.reflect.Field field = Game.class.getDeclaredField("commandManager");
        field.setAccessible(true);
        commandManager = (CommandManager) field.get(game);
    }

    @Test
    void testBuildRoadUndoRedo() {
        Player p1 = players.get(0);
        Edge e = board.getEdge(0);
        
        // Give resources
        p1.getResourceHand().add(ResourceType.BRICK, 1);
        p1.getResourceHand().add(ResourceType.LUMBER, 1);
        
        GameCommand cmd = new BuildRoadCommand(p1, e, resourceBank);
        commandManager.executeCommand(cmd);
        
        assertNotNull(e.getRoad());
        assertEquals(0, p1.getResourceHand().getAmount(ResourceType.BRICK));
        assertEquals(0, p1.getResourceHand().getAmount(ResourceType.LUMBER));
        
        commandManager.undo();
        assertNull(e.getRoad());
        assertEquals(1, p1.getResourceHand().getAmount(ResourceType.BRICK));
        assertEquals(1, p1.getResourceHand().getAmount(ResourceType.LUMBER));
        
        commandManager.redo();
        assertNotNull(e.getRoad());
        assertEquals(0, p1.getResourceHand().getAmount(ResourceType.BRICK));
        assertEquals(0, p1.getResourceHand().getAmount(ResourceType.LUMBER));
    }

    @Test
    void testBuildSettlementUndoRedo() {
        Player p1 = players.get(0);
        Node n = board.getNode(0);
        
        p1.getResourceHand().add(ResourceType.BRICK, 1);
        p1.getResourceHand().add(ResourceType.LUMBER, 1);
        p1.getResourceHand().add(ResourceType.WOOL, 1);
        p1.getResourceHand().add(ResourceType.GRAIN, 1);
        
        int initialVP = p1.getVictoryPoints();
        
        GameCommand cmd = new BuildSettlementCommand(p1, n, resourceBank);
        commandManager.executeCommand(cmd);
        
        assertNotNull(n.getStructure());
        assertEquals(initialVP + 1, p1.getVictoryPoints());
        assertEquals(0, p1.getResourceHand().getAmount(ResourceType.BRICK));
        
        commandManager.undo();
        assertNull(n.getStructure());
        assertEquals(initialVP, p1.getVictoryPoints());
        assertEquals(1, p1.getResourceHand().getAmount(ResourceType.BRICK));
        
        commandManager.redo();
        assertNotNull(n.getStructure());
        assertEquals(initialVP + 1, p1.getVictoryPoints());
    }

    @Test
    void testBuildCityUndoRedo() {
        Player p1 = players.get(0);
        Node n = board.getNode(0);
        Settlement s = new Settlement(p1, n);
        p1.addStructure(s); // 1 VP
        
        p1.getResourceHand().add(ResourceType.ORE, 3);
        p1.getResourceHand().add(ResourceType.GRAIN, 2);
        
        int vpWithSettlement = p1.getVictoryPoints();
        
        GameCommand cmd = new BuildCityCommand(p1, n, resourceBank);
        commandManager.executeCommand(cmd);
        
        assertTrue(n.getStructure() instanceof City);
        assertEquals(vpWithSettlement + 1, p1.getVictoryPoints()); // City (+2) replaces Settlement (+1) -> net +1
        assertEquals(0, p1.getResourceHand().getAmount(ResourceType.ORE));
        
        commandManager.undo();
        assertTrue(n.getStructure() instanceof Settlement);
        assertEquals(vpWithSettlement, p1.getVictoryPoints());
        assertEquals(3, p1.getResourceHand().getAmount(ResourceType.ORE));
        
        commandManager.redo();
        assertTrue(n.getStructure() instanceof City);
        assertEquals(vpWithSettlement + 1, p1.getVictoryPoints());
    }

    @Test
    void testRedoStackClearsOnNewCommand() {
        Player p1 = players.get(0);
        Edge e0 = board.getEdge(0);
        Edge e1 = board.getEdge(1);
        
        p1.getResourceHand().add(ResourceType.BRICK, 10);
        p1.getResourceHand().add(ResourceType.LUMBER, 10);
        
        commandManager.executeCommand(new BuildRoadCommand(p1, e0, resourceBank));
        commandManager.undo();
        assertTrue(commandManager.canRedo());
        
        commandManager.executeCommand(new BuildRoadCommand(p1, e1, resourceBank));
        assertFalse(commandManager.canRedo(), "Redo stack should clear when a new command is executed");
    }
}
