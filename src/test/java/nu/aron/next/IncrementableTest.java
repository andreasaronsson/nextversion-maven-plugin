package nu.aron.next;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static nu.aron.next.Constants.EMPTY;
import static nu.aron.next.Constants.MAIN;
import static nu.aron.next.Constants.MASTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class IncrementableTest implements Incrementable {

    @Test
    void newVersionMaster() {
        assertEquals("3.2.2", newVersion("3.2.1", MASTER, MASTER, 1));
        assertEquals("3.4.2", newVersion("3.4.1", MASTER, MASTER, 1));
        assertEquals("3.0.10", newVersion("3.0.9", MASTER, MASTER, 1));
        assertEquals("3.0.101", newVersion("3.0.100", MASTER, MASTER, 1));
        assertEquals("3.1", newVersion("3.0", MASTER, MASTER, 1));
        assertEquals("3", newVersion("2", MASTER, MASTER, 1));
    }
    @Test
    void newVersionMain() {
        assertEquals("3.2.2", newVersion("3.2.1", MAIN, MAIN, 1));
        assertEquals("3.4.2", newVersion("3.4.1", MAIN, MAIN, 1));
        assertEquals("3.0.10", newVersion("3.0.9", MAIN, MAIN, 1));
        assertEquals("3.0.101", newVersion("3.0.100", MAIN, MAIN, 1));
        assertEquals("3.1", newVersion("3.0", MAIN, MAIN, 1));
        assertEquals("3", newVersion("2", MAIN, MAIN, 1));
        assertEquals("1", newVersion("1-SNAPSHOT", MAIN, "", 1));
        assertEquals("2", newVersion("2-SNAPSHOT", MAIN, MAIN, 1));
    }

    @Test
    void newVersionMasterWhenDefaultIsMain() {
        assertEquals("3.2.2-master", newVersion("3.2.1", MASTER, "main", 1));
    }


    @Test
    void newVersionBranch() {
        assertEquals("3.2.2-f-BR-1", newVersion("3.2.1", "f/BR-1", MASTER, 1));
        assertEquals("3.4.2-f-BR-1", newVersion("3.4.1", "f/BR-1", MASTER, 1));
        assertEquals("3.0.10-f-BR-1", newVersion("3.0.9", "f/BR-1", MASTER, 1));
        assertEquals("3.0.101-f-BR-1", newVersion("3.0.100", "f/BR-1", MASTER, 1));
        assertEquals("3.1-f-BR-1", newVersion("3.0", "f/BR-1", MASTER, 1));
        assertEquals("3-f-BR-1", newVersion("2", "f/BR-1", MASTER, 1));
    }

    @Test
    void countDots() {
        assertEquals(1, countDots("."));
        assertEquals(3, countDots("..."));
        assertEquals(1, countDots("1.2"));
        assertEquals(3, countDots("1.3.4."));
        assertEquals(2, countDots("1..2"));
        assertEquals(2, countDots(".1."));
        assertEquals(30, countDots(".1.1.1.1.1.1.1..........1.1.1.1.1.2.2.2.2.2...."));
        assertEquals(0, countDots(""));
    }

    @Test
    void manuallyBumped() {
        assertEquals("5.0.0", manuallyBumped("1.1.0-SNAPSHOT", "5.0.0")._1);
        assertEquals("1.1.0", manuallyBumped("1.1.0-SNAPSHOT", "1.0.555")._1);
        assertEquals("1.3.555", manuallyBumped("1.1.0-SNAPSHOT", "1.3.555")._1);
        assertEquals("1.1.0", manuallyBumped("1.1.0", "1.0.555")._1);
        assertEquals("1.0.555", manuallyBumped("1.0.555-FEATURE-JIRA-123", "1.0.555")._1);
    }

    @Test
    void newVersionManual() {
        assertEquals("3", newVersion("3-SNAPSHOT", EMPTY, EMPTY, 0));
        assertEquals("22", newVersion("21", EMPTY, EMPTY, 1));
    }
}