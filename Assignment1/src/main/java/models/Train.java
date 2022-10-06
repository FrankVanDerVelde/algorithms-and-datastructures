package models;

public class Train {
    private final String origin;
    private final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        if (engine == null) {
            throw new IllegalArgumentException("The engine must not be null.");
        }

        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /**
     * Get whether this train has any waggons attached to it.
     * @return True if this train has any wagons, false otherwise.
     */
    public boolean hasWagons() {
        return firstWagon != null;
    }

    /**
     * Get whether this train is passenger train.
     *
     * @return True if this train is a passenger train, false otherwise.
     */
    public boolean isPassengerTrain() {
        return firstWagon instanceof PassengerWagon;
    }

    /**
     * Get whether this train is a freight train.
     *
     * @return True if this train is a freight train, false otherwise.
     */
    public boolean isFreightTrain() {
        return firstWagon instanceof FreightWagon;
    }

    /**
     * Get the locomotive of this train.
     *
     * @return the locomotive
     */
    public Locomotive getEngine() {
        return engine;
    }

    /**
     * Get the origin of the train.
     *
     * @return the origin of the train.
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Get the destination of this train.
     *
     * @return The destination.
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Get the first wagon of the train.
     *
     * @return the first wagon of the train.
     */
    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * (sustaining all representation invariants)
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     *              (can be null)
     */
    public void setFirstWagon(Wagon wagon) {
        this.firstWagon = wagon;
    }

    /**
     * @return the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        if (firstWagon == null) return 0;

        int count = 1;
        Wagon wagon = firstWagon;
        while (((wagon = wagon.getNextWagon()) != null)) {
            count++;
        }

        return count;
    }

    /**
     * @return the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        if (firstWagon == null) return null;

        Wagon wagon = firstWagon;
        while (wagon != null && wagon.hasNextWagon()) {
            wagon = wagon.getNextWagon();
        }
        return wagon;
    }

    /**
     * @return the total number of seats on a passenger train
     * (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        if (!(firstWagon instanceof PassengerWagon firstPassengerWagon)) return 0;

        Wagon nextWagon = firstWagon;
        int seats = firstPassengerWagon.getNumberOfSeats();
        while ((nextWagon = nextWagon.getNextWagon()) != null) {
            seats += ((PassengerWagon) nextWagon).getNumberOfSeats();
        }

        return seats;
    }

    /**
     * calculates the total maximum weight of a freight train
     *
     * @return the total maximum weight of a freight train
     * (return 0 for a passenger train)
     */
    public int getTotalMaxWeight() {
        if (!(firstWagon instanceof FreightWagon firstFreightWagon)) return 0;

        Wagon nextWagon = firstWagon;
        int weight = firstFreightWagon.getMaxWeight();
        while ((nextWagon = nextWagon.getNextWagon()) != null) {
            weight += ((FreightWagon) nextWagon).getMaxWeight();
        }

        return weight;
    }

    /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     *
     * @param position the position of the wagon to be found
     * @return the wagon found at the given position
     * (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        if (position < 1) return null;

        int i = 1;
        Wagon nextWagon = firstWagon;
        while (nextWagon != null) {
            if (i == position) return nextWagon;
            nextWagon = nextWagon.getNextWagon();
            i++;
        }

        return null;
    }

    /**
     * Finds the wagon with a given wagonId
     *
     * @param wagonId the wagonId of the wagon to be found
     * @return the wagon found
     * (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon nextWagon = firstWagon;
        while (nextWagon != null) {
            if (nextWagon.getId() == wagonId) return nextWagon;
            nextWagon = nextWagon.getNextWagon();
        }
        return null;
    }

    /**
     * Determines if the given sequence of wagons can be attached to this train
     * Verifies if the type of wagons match the type of train (Passenger or Freight)
     * Verifies that the capacity of the engine is sufficient to also pull the additional wagons
     * Verifies that the wagon is not part of the train already
     * Ignores the predecessors before the head wagon, if any
     *
     * @param wagon the head wagon of a sequence of wagons to consider for attachment
     * @return whether type and capacity of this train can accommodate attachment of the sequence
     */
    public boolean canAttach(Wagon wagon) {
        if (wagon == null) return false;
        // checking if the wagon matches the type of the train
        if (firstWagon == null || (isPassengerTrain() && wagon instanceof PassengerWagon) ||
                (isFreightTrain() && wagon instanceof FreightWagon)) {
            // checking if adding this wagon won't exceed the max wagon limit
            if (getNumberOfWagons() + wagon.getSequenceLength() <= this.engine.getMaxWagons()) {
                // checking if the train doesn't already has this wagon.
                return (findWagonById(wagon.getId()) == null);
            }
            // continue
        }
        return false;
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if attachment is possible, the head wagon is first detached from its predecessors, if any
     *
     * @param wagon the head wagon of a sequence of wagons to be attached
     * @return whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {
        if (!canAttach(wagon)) return false;

        Wagon nextWagon = wagon;
        while (nextWagon != null) {
            // getting the next wagon of this current wagon.
            Wagon originalNextWagon = nextWagon.getNextWagon();
            // detaching the tail from the current wagon.
            nextWagon.detachTail();
            // detaching the front from the current wagon.
            nextWagon.detachFront();
            if (hasWagons()) {
                // if this train has wagons, attach the nextWagon to the end of the train (end of first wagon of this train).
                firstWagon.getLastWagonAttached().attachTail(nextWagon);
            } else {
                // if this train does not have any wagons, setting the first wagon to the next wagon.
                firstWagon = nextWagon;
            }
            nextWagon = originalNextWagon;
        }
        return true;
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * (the front is at position one, before the current first wagon, if any)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if insertion is possible, the head wagon is first detached from its predecessors, if any
     *
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {
        if (!canAttach(wagon)) return false;

        if (firstWagon == null) {
            wagon.detachFront();
            firstWagon = wagon;
            return true;
        }

        wagon.detachFront();

        Wagon originalFirstWagon = firstWagon;
        firstWagon = wagon;

        Wagon lastWaggonOfSequence = wagon.getLastWagonAttached();
        lastWaggonOfSequence.setNextWagon(originalFirstWagon);
        originalFirstWagon.setPreviousWagon(lastWaggonOfSequence);
        return true;
    }

    /**
     * Tries to insert the given sequence of wagons at/before the given position in the train.
     * (The current wagon at given position including all its successors shall then be reattached
     * after the last wagon of the given sequence.)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity
     * or the given position is not valid for insertion into this train)
     * if insertion is possible, the head wagon of the sequence is first detached from its predecessors, if any
     *
     * @param position the position where the head wagon and its successors shall be inserted
     *                 1 <= position <= numWagons + 1
     *                 (i.e. insertion immediately after the last wagon is also possible)
     * @param wagon    the head wagon of a sequence of wagons to be inserted
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {
        final int numberOfWagons = getNumberOfWagons();
        if (!canAttach(wagon) || position < 1 || position > numberOfWagons + 1) return false;

        if (position == 1) return insertAtFront(wagon);
        else if (position == numberOfWagons + 1) return attachToRear(wagon);

        wagon.detachFront();

        if (numberOfWagons == 0) {
            firstWagon = wagon;
            return true;
        }

        final Wagon wagonAtPosition = findWagonAtPosition(position);
        if (wagonAtPosition != null && wagonAtPosition.hasPreviousWagon()) {
            final Wagon previousWagon = wagonAtPosition.getPreviousWagon();
            previousWagon.setNextWagon(wagon);
            wagon.setPreviousWagon(previousWagon);
        }

        Wagon lastSequenceOfWagon = wagon.getLastWagonAttached();
        lastSequenceOfWagon.setNextWagon(wagonAtPosition);
        if (wagonAtPosition != null) wagonAtPosition.setPreviousWagon(lastSequenceOfWagon);

        return true;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param wagonId the id of the wagon to be removed
     * @param toTrain the train to which the wagon shall be attached
     *                toTrain shall be different from this train
     * @return whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        if (toTrain == null) return false;
        Wagon wagon = findWagonById(wagonId);
        if (wagon == null) return false;

        if (equals(toTrain) || !toTrain.canAttach(wagon)) return false;

        Wagon previous = wagon.getPreviousWagon();
        Wagon next = wagon.getNextWagon();

        wagon.setNextWagon(null);
        wagon.setPreviousWagon(null);

        // current wagon is somewhere in the middle of the train.
        if (previous != null && next != null) {
            previous.setNextWagon(next);
            next.setPreviousWagon(previous);
        }
        // current wagon is the first wagon of the train.
        else if (previous == null && next != null) {
            next.setPreviousWagon(null);
            firstWagon = next;
        }
        // current wagon is the last wagon of the train
        else if (previous != null) {
            previous.setNextWagon(null);
        }

        toTrain.attachToRear(wagon);
        return true;
    }

    /**
     * Tries to split this train before the wagon at given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param position 1 <= position <= numWagons
     * @param toTrain  the train to which the split sequence shall be attached
     *                 toTrain shall be different from this train
     * @return whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        if (toTrain == null || position < 1 || position > getNumberOfWagons() + 1) return false;

        Wagon wagonAtPosition = findWagonAtPosition(position);

        if (!toTrain.canAttach(wagonAtPosition)) return false;

        if (position == 1) {
            setFirstWagon(null);
        }

        wagonAtPosition.detachFront();

        toTrain.attachToRear(wagonAtPosition);
        return true;
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     * the previous wagon of the last wagon becomes the second wagon
     * etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        // reverse the train so the first wagon becomes the last wagon and everything in between
        final int numberOfWagons = getNumberOfWagons();
        if (numberOfWagons < 2) return;

        Wagon prev = null;
        Wagon current = firstWagon;
        Wagon next;
        // looping until the next wagon is null, which means we are at the last wagon
        while (current != null) {
            // before changing the next wagon of the current wagon, store the next wagon.
            next = current.getNextWagon();
            // switching up the next and previous wagons of the current wagon
            current.setNextWagon(prev);
            current.setPreviousWagon(next);
            // move the current wagon one place forward.
            prev = current;
            current = next;
        }
        firstWagon = prev;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Wagon nextWagon = firstWagon;
        while (nextWagon != null) {
            sb.append(nextWagon);
            nextWagon = nextWagon.getNextWagon();
        }

        sb.append(" with ")
                .append(getNumberOfWagons())
                .append(" wagons from ")
                .append(getOrigin())
                .append(" to ")
                .append(getDestination());
        return sb.toString();
    }
}
