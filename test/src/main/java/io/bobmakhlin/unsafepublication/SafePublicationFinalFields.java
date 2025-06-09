package io.bobmakhlin.unsafepublication;


import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;

@JCStressTest
@Outcome(id = "0", expect = ACCEPTABLE_INTERESTING, desc = "Unsafe publication: saw default value of x.")
@Outcome(id = "42", expect = ACCEPTABLE, desc = "Saw correctly published value.")
@Outcome(id = "-1", expect = ACCEPTABLE, desc = "Holder not published yet.")
@State
public class SafePublicationFinalFields {

    private Holder h;

    @Actor
    public void writer() {
        h = new Holder();

        // Issue a [StoreStore] barrier after all stores (writes to fields, x in our case)
        // but before return from any constructor
        // for any class with a final field.
    }

    @Actor
    public void reader(I_Result r) {
        Holder local = h;
        if (local != null) {
            // All final fields are guaranteed to be visible with the correct values!
            r.r1 = local.x;
        } else {
            r.r1 = -1;
        }
    }

    public static class Holder {
        final int x; // FINAL!

        Holder() {
            x = 42;
        }
    }
}

