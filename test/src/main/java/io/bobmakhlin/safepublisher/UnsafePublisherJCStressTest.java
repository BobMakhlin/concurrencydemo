package io.bobmakhlin.safepublisher;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.III_Result;
import org.openjdk.jcstress.infra.results.II_Result;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Description("Triggers memory reordering")
@Outcome(id = {"-1, -1", "-1, 6", "5, -1", "0, -1", "-1, 0"}, expect = Expect.ACCEPTABLE, desc = "Not initialized yet")
@Outcome(id = "5, 6", expect = Expect.ACCEPTABLE, desc = "Returned correct value")
@Outcome(id = {"0, 0", "0, 6", "5, 0"}, expect = Expect.ACCEPTABLE_INTERESTING, desc = "Initialized but returned default value")
public class UnsafePublisherJCStressTest {
    @Actor
    public final void actor1(DataHolder dataHolder) {
        dataHolder.writer();
    }

    @Actor
    public final void actor2(DataHolder dataHolder, II_Result r) {
        r.r1 = dataHolder.readerX();
        r.r2 = dataHolder.readerY();
    }

    @State
    public static class DataHolder {
        private int x;
        private int y;
        private boolean initialized = false;

        public void writer() {
            // Might be reordered,
            // or there might be a cache latency.
            x = 5;
            y = 6;
            initialized = true;
        }

        public int readerX() {
            if (initialized) {
                return x;
            }
            return -1; // return mock value if not initialized
        }

        public int readerY() {
            if (initialized) {
                return y;
            }
            return -1; // return mock value if not initialized
        }
    }
}
