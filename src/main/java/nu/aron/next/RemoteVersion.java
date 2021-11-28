package nu.aron.next;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;

import java.net.URI;

import static java.lang.String.join;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static nu.aron.next.Constants.log;

interface RemoteVersion {

	static final String RELEASE_OPEN = "<release>";
    static final String RELEASE_CLOSE = "</release>";

    default String getCurrent(MavenSession session, Model model) {
        return versionFromString(xmlData(session, model))
                .onEmpty(() -> log("No previous release found for {}. Will use version from pom and remove \"-SNAPSHOT\"", groupIdFromModel(model) + ":" + model.getArtifactId(), model.getVersion()))
                .getOrElse(removeEnd(model.getVersion(), "-SNAPSHOT"));

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
        var request = newBuilder(URI.create(url)).build();
        return Try.of(() -> newHttpClient().send(request, ofString())).get().body();
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
        if (str.isEmpty() || !str.contains(RELEASE_OPEN) || !str.contains(RELEASE_CLOSE)) {
            return Option.none();
        }
        return Option.of(str);
    }

    default String groupIdFromModel(Model model) {
        return Option.of(model.getGroupId()).getOrElse(() -> model.getParent().getGroupId());
    }
}
