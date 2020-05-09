package nu.aron.next;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Properties;

import static nu.aron.next.Constants.log;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivatorTest implements Activator {

    @Mock
    MavenSession session;
    @Mock
    MavenExecutionRequest executionRequest;
    Properties userProperties = new Properties();

    @BeforeEach
    void setUp() {
        when(session.getUserProperties()).thenReturn(userProperties);
        log("Start");
    }

    @Test
    void skipped() {
        userProperties.put("next.skip", "true");
        assertFalse(activated(session));
    }

    @Test
    void activeByNuAronNextRun() {
        when(session.getRequest()).thenReturn(executionRequest);
        when(executionRequest.getGoals()).thenReturn(List.of("nu.aron:next:run"));
        assertTrue(activated(session));
    }

    @Test
    void activeByNextVersion() {
        when(session.getRequest()).thenReturn(executionRequest);
        when(executionRequest.getGoals()).thenReturn(List.of("nextversion"));
        assertTrue(activated(session));
    }

    @Test
    void activeByNextversionMavenPlugin() {
        when(session.getRequest()).thenReturn(executionRequest);
        when(executionRequest.getGoals()).thenReturn(List.of("nextversion-maven-plugin"));
        assertTrue(activated(session));
    }

    @Test
    void activeByNextversionAndRun() {
        when(session.getRequest()).thenReturn(executionRequest);
        when(executionRequest.getGoals()).thenReturn(List.of("nextversion-maven-plugin:run"));
        assertTrue(activated(session));
    }

    @Test
    void activeByNextversionFQCN() {
        when(session.getRequest()).thenReturn(executionRequest);
        when(executionRequest.getGoals()).thenReturn(List.of("nu.aron:nextversion-maven-plugin:run"));
        assertTrue(activated(session));
    }

}