package nu.aron.next;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest
    @ValueSource( strings =  {"nu.aron:next:run", "nextversion", "nextversion-maven-plugin", "nextversion-maven-plugin:run", "nu.aron:nextversion-maven-plugin:run"})
    void activeBy(String goal) {
        when(session.getRequest()).thenReturn(executionRequest);
        when(executionRequest.getGoals()).thenReturn(List.of(goal));
        assertTrue(activated(session));
    }
}