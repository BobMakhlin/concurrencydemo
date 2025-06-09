package io.bobmakhlin.unsafepublication;


import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;

@JCStressTest
@Outcome(id = "0", expect = ACCEPTABLE_INTERESTING, desc = "Unsafe publication: saw default value of x.")
@Outcome(id = "42", expect = ACCEPTABLE, desc = "Saw correctly published value.")
@Outcome(id = "-1", expect = ACCEPTABLE, desc = "Holder not published yet.")
@State
public class SafePublicationSynchronization {

    private volatile Holder h;

    @Actor
    public void writer() {
        h = new Holder(); // safe publish - volatile write

        /*
        volatile write + volatile read:
            when "thread 2" reads the volatile,
            it's guaranteed to see all the writes happened in "thread 1" before the volatile write
            - x variable write in our case
         */
    }

    @Actor
    public void reader(I_Result r) {
        Holder local = h; // volatile read
        if (local != null) {
            r.r1 = local.x;
        } else {
            r.r1 = -1;
        }
    }

    public static class Holder {
        int x;

        Holder() {
            x = 42;
        }
    }
}

