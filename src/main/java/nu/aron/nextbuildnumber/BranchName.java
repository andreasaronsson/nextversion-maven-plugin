package nu.aron.nextbuildnumber;

import java.io.File;

import static nu.aron.nextbuildnumber.CommandInDirectory.run;

interface BranchName {
    default String branchName(File directory) {
        return run(directory, "git branch --show-current");
    }
}
