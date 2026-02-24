package catandomainmodel;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

public class ConfigurationTest {

    private static final int DEFAULT_TIMEOUT_MS = 2000;

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testDefaultMaxRounds() {
        Configuration config = new Configuration();
        assertEquals(500, config.getMaxRounds(), "Default max rounds should be 500");
    }

    @Timeout(value = DEFAULT_TIMEOUT_MS, unit = TimeUnit.MILLISECONDS)
    @Test
    void testLoadFromFileUpdatesMaxRounds(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.txt");
        Files.writeString(configFile, "turns: 100\nsome_other_key: 10");

        Configuration config = new Configuration();
        config.loadFromFile(configFile.toString());
        assertEquals(100, config.getMaxRounds(), "Max rounds should be updated from file");
    }
}
