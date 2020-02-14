package nu.aron.nextbuildnumber;

import io.vavr.collection.List;
import io.vavr.control.Option;
import kong.unirest.Unirest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;

import static java.lang.String.join;
import static nu.aron.nextbuildnumber.Constants.log;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.substringBetween;

interface RemoteVersion {

    default String getCurrent(MavenSession session, Model model) {
        var xmlData = xmlData(session, model);
        return versionFromString(xmlData)
                .onEmpty(() -> log("No previous release found for {}. Will use version from pom and remove \"-SNAPSHOT\"", Option.of(model.getGroupId()).getOrElse(model.getParent().getGroupId()) + ":" + model.getArtifactId(), model.getVersion()))
                .getOrElse(removeEnd(model.getVersion(), "-SNAPSHOT"));

    }

    default String xmlData(MavenSession session, Model model) {
        return List.ofAll(session.getRequest().getProjectBuildingRequest().getRemoteRepositories())
                .map(ar -> ar.getUrl())
                .map(u -> urlFromRepo(u, model))
                .map(u -> Unirest.get(u).asString().getBody())
                .reject(s -> s.contains("404 Not Found")).toCharSeq().toString();
    }

    private Option<String> versionFromString(String data) {
        return Option.of(substringBetween(data, "<release>", "</release>"));
    }

    private String urlFromRepo(String repoUrl, Model model) {
        var groupId = Option.of(model.getGroupId()).getOrElse(() -> model.getParent().getGroupId());
        return join("/", removeEnd(repoUrl, "/"), groupId.replace('.', '/'), model.getArtifactId(), "maven-metadata.xml");
    }


}
