package pl.essekkat.asparagus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Timed asparagus that manages it's own state in a housecleaning thread.
 * <p/>
 * Created by Kamil Essekkat on 13.12.15.
 */
public class ManagedTimedAsparagus<T> extends TimedAsparagus<T> {

    private final ScheduledExecutorService executor;

    public ManagedTimedAsparagus(int quietPeriod) {
        super(quietPeriod);

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(
                this::promote,
                0,
                quietPeriod, TimeUnit.MILLISECONDS
        );
    }

    @Override
    protected void finalize() throws Throwable {
        executor.shutdown();
        super.finalize();
    }
}
