package nu.aron.nextbuildnumber;

import org.apache.maven.execution.MavenSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static nu.aron.nextbuildnumber.Constants.MASTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class IncrementableTest implements Incrementable {

    @Mock
    MavenSession session;

    @Test
    void newVersionMaster() {
        assertEquals("3.2.2", newVersion("3.2.1", MASTER));
        assertEquals("3.4.2", newVersion("3.4.1", MASTER));
        assertEquals("3.0.10", newVersion("3.0.9", MASTER));
        assertEquals("3.0.101", newVersion("3.0.100", MASTER));
        assertEquals("3.1", newVersion("3.0", MASTER));
        assertEquals("3", newVersion("2", MASTER));
    }

    @Test
    void newVersionBranch() {
        assertEquals("3.2.2-f-BR-1", newVersion("3.2.1", "f/BR-1"));
        assertEquals("3.4.2-f-BR-1", newVersion("3.4.1", "f/BR-1"));
        assertEquals("3.0.10-f-BR-1", newVersion("3.0.9", "f/BR-1"));
        assertEquals("3.0.101-f-BR-1", newVersion("3.0.100", "f/BR-1"));
        assertEquals("3.1-f-BR-1", newVersion("3.0", "f/BR-1"));
        assertEquals("3-f-BR-1", newVersion("2", "f/BR-1"));
    }

}