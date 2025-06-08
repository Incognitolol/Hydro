package integral.studios.hydro.util.math;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Tuple<A, B> {
    public A one;
    public B two;

    public Tuple(A one, B two) {
        this.one = one;
        this.two = two;
    }
}