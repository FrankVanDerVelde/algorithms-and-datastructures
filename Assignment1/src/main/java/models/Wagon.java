package models;

public abstract class Wagon {
    protected int id;               // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
    // a.k.a. the successor of this wagon in a sequence
    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
    // a.k.a. the predecessor of this wagon in a sequence
    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon(int wagonId) {
        this.id = wagonId;
    }

    /**
     * Get the ID of this wagon.
     * @return The ID of this wagon.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the next wagon in the sequence.
     * @return The next wagon.
     */
    public Wagon getNextWagon() {
        return nextWagon;
    }

    /**
     * Get the previous wagon in the sequence.
     * @return The previous wagon.
     */
    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    /**
     * Set the next wagon in the sequence.
     * @param nextWagon the new next wagon.
     */
    public void setNextWagon(Wagon nextWagon) {
        if (nextWagon == this) {
            throw new IllegalArgumentException("Cannot attach a wagon to itself");
        }
        this.nextWagon = nextWagon;
    }

    /**
     * Set the previous wagon of the sequence.
     * @param previousWagon the new previous wagon.
     */
    public void setPreviousWagon(Wagon previousWagon) {
        if (previousWagon == this) {
            throw new IllegalArgumentException("Cannot attach a wagon to itself");
        }
        this.previousWagon = previousWagon;
    }

    /**
     * @return whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return this.nextWagon != null;
    }

    /**
     * @return whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return this.previousWagon != null;
    }

    /**
     * Returns the last wagon attached to it,
     * if there are no wagons attached to it then this wagon is the last wagon.
     *
     * @return the last wagon
     */
    public Wagon getLastWagonAttached() {
        if (nextWagon == null) return this;
        return nextWagon.getLastWagonAttached();
    }

    /**
     * Get the first wagon of this sequence.
     * @return The first wagon of this sequence.
     */
    public Wagon getFirstWagonAttached() {
        if (previousWagon == null) return this;
        return previousWagon.getFirstWagonAttached();
    }

    /**
     * @return the length of the sequence of wagons towards the end of its tail
     * including this wagon itself.
     */
    public int getSequenceLength() {
        // TODO traverse the sequence and find its length
        if (!hasNextWagon()) return 1;

        return 1 + nextWagon.getSequenceLength();
    }


    /**
     * Attaches the tail wagon and its connected successors behind this wagon,
     * if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     *
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     *                               The exception should include a message that reports the conflicting connection,
     *                               e.g.: "%s is already pulling %s"
     *                               or:   "%s has already been attached to %s"
     */
    public void attachTail(Wagon tail) {
        if (tail == this) {
            throw new IllegalArgumentException("Cannot attach a wagon to itself");
        } else if (hasNextWagon()) {
            throw new IllegalStateException("%s is already pulling %s".formatted(this.toString(), this.nextWagon.toString()));
        } else if (tail.hasPreviousWagon()) {
            throw new IllegalStateException("%s has already been attached to %s".formatted(tail.toString(), tail.getPreviousWagon().toString()));
        }

        this.nextWagon = tail;
        tail.previousWagon = this;
    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     *
     * @return the first wagon of the tail that has been detached
     * or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        if (!hasNextWagon()) return null;

        Wagon detachedTail = nextWagon;

        nextWagon.setPreviousWagon(null);
        setNextWagon(null);

        return detachedTail;
    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     *
     * @return the former previousWagon that has been detached from,
     * or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {
        if (!hasPreviousWagon()) return null;

        Wagon detachedFront = previousWagon;

        previousWagon.setNextWagon(null);
        setPreviousWagon(null);

        return detachedFront;
    }

    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon and its connected successors
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     *
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {
        if (front == this) {
            throw new IllegalArgumentException("Cannot attach a wagon to itself");
        }
        this.detachFront();
        front.detachTail();
        front.attachTail(this);
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if any.
     */
    public void removeFromSequence() {
        // Attach this wagons tail to the wagon in front
        if (hasPreviousWagon() && hasNextWagon()) {
            this.nextWagon.reAttachTo(previousWagon);
        } else {
            this.detachTail();
        }

        // Attach this wagons front to this wagons tail
        if (hasNextWagon() && hasPreviousWagon()) {
            this.previousWagon.reAttachTo(nextWagon);
        } else {
            this.detachFront();
        }
    }


    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     *
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        // Safe the original last wagon to return it
        Wagon originaLastWagon = getLastWagonAttached();

        // Detatch the wagon chain to reverse from everything in front
        Wagon detachedWagons = this.detachFront();

        // Loop over all items and reverse their previous and next wagon
        Wagon tempPreviousWagonRef = null;
        Wagon currentWagon = this;

        while (currentWagon != null) {
            tempPreviousWagonRef = currentWagon.previousWagon;

            currentWagon.previousWagon = currentWagon.nextWagon;
            currentWagon.nextWagon = tempPreviousWagonRef;

            currentWagon = currentWagon.previousWagon;
        }


        // If detatched wagons exist, re-attach the reversed sequence as a tail
        if (detachedWagons != null) detachedWagons.attachTail(this.previousWagon);

        return originaLastWagon;
    }

    @Override
    public String toString() {
        return "[Wagon-%d]".formatted(this.id);
    }

    /**
     * A debug method that prints the entire sequence of wagons from the front to the tail.
     * @param wagon the wagon to start printing from.
     */
    public static void showAllWagons(Wagon wagon) {

        Wagon nextWagon = wagon.getFirstWagonAttached();
        while (nextWagon != null) {
            System.out.println(nextWagon);
            nextWagon = nextWagon.getNextWagon();
        }

    }
}
