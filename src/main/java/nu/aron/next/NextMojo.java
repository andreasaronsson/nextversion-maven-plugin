package nu.aron.next;
import org.apache.maven.plugins.annotations.LifecyclePhase;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nu.aron.next.Constants.RUN;

/**
 * A do nothing mojo to be able to call nextversion as a goal
 */
@Mojo(name = RUN, defaultPhase = LifecyclePhase.DEPLOY)
public class NextMojo extends AbstractMojo {
    static final Logger log = LoggerFactory.getLogger(NextMojo.class);

    /**
     * (non-Javadoc)
     *
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException {
        log.info("Nextversion plugin executing.");
    }
}
