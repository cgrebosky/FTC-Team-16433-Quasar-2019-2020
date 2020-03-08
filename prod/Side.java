package quasar.prod;

import org.jetbrains.annotations.Contract;

/**
 * This simply holds whatever side we're on.  This is critical to know for autonomous.
 */
public enum Side {
    RED, BLUE;

    /**
     * This simply swaps whatever side we're currently on.  E.g., if {this} is red, it will return blue.
     * It's functional, so no side effects - you have to update the value yourself
     * @return The opposite side from whatever we are now
     */
    @Contract(pure = true)
    public Side swap() {
        if(this == RED) return BLUE;
        return RED;
    }

    /**
     * Returns a coefficient dependent on what side we're currently on.  This is useful for autonomous,
     * because it allows us to change the way we turn, for example, without if/else structures all over
     * the code
     * @return 1 if red, -1 if blue.
     */
    @Contract(pure = true)
    public double c() {
        if(this == RED) return 1;
        else return -1;
    }
}