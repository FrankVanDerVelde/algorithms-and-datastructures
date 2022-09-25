package models;

public class FreightWagon extends Wagon {

    private final int maxWeight;

    public FreightWagon(int wagonId, int maxWeight) {
        super(wagonId);
        if (maxWeight < 0) {
            throw new IllegalArgumentException("The weight of the train must be positive");
        }
        this.maxWeight = maxWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }
}
