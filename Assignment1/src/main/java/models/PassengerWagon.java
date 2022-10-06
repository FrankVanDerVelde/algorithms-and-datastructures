package models;

public class PassengerWagon extends Wagon {

    private final int numberOfSets;

    public PassengerWagon(int wagonId, int numberOfSeats) {
        super(wagonId);
        if (numberOfSeats < 0) {
            throw new IllegalArgumentException("Number of seats must be positive");
        }
        this.numberOfSets = numberOfSeats;
    }

    public int getNumberOfSeats() {
        return this.numberOfSets;
    }
}
