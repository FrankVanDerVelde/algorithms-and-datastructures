package models;

public class Locomotive {

    private final int locNumber;
    private final int maxWagons;

    public Locomotive(int locNumber, int maxWagons) {
        if (maxWagons < 0) {
            throw new IllegalArgumentException("The maximum number of wagons must be positive");
        }
        this.locNumber = locNumber;
        this.maxWagons = maxWagons;
    }

    public int getMaxWagons() {
        return maxWagons;
    }

    public int getLocNumber() {
        return locNumber;
    }

    @Override
    public String toString() {
        return "[Loc-%d]".formatted(this.locNumber);
    }

}
