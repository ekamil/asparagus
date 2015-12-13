package pl.essekkat.asparagus.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.essekkat.asparagus.TimedAsparagus;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Timed asparagus that manages it's own state in a housecleaning thread.
 * <p/>
 * Created by Kamil Essekkat on 13.12.15.
 */
public class ManagedTimedAsparagus<T> extends TimedAsparagus<T> {
    private final static Logger LOG = LoggerFactory.getLogger(ManagedTimedAsparagus.class);

    private final ScheduledExecutorService executor;
    private final Set<Consumer<Integer>> callbacks = new HashSet<>();

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

    public void registerCallback(Consumer<Integer> cons) {
        this.callbacks.add(cons);
    }

    @Override
    protected void afterPromotion(int cnt) {
        super.afterPromotion(cnt);
        for (Consumer<Integer> fn : callbacks) {
            try {
                fn.accept(cnt);
            } catch (Exception e) {
                LOG.error("Error in {} while calling {}. Turn on debug to see stacktrace.", this, fn);
                LOG.debug("", e);
            }
        }
    }
}
