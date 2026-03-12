package catandomainmodel;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameStateExporter {

    private static final Logger LOGGER = Logger.getLogger(GameStateExporter.class.getName());
    private static final String JSON_START_BRACE = "    {";
    private static final String DESERT_LITERAL = "DESERT";

    private static final String DEFAULT_BASE_MAP_PATH = "../2aa4-2026-base/assignments/visualize/base_map.json";
    private static final String DEFAULT_STATE_PATH = "../2aa4-2026-base/assignments/visualize/state.json";

    private String baseMapPath;
    private String statePath;

    public GameStateExporter() {
        // Hardcoded to the visualizer folder as requested, using forward slashes for
        // cross-compatibility
        this.baseMapPath = DEFAULT_BASE_MAP_PATH;
        this.statePath = DEFAULT_STATE_PATH;
    }

    public GameStateExporter(String baseMapPath, String statePath) {
        this.baseMapPath = baseMapPath;
        this.statePath = statePath;
    }

    public void writeBaseMap(Board board) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"tiles\": [\n");

        List<Tile> tiles = board.getTiles();

        // Ordered exactly by natively translated Instructor IDs 0 through 18
        int[][] coords = {
                { 0, 0, 0 }, // Tile 0
                { 0, -1, 1 }, // Tile 1
                { -1, 0, 1 }, // Tile 2
                { -1, 1, 0 }, // Tile 3
                { 0, 1, -1 }, // Tile 4
                { 1, 0, -1 }, // Tile 5
                { 1, -1, 0 }, // Tile 6
                { 0, -2, 2 }, // Tile 7
                { -1, -1, 2 }, // Tile 8
                { -2, 0, 2 }, // Tile 9
                { -2, 1, 1 }, // Tile 10
                { -2, 2, 0 }, // Tile 11
                { -1, 2, -1 }, // Tile 12
                { 0, 2, -2 }, // Tile 13
                { 1, 1, -2 }, // Tile 14
                { 2, 0, -2 }, // Tile 15
                { 2, -1, -1 }, // Tile 16
                { 2, -2, 0 }, // Tile 17
                { 1, -2, 1 } // Tile 18
        };

        for (int i = 0; i < tiles.size(); i++) {
            if (i >= coords.length)
                break; // In case we have more than 19 for some reason

            Tile t = tiles.get(i);
            int[] c = coords[i];
            String res = mapResource(t.getResourceType(), t.getNumber());

            sb.append(JSON_START_BRACE)
                    .append("\"id\": ").append(t.getId()).append(", ")
                    .append("\"q\": ").append(c[0]).append(", ")
                    .append("\"s\": ").append(c[1]).append(", ")
                    .append("\"r\": ").append(c[2]).append(", ")
                    .append("\"resource\": \"").append(res).append("\", ")
                    .append("\"number\": ").append(res.equals(DESERT_LITERAL) ? 0 : t.getNumber())
                    .append("}");

            if (i < tiles.size() - 1 && i < coords.length - 1)
                sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}\n");

        try {
            try (FileWriter fw = new FileWriter(baseMapPath + ".tmp")) {
                fw.write(sb.toString());
                fw.flush();
            }
            Files.move(Paths.get(baseMapPath + ".tmp"), Paths.get(baseMapPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write base map to temp file and move: {0}", e.getMessage());
        }
    }

    public void writeState(Game game) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"round\": ").append(game.getRound()).append(",\n");

        int robberTileId = (game.getBoard().getRobber().getLocation() != null)
                ? game.getBoard().getRobber().getLocation().getId()
                : 18; // Default to desert if not set
        sb.append("  \"robber_tile\": ").append(robberTileId).append(",\n");

        writeBuildingsJson(sb, game.getPlayers());
        writeRoadsJson(sb, game.getBoard().getEdges());

        sb.append("}\n");

        try {
            try (FileWriter fw = new FileWriter(statePath + ".tmp")) {
                fw.write(sb.toString());
                fw.flush();
            }
            Files.move(Paths.get(statePath + ".tmp"), Paths.get(statePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write state to temp file and move: {0}", e.getMessage());
        }
    }

    private void writeBuildingsJson(StringBuilder sb, List<Player> players) {
        sb.append("  \"buildings\": [\n");
        boolean firstBuilding = true;
        for (Player p : players) {
            String color = mapPlayerToColor(p.getId());
            for (Structure s : p.getStructures()) {
                if (!firstBuilding)
                    sb.append(",\n");

                String typeStr = (s instanceof City) ? "CITY" : "SETTLEMENT";
                sb.append(JSON_START_BRACE)
                        .append("\"node\": ").append(s.getLocation().getId()).append(", ")
                        .append("\"owner\": \"").append(color).append("\", ")
                        .append("\"type\": \"").append(typeStr).append("\"")
                        .append("}");
                firstBuilding = false;
            }
        }
        sb.append("\n  ],\n");
    }

    private void writeRoadsJson(StringBuilder sb, List<Edge> edges) {
        sb.append("  \"roads\": [\n");
        boolean firstRoad = true;

        int[][] canonicalEdges = {
                { 0, 1 }, { 0, 5 }, { 0, 17 }, { 1, 2 }, { 1, 21 }, { 2, 3 }, { 2, 6 }, { 3, 4 }, { 3, 9 },
                { 4, 5 }, { 4, 12 }, { 5, 13 }, { 6, 7 }, { 6, 23 }, { 7, 8 }, { 7, 24 }, { 8, 9 }, { 8, 27 },
                { 9, 10 }, { 10, 11 }, { 10, 29 }, { 11, 12 }, { 11, 32 }, { 12, 14 }, { 13, 15 }, { 13, 18 },
                { 14, 15 }, { 14, 34 }, { 15, 35 }, { 16, 17 }, { 16, 18 }, { 16, 41 }, { 17, 19 }, { 18, 38 },
                { 19, 20 }, { 19, 44 }, { 20, 21 }, { 20, 47 }, { 21, 22 }, { 22, 23 }, { 22, 49 }, { 23, 52 },
                { 24, 25 }, { 24, 53 }, { 25, 26 }, { 26, 27 }, { 27, 28 }, { 28, 29 }, { 29, 30 }, { 30, 31 },
                { 31, 32 }, { 32, 33 }, { 33, 34 }, { 34, 36 }, { 35, 37 }, { 35, 39 }, { 36, 37 }, { 38, 39 },
                { 38, 42 }, { 40, 41 }, { 40, 42 }, { 41, 43 }, { 43, 44 }, { 44, 45 }, { 45, 46 }, { 46, 47 },
                { 47, 48 }, { 48, 49 }, { 49, 50 }, { 50, 51 }, { 51, 52 }, { 52, 53 }
        };

        for (Edge e : edges) {
            if (e.getRoad() != null && e.getNodes().size() == 2) {
                int nodeA = e.getNodes().get(0).getId();
                int nodeB = e.getNodes().get(1).getId();

                int minNode = Math.min(nodeA, nodeB);
                int maxNode = Math.max(nodeA, nodeB);

                boolean isValidCanonicalEdge = false;
                for (int[] canonical : canonicalEdges) {
                    if (canonical[0] == minNode && canonical[1] == maxNode) {
                        isValidCanonicalEdge = true;
                        break;
                    }
                }

                if (!isValidCanonicalEdge) {
                    LOGGER.log(Level.WARNING, "Skipping invalid road export from {0} to {1}. Edge not canonical.",
                            new Object[] { nodeA, nodeB });
                    continue;
                }

                if (!firstRoad)
                    sb.append(",\n");

                String color = mapPlayerToColor(e.getRoad().getOwner().getId());

                sb.append(JSON_START_BRACE)
                        .append("\"a\": ").append(nodeA).append(", ")
                        .append("\"b\": ").append(nodeB).append(", ")
                        .append("\"owner\": \"").append(color).append("\"")
                        .append("}");
                firstRoad = false;
            }
        }
        sb.append("\n  ]\n");
    }

    private String mapResource(ResourceType type, int number) {
        if (number == 0)
            return DESERT_LITERAL;
        switch (type) {
            case LUMBER:
                return "WOOD";
            case WOOL:
                return "SHEEP";
            case GRAIN:
                return "WHEAT";
            case BRICK:
                return "BRICK";
            case ORE:
                return "ORE";
            default:
                return DESERT_LITERAL;
        }
    }

    private String mapPlayerToColor(int playerId) {
        switch (playerId) {
            case 1:
                return "RED";
            case 2:
                return "BLUE";
            case 3:
                return "ORANGE";
            case 4:
                return "WHITE";
            default:
                return "WHITE";
        }
    }

    // Still satisfying IAgent and basic checks by providing a single write method
    // alias if needed,
    // but demonstrating properly calling the two exports in Demonstrator.
    public void write(Game game) {
        writeState(game);
    }
}
