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

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
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
        int sequenceLength = 1;
        Wagon currentWagon = this;

        while (currentWagon.nextWagon != null) {
            currentWagon = currentWagon.nextWagon;
            sequenceLength++;
        }
        return sequenceLength;
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
        if (hasNextWagon()) {
            throw new IllegalStateException("%s is already pulling %s".formatted(this.toString(), this.nextWagon.toString()));
        } else if (tail.hasPreviousWagon()) {
            throw new IllegalStateException("%s has already been attached to %s".formatted(tail.toString(), tail.getPreviousWagon().toString()));
        }

//        System.out.println("Attaching " + tail + "behind " + getId());
        nextWagon = tail;
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

        nextWagon.previousWagon = null;
        nextWagon = null;

        // TODO detach the tail from this wagon (sustaining the invariant propositions).
        //  and return the head wagon of that tail

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
        // TODO detach this wagon from its predecessor (sustaining the invariant propositions).
        //   and return that predecessor

        if (!hasPreviousWagon()) return null;

        Wagon detachedFront = previousWagon;

        previousWagon.nextWagon = null;
        previousWagon = null;

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
        // TODO detach any existing connections that will be rearranged
        this.detachFront();
        front.detachTail();

        // TODO attach this wagon to its new predecessor front (sustaining the invariant propositions).
        front.attachTail(this);
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if any.
     */
    public void removeFromSequence() {
        // TODO
        // Attach this wagons tail to the wagon infront
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
        // TODO provide an iterative implementation,
        //   using attach- and detach methods of this class

        Wagon tempWagon = null;
        Wagon currentWagon = getFirstWagonAttached();

        Wagon originaLastWagon = getLastWagonAttached();

        while (currentWagon != null) {
            tempWagon = currentWagon.previousWagon;
//            attachTail();
//            detachFront();
//            detachTail();
//            wagon1.reAttachTo(wagon2);


//            currentWagon.detachFront();
            currentWagon.previousWagon = currentWagon.nextWagon;
            currentWagon.nextWagon = tempWagon;

            currentWagon = currentWagon.previousWagon;
        }

        return originaLastWagon;

    }

    @Override
    public String toString() {
        return "[Wagon-%d]".formatted(this.id);
    }

    public void showAllWagons(Wagon wagon) {

        Wagon nextWagon = wagon.getFirstWagonAttached();
        while (nextWagon != null) {
            System.out.println(nextWagon);
            nextWagon = nextWagon.getNextWagon();

        }

    }
}
