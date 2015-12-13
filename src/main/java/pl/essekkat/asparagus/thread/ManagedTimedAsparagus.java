package pl.essekkat.asparagus.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.essekkat.asparagus.TimedAsparagus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Timed asparagus that manages it's own state in a housecleaning thread.
 * <p/>
 * Created by Kamil Essekkat on 13.12.15.
 */
public class ManagedTimedAsparagus<T> extends TimedAsparagus<T> {
    private final static Logger LOG = LoggerFactory.getLogger(ManagedTimedAsparagus.class);

    private final ScheduledExecutorService executor;

    public ManagedTimedAsparagus(int quietPeriod) {
        super(quietPeriod);

        executor = Executors.newSingleThreadScheduledExecutor();
        LOG.debug("Created executor {} for {}", executor, this);
        executor.scheduleAtFixedRate(
                this::promote,
                0,
                quietPeriod, TimeUnit.MILLISECONDS
        );
    }

    @Override
    protected void finalize() throws Throwable {
        executor.shutdown();
        LOG.debug("Shutdown of executor {} in {}", executor, this);
        super.finalize();
    }
}
