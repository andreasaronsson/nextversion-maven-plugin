package nu.aron.next;

import java.io.File;

import static nu.aron.next.CommandInDirectory.run;
import static nu.aron.next.Constants.GIT_BRANCH;

interface BranchName {
    default String branchName(File directory) {
        return run(directory, GIT_BRANCH);
    }
}
