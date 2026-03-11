package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandParserTest {

    private CommandParser parser;

    @BeforeEach
    void setup() {
        parser = new CommandParser();
    }

    @Test
    void testParseRollCommand() {
        Action a = parser.parse("roll");
        assertNotNull(a, "Should parse roll");
        assertEquals(ActionType.ROLL, a.getActionType());
        
        // Case insensitive
        a = parser.parse("RoLl");
        assertNotNull(a);
        assertEquals(ActionType.ROLL, a.getActionType());
    }

    @Test
    void testParseListCommand() {
        Action a = parser.parse("list");
        assertNotNull(a);
        assertEquals(ActionType.LIST, a.getActionType());
    }

    @Test
    void testParseBuildSettlement() {
        Action a = parser.parse("build settlement 5");
        assertNotNull(a);
        assertEquals(ActionType.BUILD_SETTLEMENT, a.getActionType());
        assertTrue(a.getDescription().contains("5"));
    }

    @Test
    void testParseBuildCity() {
        Action a = parser.parse("build city 12");
        assertNotNull(a);
        assertEquals(ActionType.BUILD_CITY, a.getActionType());
        assertTrue(a.getDescription().contains("12"));
    }

    @Test
    void testParseBuildRoad() {
        Action a = parser.parse("build road 3 7");
        assertNotNull(a);
        assertEquals(ActionType.BUILD_ROAD, a.getActionType());
        assertTrue(a.getDescription().contains("3"));
        assertTrue(a.getDescription().contains("7"));
    }

    @Test
    void testInvalidCommandsReturnNull() {
        assertNull(parser.parse("fly away"));
        assertNull(parser.parse("build spaceship"));
        assertNull(parser.parse("roll dice")); // too many words
        assertNull(parser.parse(""));
        assertNull(parser.parse(null));
    }

    @Test
    void testIsValid() {
        assertTrue(parser.isValid("roll"));
        assertTrue(parser.isValid("build settlement 1"));
        assertFalse(parser.isValid("fake command"));
    }

    @Test
    void testExtraWhitespaceIsHandled() {
        Action a = parser.parse("  build    settlement   10  ");
        assertNotNull(a);
        assertEquals(ActionType.BUILD_SETTLEMENT, a.getActionType());
    }

    @Test
    void testGoIsHandledCorrectlyAsNotGameplayAction() {
        // "go" is NOT a gameplay action, so the parser (which returns Actions) should return null.
        // The HumanAgent handles "go" internally as step-forward control.
        assertNull(parser.parse("go"), "'go' should not be parsed as a normal action");
    }
}
