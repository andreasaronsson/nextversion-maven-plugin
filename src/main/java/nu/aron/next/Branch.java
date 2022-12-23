package nu.aron.next;

import io.vavr.control.Try;

import java.io.File;
import java.nio.file.Files;

import static nu.aron.next.Constants.EMPTY;

interface Branch {
    default String name(File directory) {
        String head = Try.of(() -> Files.readString(directory.toPath().
                        resolve(".git").resolve("HEAD"))).
                getOrElseThrow(PluginException::new);
        if (head.contains("ref:")) {
            return head.replace("ref: refs/heads/", EMPTY).trim();
        }
        return EMPTY;
    }

    default String defaultName(File directory) {
        return Try.of(() -> Files.readString(directory.toPath().
                        resolve(".git").resolve("refs").resolve("remotes").resolve("origin").resolve("HEAD"))).
                getOrElse("").replace("ref: refs/remotes/origin/", EMPTY).trim();
    }
}
