package io.bobmakhlin.eventualvisibility;

import org.openjdk.jcstress.annotations.*;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;

@JCStressTest(Mode.Termination)
@Outcome(id = "TERMINATED", expect = ACCEPTABLE, desc = "Gracefully finished")
@Outcome(id = "STALE", expect = ACCEPTABLE_INTERESTING, desc = "Test is stuck")
@State
public class PlainSpin {
    boolean ready; // Add volatile to fix!

    @Actor
    public void actor1() {
        // Compiler might optimise this into while(true) !!
        while (!ready) ; // spin
    }

    @Signal
    public void signal() {
        ready = true;
    }
}
