package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class PlayerAndStructureTest {

    private static final int DEFAULT_TIMEOUT_MS = 2000;

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testAddStructureIncrementsVictoryPointsByStructureVP() {
        Player p = new Player(1);
        assertEquals(0, p.getVictoryPoints(), "VP starts at 0");

        p.addStructure(new Settlement(p, new Node(10)));
        assertEquals(1, p.getVictoryPoints(), "VP after settlement");

        p.addStructure(new City(p, new Node(11)));
        assertEquals(3, p.getVictoryPoints(), "VP after adding city too");
    }

    // boundary test for needsToSpendCards: >7
    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testNeedsToSpendCardsBoundaryFalseAt7TrueAt8() {
        Player p = new Player(2);

        p.getResourceHand().add(ResourceType.BRICK, 7);
        assertFalse(p.needsToSpendCards(), "exactly 7 cards should be false");

        p.getResourceHand().add(ResourceType.BRICK, 1);
        assertTrue(p.needsToSpendCards(), "8 cards should be true");
    }
}