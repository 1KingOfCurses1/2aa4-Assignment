package catandomainmodel;

import java.util.HashMap;
import java.util.Map;

/**
 * Command to build a road on an edge.
 */
public class BuildRoadCommand implements GameCommand {
    private Player player;
    private Edge edge;
    private ResourceBank resourceBank;
    private Road road;
    private static final Map<ResourceType, Integer> COST = new HashMap<>();

    static {
        COST.put(ResourceType.BRICK, 1);
        COST.put(ResourceType.LUMBER, 1);
    }

    public BuildRoadCommand(Player player, Edge edge, ResourceBank resourceBank) {
        this.player = player;
        this.edge = edge;
        this.resourceBank = resourceBank;
    }

    @Override
    public void execute() {
        // Deduct resources
        player.getResourceHand().spend(COST);
        for (Map.Entry<ResourceType, Integer> entry : COST.entrySet()) {
            resourceBank.returnResource(entry.getKey(), entry.getValue());
        }

        // Place road
        this.road = new Road(player, edge);
        edge.setRoad(this.road);
    }

    @Override
    public void undo() {
        // Remove road
        edge.setRoad(null);

        // Restore resources
        for (Map.Entry<ResourceType, Integer> entry : COST.entrySet()) {
            player.getResourceHand().add(entry.getKey(), entry.getValue());
            resourceBank.takeResource(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String describe() {
        return "Build Road at " + edge.getId();
    }
}
