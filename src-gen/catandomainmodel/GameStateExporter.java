package catandomainmodel;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GameStateExporter {

    private String baseMapPath;
    private String statePath;

    public GameStateExporter() {
        // Hardcoded to the visualizer folder as requested, using forward slashes for cross-compatibility
        this.baseMapPath = "C:/University/Second Year Programming/2AA4/2aa4-2026-base/assignments/visualize/base_map.json";
        this.statePath = "C:/University/Second Year Programming/2AA4/2aa4-2026-base/assignments/visualize/state.json";
    }

    public GameStateExporter(String baseMapPath, String statePath) {
        this.baseMapPath = baseMapPath;
        this.statePath = statePath;
    }

    public void writeBaseMap(Board board) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"tiles\": [\n");

        List<Tile> tiles = board.getTiles();
        
        // Cube coordinates for a standard 19-tile hex layout
        int[][] coords = {
            // center
            {0, 0, 0},
            // radius 1
            {1, -1, 0}, {1, 0, -1}, {0, 1, -1}, {-1, 1, 0}, {-1, 0, 1}, {0, -1, 1},
            // radius 2
            {2, -2, 0}, {2, -1, -1}, {2, 0, -2}, {1, 1, -2}, {0, 2, -2}, {-1, 2, -1},
            {-2, 2, 0}, {-2, 1, 1}, {-2, 0, 2}, {-1, -1, 2}, {0, -2, 2}, {1, -2, 1}
        };

        for (int i = 0; i < tiles.size(); i++) {
            if (i >= coords.length) break; // In case we have more than 19 for some reason
            
            Tile t = tiles.get(i);
            int[] c = coords[i];
            String res = mapResource(t.getResourceType(), t.getNumber());
            
            sb.append("    {")
              .append("\"id\": ").append(t.getId()).append(", ")
              .append("\"q\": ").append(c[0]).append(", ")
              .append("\"s\": ").append(c[1]).append(", ")
              .append("\"r\": ").append(c[2]).append(", ")
              .append("\"resource\": \"").append(res).append("\", ")
              .append("\"number\": ").append(res.equals("DESERT") ? 0 : t.getNumber())
              .append("}");
            
            if (i < tiles.size() - 1 && i < coords.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}\n");

        try (FileWriter fw = new FileWriter(baseMapPath)) {
            fw.write(sb.toString());
            fw.flush();
        } catch (IOException e) {
            System.err.println("Failed to write base map: " + e.getMessage());
        }
    }

    public void writeState(Game game) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"round\": ").append(game.getRound()).append(",\n");
        
        // Buildings
        sb.append("  \"buildings\": [\n");
        boolean firstBuilding = true;
        for (Player p : game.getPlayers()) {
            String color = mapPlayerToColor(p.getId());
            for (Structure s : p.getStructures()) {
                if (!firstBuilding) sb.append(",\n");
                
                String typeStr = (s instanceof City) ? "CITY" : "SETTLEMENT";
                sb.append("    {")
                  .append("\"node\": ").append(s.getLocation().getId()).append(", ")
                  .append("\"owner\": \"").append(color).append("\", ")
                  .append("\"type\": \"").append(typeStr).append("\"")
                  .append("}");
                firstBuilding = false;
            }
        }
        sb.append("\n  ],\n");

        // Roads
        sb.append("  \"roads\": [\n");
        boolean firstRoad = true;
        for (Edge e : game.getBoard().getEdges()) {
            if (e.getRoad() != null && e.getNodes().size() == 2) {
                if (!firstRoad) sb.append(",\n");
                
                String color = mapPlayerToColor(e.getRoad().getOwner().getId());
                int nodeA = e.getNodes().get(0).getId();
                int nodeB = e.getNodes().get(1).getId();
                
                sb.append("    {")
                  .append("\"a\": ").append(nodeA).append(", ")
                  .append("\"b\": ").append(nodeB).append(", ")
                  .append("\"owner\": \"").append(color).append("\"")
                  .append("}");
                firstRoad = false;
            }
        }
        sb.append("\n  ]\n");
        // We will omit writing other player text specifics here since the visualizer directly renders the board layout.
        sb.append("}\n");

        try (FileWriter fw = new FileWriter(statePath)) {
            fw.write(sb.toString());
            fw.flush();
        } catch (IOException e) {
            System.err.println("Failed to write state: " + e.getMessage());
        }
    }

    private String mapResource(ResourceType type, int number) {
        if (number == 0) return "DESERT";
        switch (type) {
            case LUMBER: return "WOOD";
            case WOOL: return "SHEEP";
            case GRAIN: return "WHEAT";
            case BRICK: return "BRICK";
            case ORE: return "ORE";
            default: return "DESERT";
        }
    }

    private String mapPlayerToColor(int playerId) {
        switch (playerId) {
            case 1: return "RED";
            case 2: return "BLUE";
            case 3: return "ORANGE";
            case 4: return "WHITE";
            default: return "WHITE";
        }
    }

    // Still satisfying IAgent and basic checks by providing a single write method alias if needed, 
    // but demonstrating properly calling the two exports in Demonstrator.
    public void write(Game game) {
        writeState(game);
    }
}
