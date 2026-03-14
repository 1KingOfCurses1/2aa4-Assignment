package catandomainmodel;

import java.util.HashMap;
import java.util.Map;

/**
 * Command to build a city (upgrade from settlement) on a node.
 */
public class BuildCityCommand implements GameCommand {
    private Player player;
    private Node node;
    private ResourceBank resourceBank;
    private City city;
    private Structure previousStructure;
    private static final Map<ResourceType, Integer> COST = new HashMap<>();

    static {
        COST.put(ResourceType.ORE, 3);
        COST.put(ResourceType.GRAIN, 2);
    }

    public BuildCityCommand(Player player, Node node, ResourceBank resourceBank) {
        this.player = player;
        this.node = node;
        this.resourceBank = resourceBank;
        this.previousStructure = node.getStructure();
    }

    @Override
    public void execute() {
        // Deduct resources
        player.getResourceHand().spend(COST);
        for (Map.Entry<ResourceType, Integer> entry : COST.entrySet()) {
            resourceBank.returnResource(entry.getKey(), entry.getValue());
        }

        // Remove old structure from player
        if (previousStructure != null) {
            player.removeStructure(previousStructure);
        }

        // Place city
        this.city = new City(player, node);
        player.addStructure(this.city);
    }

    @Override
    public void undo() {
        // Remove city
        player.removeStructure(this.city);

        // Restore settlement
        if (previousStructure != null) {
            player.addStructure(previousStructure);
        }

        // Restore resources
        for (Map.Entry<ResourceType, Integer> entry : COST.entrySet()) {
            player.getResourceHand().add(entry.getKey(), entry.getValue());
            resourceBank.takeResource(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String describe() {
        return "Build City at node " + node.getId();
    }
}
