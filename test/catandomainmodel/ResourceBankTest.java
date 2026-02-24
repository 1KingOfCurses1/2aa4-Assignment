package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class ResourceBankTest {

    private static final int DEFAULT_TIMEOUT_MS = 2000;

    private ResourceBank bank;

    @BeforeEach
    void setup() {
        bank = new ResourceBank();
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testConstructorInitializes19OfEachResource() {
        for (ResourceType t : ResourceType.values()) {
            assertEquals(19, bank.getRemainingCount(t), "bank starts with 19 of " + t);
        }
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testHasResourceTrueWhenEnoughFalseWhenNotEnough() {
        assertTrue(bank.hasResource(ResourceType.ORE, 19), "should have exactly 19 ore initially");
        assertFalse(bank.hasResource(ResourceType.ORE, 20), "should not have more than 19 ore initially");
    }

    // Boundary testing: taking exactly the available amount (19)
    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testTakeResourceBoundaryTakeExactlyAllThenFailNext() {
        assertTrue(bank.takeResource(ResourceType.WOOL, 19), "should be able to take exactly 19 wool");
        assertEquals(0, bank.getRemainingCount(ResourceType.WOOL), "wool remaining after taking all");
        assertFalse(bank.takeResource(ResourceType.WOOL, 1), "should fail when resource is depleted");
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testReturnResourceIncreasesCount() {
        assertTrue(bank.takeResource(ResourceType.BRICK, 3), "setup: take 3 brick");
        bank.returnResource(ResourceType.BRICK, 2);
        assertEquals(18, bank.getRemainingCount(ResourceType.BRICK), "brick should be 18 after take 3 then return 2");
    }
}