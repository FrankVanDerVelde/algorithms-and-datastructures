package models;

public class PassengerWagon extends Wagon {

    private final int numberOfSets;

    public PassengerWagon(int wagonId, int numberOfSeats) {
        super(wagonId);
        this.numberOfSets = numberOfSeats;
    }

    public int getNumberOfSeats() {
        return this.numberOfSets;
    }
}
