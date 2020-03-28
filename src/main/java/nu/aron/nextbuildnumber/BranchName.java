package nu.aron.nextbuildnumber;

import java.io.File;

import static nu.aron.nextbuildnumber.CommandInDirectory.run;
import static nu.aron.nextbuildnumber.Constants.GIT_BRANCH;

interface BranchName {
    default String branchName(File directory) {
        return run(directory, GIT_BRANCH);
    }
}
