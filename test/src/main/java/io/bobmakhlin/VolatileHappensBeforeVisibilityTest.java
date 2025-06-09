package io.bobmakhlin;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Description("Triggers memory reordering")
@Outcome(id = "-1", expect = Expect.ACCEPTABLE, desc = "")
@Outcome(id = "11", expect = Expect.ACCEPTABLE, desc = "Returned correct value")
public class VolatileHappensBeforeVisibilityTest {
    @Actor
    public final void actor1(DataHolder dataHolder) {
        dataHolder.writer();
    }

    @Actor
    public final void actor2(DataHolder dataHolder, I_Result r) {
        r.r1 = dataHolder.reader();
    }

    @State
    public static class DataHolder {
        private int x;
        private int y;
        private volatile boolean initialized = false;

        public void writer() {
            x = 5;
            y = 6;
            initialized = true; // Happens before (the next reading)
        }

        public int reader() {
            if (initialized) { // Reading
                // All changes to shared vars (x, y) made by thread A before writing the volatile var,
                // are visible to the thread B
                // so we are guaranteed to have x = 5 y = 6 at this point
                return x + y;
            }
            return -1;
        }

//        public int readerX() {
//            if (initialized) { // Reading
//                // All changes to shared vars (x, y) made by thread A before writing the volatile var,
//                // are visible to the thread B
//                // so we are guaranteed to have x = 5 y = 6 at this point
//                return x;
//            }
//            return -1;
//        }
//
//        public int readerY() {
//            if (initialized) { // Reading
//                // All changes to shared vars (x, y) made by thread A before writing the volatile var,
//                // are visible to the thread B
//                // so we are guaranteed to have x = 5 y = 6 at this point
//                return y;
//            }
//            return -1;
//        }
    }
}
