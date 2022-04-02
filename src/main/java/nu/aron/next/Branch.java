package nu.aron.next;

import io.vavr.control.Try;

import java.io.File;
import java.nio.file.Files;

import static nu.aron.next.Constants.EMPTY;

interface Branch {
    default String name(File directory) {
        return Try.of(() -> Files.readString(directory.toPath().
                        resolve(".git").resolve("HEAD"))).
                getOrElseThrow(PluginException::new).replace("ref: refs/heads/", EMPTY).trim();
    }

    default String defaultName(File directory) {
        return Try.of(() -> Files.readString(directory.toPath().
                        resolve(".git").resolve("refs").resolve("remotes").resolve("origin").resolve("HEAD"))).
                getOrElseThrow(PluginException::new).replace("ref: refs/remotes/origin/", EMPTY).trim();
    }
}
