package nu.aron.nextbuildnumber;

import io.vavr.collection.List;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModelbuilderTest implements Modelbuilder {

    @Mock
    ModelReader modelReader;
    @Mock
    Model model;

    @Test
    void findModelsEmpty() {
        assertTrue(findModels(List.empty(), modelReader).isEmpty());
    }

    @Test
    void findModels() {
        assertNotNull(findModels(List.of(model), modelReader));
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Test
    void modelFromFile() throws IOException {
        when(modelReader.read(any(File.class), isNull())).thenReturn(model);
        assertNotNull(modelFromFile(Paths.get("src/test/resources/pom.xml").toFile(), modelReader));
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Test
    void modelFromDirectory() throws IOException {
        when(modelReader.read(any(File.class), isNull())).thenReturn(model);
        assertNotNull(modelFromFile(Paths.get("src/test/resources").toFile(), modelReader));
    }
}