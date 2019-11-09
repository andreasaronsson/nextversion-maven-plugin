package nu.aron.nextbuildnumber;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IncrementableTest implements Incrementable {

    @Test
    void newVersion() {
        assertEquals("3.2.2", newVersion("3.2.1"));
        assertEquals("3.4.2", newVersion("3.4.1"));
        assertEquals("3.0.10", newVersion("3.0.9"));
        assertEquals("3.0.101", newVersion("3.0.100"));
    }
}