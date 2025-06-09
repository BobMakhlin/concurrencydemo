package io.bobmakhlin.unsafepublication;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;

//RESULT      SAMPLES     FREQ       EXPECT  DESCRIPTION
//      -1  145,122,233   68.13%   Acceptable  Holder not published yet.
//       0       14,256   <0.01%  Interesting  Unsafe publication: saw default value of x.
//      42   67,873,962   31.86%   Acceptable  Saw correctly published value.
//

@JCStressTest
@Description("Tests unsafe publication of a Holder object: x might be seen as 0 due to lack of synchronization.")
@Outcome(id = "0", expect = ACCEPTABLE_INTERESTING, desc = "Unsafe publication: saw default value of x.")
@Outcome(id = "42", expect = ACCEPTABLE, desc = "Saw correctly published value.")
@Outcome(id = "-1", expect = ACCEPTABLE, desc = "Holder not published yet.")
@State
public class UnsafePublicationProblem {

    private Holder h;

    @Actor
    public void writer() {
        h = new Holder(); // unsafe publish (of the reference)

        /*
        Problems:
        1. The constructor’s write to x may not be visible yet (cache latency)
        2. The reference to h may be published before (!) the field x = 42 becomes visible (memory reordering).
         */
    }

    @Actor
    public void reader(I_Result r) {
        Holder local = h; // local caching of the reference.
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
