package nu.aron.nextbuildnumber;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class CommandInDirectoryTest {

    @Test
    void runTest() {
        assertNotNull(CommandInDirectory.run(Paths.get(".").toFile(), "ls"));
    }
}