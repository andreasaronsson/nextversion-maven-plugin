package nu.aron.nextbuildnumber;

import io.vavr.collection.List;
import io.vavr.control.Try;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;

import java.io.File;
import java.nio.file.Paths;

import static java.util.function.Function.identity;

interface Modelbuilder {
    default List<Model> findModels(List<Model> models, ModelReader modelReader) {
        if (models.isEmpty()) {
            return models;
        }
        return models.appendAll(findModels(models.map(this::fromModel).flatMap(identity()).map(f -> modelFromFile(f, modelReader)), modelReader));
    }

    default Model modelFromFile(File file, ModelReader modelReader) {
        if (file.isDirectory()) {
            return modelFromPom(file.toPath().resolve("pom.xml").toFile(), modelReader);
        }
        return modelFromPom(file, modelReader);
    }

    private List<File> fromModel(Model m) {
        return List.ofAll(m.getModules()).map(module -> Paths.get(m.getProjectDirectory().toString(), module).toFile());
    }

    private Model modelFromPom(File pom, ModelReader modelReader) {
        return Try.of(() -> modelReader.read(pom, null)).getOrElseThrow(PluginException::new);
    }
}
