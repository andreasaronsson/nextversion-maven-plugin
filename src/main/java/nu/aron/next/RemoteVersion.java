package nu.aron.next;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Objects;

import static java.lang.String.join;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.Objects.nonNull;
import static nu.aron.next.Constants.log;

interface RemoteVersion {

    String RELEASE_OPEN = "<release>";
    String RELEASE_CLOSE = "</release>";

    default String getRemote(MavenSession session, Model model) {
        return versionFromString(xmlData(session, model))
                .onEmpty(() -> log("No previous release found for {}.", groupIdFromModel(model) + ":" + model.getArtifactId(), model.getVersion()))
                .getOrElse("");

    }

    default String xmlData(MavenSession session, Model model) {
        return List.ofAll(session.getRequest().getProjectBuildingRequest().getRemoteRepositories())
                .map(ArtifactRepository::getUrl)
                .map(u -> urlFromRepo(u, model))
                .map(this::responseToString)
                .reject(this::notFound).toCharSeq().toString();
    }

    private String urlFromRepo(String repoUrl, Model model) {
        var groupId = groupIdFromModel(model);
        return join("/", removeEnd(repoUrl, "/"), groupId.replace('.', '/'), model.getArtifactId(), "maven-metadata.xml");
    }

    private String responseToString(String url) {
        return Try.of(() -> newHttpClient().send(request(url), ofString())).get().body();
    }

    private HttpRequest request(String url) {
        String token = System.getenv("CI_JOB_TOKEN");
        if (nonNull(token)) {
            return newBuilder(URI.create(url)).header("Job-Token", token).build();
        }
        return newBuilder(URI.create(url)).build();
    }
    private boolean notFound(String s) {
        return s.contains("404 Not Found");
    }

    default String removeEnd(String str, String remove) {
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    default Option<String> versionFromString(String str) {
        return validVersionInString(str).map(s -> {
            int start = str.indexOf(RELEASE_OPEN);
            int end = str.indexOf(RELEASE_CLOSE, start + RELEASE_OPEN.length());
            return str.substring(start + RELEASE_OPEN.length(), end);
        });
    }

    default Option<String> validVersionInString(String str) {
        if (!str.contains(RELEASE_OPEN) || !str.contains(RELEASE_CLOSE)) {
            return Option.none();
        }
        return Option.of(str);
    }

    default String groupIdFromModel(Model model) {
        return Option.of(model.getGroupId()).getOrElse(() -> model.getParent().getGroupId());
    }
}
