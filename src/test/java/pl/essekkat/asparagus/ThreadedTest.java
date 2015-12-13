package pl.essekkat.asparagus;

import org.testng.annotations.Test;

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
}
