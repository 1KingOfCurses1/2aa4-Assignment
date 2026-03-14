package catandomainmodel;

import java.util.HashMap;
import java.util.Map;

/**
 * Command to build a settlement on a node.
 */
public class BuildSettlementCommand implements GameCommand {
    private Player player;
    private Node node;
    private ResourceBank resourceBank;
    private Settlement settlement;
    private static final Map<ResourceType, Integer> COST = new HashMap<>();

    static {
        COST.put(ResourceType.BRICK, 1);
        COST.put(ResourceType.LUMBER, 1);
        COST.put(ResourceType.WOOL, 1);
        COST.put(ResourceType.GRAIN, 1);
    }

    public BuildSettlementCommand(Player player, Node node, ResourceBank resourceBank) {
        this.player = player;
        this.node = node;
        this.resourceBank = resourceBank;
    }

    @Override
    public void execute() {
        // Deduct resources
        player.getResourceHand().spend(COST);
        for (Map.Entry<ResourceType, Integer> entry : COST.entrySet()) {
            resourceBank.returnResource(entry.getKey(), entry.getValue());
        }

        // Place settlement
        this.settlement = new Settlement(player, node);
        player.addStructure(this.settlement);
        // Settlement constructor doesn't set node structure, 
        // but Player.addStructure(s) does: s.getLocation().setStructure(s)
    }

    @Override
    public void undo() {
        // Remove settlement
        player.removeStructure(this.settlement);

        // Restore resources
        for (Map.Entry<ResourceType, Integer> entry : COST.entrySet()) {
            player.getResourceHand().add(entry.getKey(), entry.getValue());
            resourceBank.takeResource(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String describe() {
        return "Build Settlement at node " + node.getId();
    }
}
