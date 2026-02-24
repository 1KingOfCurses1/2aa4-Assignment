package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class ResourceHandTest {

    private static final int DEFAULT_TIMEOUT_MS = 2000;

    private ResourceHand hand;

    @BeforeEach
    void setup() {
        hand = new ResourceHand();
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testConstructorInitializesAllResourcesToZero() {
        assertEquals(0, hand.getBrick(), "brick starts at 0");
        assertEquals(0, hand.getLumber(), "lumber starts at 0");
        assertEquals(0, hand.getWool(), "wool starts at 0");
        assertEquals(0, hand.getGrain(), "grain starts at 0");
        assertEquals(0, hand.getOre(), "ore starts at 0");
        assertEquals(0, hand.getTotalCards(), "total cards starts at 0");
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testAddIncreasesCountAndTotalCards() {
        hand.add(ResourceType.GRAIN, 2);
        assertEquals(2, hand.getAmount(ResourceType.GRAIN), "grain after adding 2");
        assertEquals(2, hand.getTotalCards(), "total after adding 2 grain");
    }

    // Partition testing: affordable vs not-affordable are two input classes
    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testCanAffordPartitionAffordableVsNotAffordable() {
        hand.add(ResourceType.BRICK, 1);
        hand.add(ResourceType.LUMBER, 1);

        Map<ResourceType, Integer> affordable = new EnumMap<>(ResourceType.class);
        affordable.put(ResourceType.BRICK, 1);

        Map<ResourceType, Integer> notAffordable = new EnumMap<>(ResourceType.class);
        notAffordable.put(ResourceType.BRICK, 2);

        assertTrue(hand.canAfford(affordable), "should afford cost that is <= holdings");
        assertFalse(hand.canAfford(notAffordable), "should not afford cost that exceeds holdings");
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testSpendDeductsExactlyWhenAffordableOtherwiseNoChange() {
        hand.add(ResourceType.WOOL, 2);
        hand.add(ResourceType.GRAIN, 1);

        Map<ResourceType, Integer> okCost = new EnumMap<>(ResourceType.class);
        okCost.put(ResourceType.WOOL, 1);
        okCost.put(ResourceType.GRAIN, 1);

        assertTrue(hand.spend(okCost), "spend should succeed when affordable");
        assertEquals(1, hand.getAmount(ResourceType.WOOL), "wool after spending 1");
        assertEquals(0, hand.getAmount(ResourceType.GRAIN), "grain after spending 1");

        Map<ResourceType, Integer> badCost = new EnumMap<>(ResourceType.class);
        badCost.put(ResourceType.ORE, 1);

        int beforeTotal = hand.getTotalCards();
        assertFalse(hand.spend(badCost), "spend should fail when not affordable");
        assertEquals(beforeTotal, hand.getTotalCards(), "total should not change after failed spend");
    }
}