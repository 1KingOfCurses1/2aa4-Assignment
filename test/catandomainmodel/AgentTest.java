package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class AgentTest {

    private static final int DEFAULT_TIMEOUT_MS = 2000;

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testChooseRandomActionReturnsNullForNullOrEmptyList() {
        Agent a = new Agent(new Player(1));
        assertNull(a.chooseRandomAction(null), "null list should yield null");
        assertNull(a.chooseRandomAction(List.of()), "empty list should yield null");
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testRollDiceAlwaysInRange2To12OverManyRolls() {
        Agent a = new Agent(new Player(1));
        for (int i = 0; i < 5000; i++) {
            int roll = a.rollDice();
            assertTrue(roll >= 2 && roll <= 12, "roll out of range: " + roll);
        }
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testTakeTurnIncludesBuildCityOnlyWhenPlayerHasStructure() {
        Player p = new Player(7);
        Agent a = new Agent(p);

        // no structures => BUILD_CITY should not be possible
        Action chosen1 = a.takeTurn(1, new Board(List.of(), List.of(), List.of()), new ResourceBank());
        assertNotNull(chosen1, "chosen action should not be null");
        assertNotEquals("BUILD_CITY", chosen1.getDescription(),
                "BUILD_CITY should not appear when player has no structures");

        // add a settlement => BUILD_CITY may appear (not guaranteed due to randomness),
        // but we can at least check it becomes "eligible" by sampling
        p.addStructure(new Settlement(p, new Node(1)));

        boolean sawCity = false;
        for (int i = 0; i < 200; i++) {
            Action chosen = a.takeTurn(1, new Board(List.of(), List.of(), List.of()), new ResourceBank());
            if ("BUILD_CITY".equals(chosen.getDescription())) {
                sawCity = true;
                break;
            }
        }
        assertTrue(sawCity, "after owning a structure, BUILD_CITY should be possible sometimes");
    }
}