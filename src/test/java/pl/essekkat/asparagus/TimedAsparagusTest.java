package pl.essekkat.asparagus;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TimedAsparagusTest {
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void wrong1() {
        new TimedAsparagus<String>(0);
    }

    @Test
    public void one() throws Exception {
        MockTimedAsparagus<String> a = new MockTimedAsparagus<>(10);
        a.millis = 0L;
        a.add("a");
        a.millis = 10L;
        a.add("b");
        a.millis = 11L;
        a.promote();
        assertThat("Only one elem", a.size(), is(1));
    }

    class MockTimedAsparagus<T> extends TimedAsparagus<T> {
        Long millis;

        public MockTimedAsparagus(int quietPeriod) {
            super(quietPeriod);
        }

        @Override
        protected Long getMillis() {
            return millis;
        }
    }
}