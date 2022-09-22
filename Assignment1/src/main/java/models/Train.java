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
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /* three helper methods that are usefull in other methods */
    public boolean hasWagons() {
        return firstWagon != null;
    }

    public boolean isPassengerTrain() {
        return firstWagon instanceof PassengerWagon;
    }

    public boolean isFreightTrain() {
        return firstWagon instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

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
            System.out.println(wagon);
            wagon = wagon.getNextWagon();
        }
//
//        while (((wagon = wagon.getNextWagon()) != null)) {
//            if (wagon.getNextWagon() == null) return wagon;
//        }
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
     * @param position
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
     * @param wagonId
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
        // TODO
        if (!canAttach(wagon)) return false;

        if (firstWagon == null) {
            wagon.detachFront();
            firstWagon = wagon;
            return true;
        }

        wagon.detachFront();
        Wagon lastWaggonOfSequence = wagon.getLastWagonAttached();
        lastWaggonOfSequence.setNextWagon(firstWagon);
        firstWagon.setPreviousWagon(wagon);

        firstWagon = wagon;
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
        // TODO

        return false;
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
        // TODO

        return false;
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
        // TODO

        return false;
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     * the previous wagon of the last wagon becomes the second wagon
     * etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        // TODO

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Wagon nextWagon = firstWagon;
        do {
            sb.append(nextWagon.toString());
            nextWagon = nextWagon.getNextWagon();
        }
        while (nextWagon != null);

        sb.append(" with ")
                .append(getNumberOfWagons())
                .append(" wagons from ")
                .append(getOrigin())
                .append(" to ")
                .append(getDestination());
        return sb.toString();
    }
}
