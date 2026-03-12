package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.EnumMap;
import java.util.Map;

/**
 * matches report: 5 tests, partition testing, boundary testing
 */
class ResourceHandTest {

    private ResourceHand hand;

    @BeforeEach
    void setup() {
        hand = new ResourceHand();
    }

    @Test
    void testInitialCounts() {
        assertEquals(0, hand.getTotalCards());
    }

    @Test
    void testAddResources() {
        hand.add(ResourceType.GRAIN, 2);
        assertEquals(2, hand.getAmount(ResourceType.GRAIN));
    }

    @Test
    void testSpendResources() {
        hand.add(ResourceType.BRICK, 2);
        Map<ResourceType, Integer> cost = new EnumMap<>(ResourceType.class);
        cost.put(ResourceType.BRICK, 1);
        assertTrue(hand.spend(cost));
        assertEquals(1, hand.getBrick());
    }

    // Partition testing: affordable vs not-affordable as claimed in report
    @Test
    void testCanAffordPartition() {
        hand.add(ResourceType.LUMBER, 1);
        Map<ResourceType, Integer> affordable = new EnumMap<>(ResourceType.class);
        affordable.put(ResourceType.LUMBER, 1);
        assertTrue(hand.canAfford(affordable), "Should afford cost <= holdings");

        Map<ResourceType, Integer> notAffordable = new EnumMap<>(ResourceType.class);
        notAffordable.put(ResourceType.LUMBER, 2);
        assertFalse(hand.canAfford(notAffordable), "Should not afford cost > holdings");
    }

    // Boundary testing: exactly matching the cost as claimed in report
    @Test
    void testCanAffordExactBoundary() {
        hand.add(ResourceType.ORE, 3);
        Map<ResourceType, Integer> cost = new EnumMap<>(ResourceType.class);
        cost.put(ResourceType.ORE, 3);
        assertTrue(hand.canAfford(cost), "Should afford when holdings EXACTLY match cost");
    }
}
