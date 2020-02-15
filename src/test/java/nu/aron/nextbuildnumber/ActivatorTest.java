package nu.aron.nextbuildnumber;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Properties;

import static nu.aron.nextbuildnumber.Constants.log;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivatorTest implements Activator {

    @Mock
    MavenSession session;
    @Mock
    MavenExecutionRequest executionRequest;
    @Mock
    GetEnvPretender getEnvPretender;
    Properties userProperties = new Properties();

    @BeforeEach
    void setUp() {
        when(session.getUserProperties()).thenReturn(userProperties);
        log("Start");
    }

    @Test
    void skipped() {
        userProperties.put("next.skip", "true");
        assertFalse(activated(session, getEnvPretender));
    }

    @Test
    void localBuildNoRelease() {
        when(session.getRequest()).thenReturn(executionRequest);
        when(executionRequest.getGoals()).thenReturn(List.of("verify"));
        assertFalse(activated(session, getEnvPretender));
    }

    @Test
    void localBuildRelease() {
        when(session.getRequest()).thenReturn(executionRequest);
        when(executionRequest.getGoals()).thenReturn(List.of("deploy"));
        assertTrue(activated(session, getEnvPretender));
    }

    @Test
    void ciBuildNoRelease() {
        when(getEnvPretender.getenv("BRANCH_NAME")).thenReturn("master");
        when(session.getRequest()).thenReturn(executionRequest);
        when(executionRequest.getGoals()).thenReturn(List.of("deploy:deploy", "jar:jar"));
        assertFalse(activated(session, getEnvPretender));
    }

    @Test
    void ciBuildRelease() {
        when(session.getRequest()).thenReturn(executionRequest);
        lenient().when(getEnvPretender.getenv("CI")).thenReturn("");
        assertTrue(activated(session, getEnvPretender));
    }
}