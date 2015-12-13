package pl.essekkat.asparagus.thread;

import org.testng.annotations.Test;
import pl.essekkat.asparagus.Asparagus;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ThreadedTest {

    @Test
    public void one() throws InterruptedException {
        Asparagus<String> a = new ManagedTimedAsparagus<>(50);
        a.add("a");
        a.add("b");
        Thread.sleep(20);
        a.add("c");
        assertThat("None promoted", a.pop(), is(Optional.empty()));
        Thread.sleep(101);
        assertThat("All available", a.size(), is(3));
    }

    @Test
    public void two() throws InterruptedException {
        Asparagus<String> a = new ManagedTimedAsparagus<>(50);
        a.add("a");
        a.add("b");
        Thread.sleep(52);
        a.add("c");
        assertThat("All available", a.size(), is(2));
    }
}
