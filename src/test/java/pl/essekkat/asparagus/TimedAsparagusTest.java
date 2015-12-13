package pl.essekkat.asparagus;

import org.testng.annotations.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
        a.promote();
        assertThat("Only one elem", a.size(), is(1));
        a.millis = 20L;
        a.promote();
        assertThat("Two elems", a.size(), is(2));
        a.add("b");
        a.millis = 40L;
        a.promote();
        assertThat("Ignored double", a.size(), is(2));
    }

    @Test
    public void testPop() throws Exception {
        MockTimedAsparagus<String> a = new MockTimedAsparagus<>(10);
        a.millis = 0L;
        a.add("a");
        a.add("b");
        a.add("c");

        assertThat("None promoted", a.pop(), is(Optional.empty()));

        a.millis = 11L;
        a.promote();

        assertThat("3 elements", a.pop(), is(not(Optional.empty())));
        assertThat("2 left", a.size(), is(2));

        a.add("d");
        a.add("f");
        a.millis = 21L;
        a.promote();

        assertThat("2 more added", a.size(), is(4));
        assertThat("can pop all 4", a.pop(4), hasSize(4));
        assertThat("nothing is left", a.size(), is(0));
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