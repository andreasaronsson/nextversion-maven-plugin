package nu.aron.nextbuildnumber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.model.Repository;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import io.vavr.control.Option;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NextBuildNumberMojoTest {
    NextBuildNumberMojo testee;
    @Mock
    MavenProject project;
    @Mock
    Repository repository;

    List<Repository> repositories = new LinkedList<>();

    @BeforeEach
    void setUp() {
        testee = new NextBuildNumberMojo();
    }

    @Test
    void testGetNexusUrl() {
        repositories.add(repository);
        when(project.getGroupId()).thenReturn("com.example");
        when(project.getArtifactId()).thenReturn("exampleartifact");
        when(project.getPackaging()).thenReturn("jar");
        assertThat(testee.getNexusURL(project)).isEqualTo("https://nexus.host.name:8000/nexus/service/local/artifact/maven/resolve?r=release&g=com.example&a=exampleartifact&p=jar&v=RELEASE");
    }

    @Test
    void testQueryNexusForVersion() {
        Option<String> result = testee.queryNexusForVersion(
                "https://nexus.host.name:8000/nexus/service/local/artifact/maven/resolve?r=release&g=nu.aron&a=exampleartifact&p=pom&v=RELEASE");
        result.forEach(System.out::println);
    }

    @Test
    void testFindNextNumber() {
        assertAll("",
                () -> assertEquals("2", testee.findNextNumber("1")),
                () -> assertEquals("1.2", testee.findNextNumber("1.1")),
                () -> assertEquals("1.1.2", testee.findNextNumber("1.1.1")));
    }
}
