package catandomainmodel;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
                ResourceHandTest.class,
                ResourceBankTest.class,
                AgentTest.class,
                PlayerAndStructureTest.class,
                EdgeAndActionTest.class,
                BoardTest.class,
                ConfigurationTest.class,
                CommandParserTest.class
})
public class AllTestsSuite {
}