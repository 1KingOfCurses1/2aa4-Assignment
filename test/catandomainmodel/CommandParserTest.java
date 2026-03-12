package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandParserTest {

    private CommandParser parser;

    @BeforeEach
    void setup() {
        parser = new CommandParser();
    }

    @Test
    void testParseRoll() {
        Action a = parser.parse("roll");
        assertEquals(ActionType.ROLL, a.getActionType());
    }

    @Test
    void testParseGo() {
        Action a = parser.parse("go");
        assertEquals(ActionType.PASS, a.getActionType());
    }

    @Test
    void testParseList() {
        Action a = parser.parse("list");
        assertEquals(ActionType.LIST, a.getActionType());
    }

    @Test
    void testParseBuildSettlement() {
        Action a = parser.parse("build settlement 5");
        assertEquals(ActionType.BUILD_SETTLEMENT, a.getActionType());
        assertTrue(a.getDescription().contains("5"));
    }

    @Test
    void testParseBuildCity() {
        Action a = parser.parse("build city 12");
        assertEquals(ActionType.BUILD_CITY, a.getActionType());
        assertTrue(a.getDescription().contains("12"));
    }

    @Test
    void testParseBuildRoad() {
        Action a = parser.parse("build road 3 7");
        assertEquals(ActionType.BUILD_ROAD, a.getActionType());
        assertTrue(a.getDescription().contains("3"));
        assertTrue(a.getDescription().contains("7"));
    }

    @Test
    void testInvalidCommands() {
        assertNull(parser.parse("fly away"));
        assertNull(parser.parse("build spaceship"));
    }

    @Test
    void testCaseInsensitivityAndWhitespace() {
        Action a = parser.parse("  BUILD   CITY   10  ");
        assertNotNull(a);
        assertEquals(ActionType.BUILD_CITY, a.getActionType());
    }
}
