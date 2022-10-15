package models;

import java.time.LocalDateTime;
import java.util.List;

import static models.Car.CarType;
import static models.Car.FuelType;

public class Detection {
    private final Car car;                  // the car that was detected
    private final String city;              // the name of the city where the detector was located
    private final LocalDateTime dateTime;   // date and time of the detection event

    /* Representation Invariant:
     *      every Detection shall be associated with a valid Car
     */

    public Detection(Car car, String city, LocalDateTime dateTime) {
        this.car = car;
        this.city = city;
        this.dateTime = dateTime;
    }

    /**
     * Parses detection information from a line of text about a car that has entered an environmentally controlled zone
     * of a specified city.
     * the format of the text line is: lisensePlate, city, dateTime
     * The licensePlate shall be matched with a car from the provided list.
     * If no matching car can be found, a new Car shall be instantiated with the given lisensePlate and added to the list
     * (besides the license plate number there will be no other information available about this car)
     *
     * @param textLine
     * @param cars     a list of known cars, ordered and searchable by licensePlate
     *                 (i.e. the indexOf method of the list shall only consider the lisensePlate when comparing cars)
     * @return a new Detection instance with the provided information
     * or null if the textLine is corrupt or incomplete
     */
    public static Detection fromLine(String textLine, List<Car> cars) {
        if (textLine == null) return null;
        textLine = textLine.replaceAll(" ", "");

        String[] split = textLine.split(",");
        if (split.length != 3) return null;

        Car car = new Car(split[0]);
        int index = cars.indexOf(car);

        if (index != -1) car = cars.get(index);
        else {
            cars.add(car);
        }

        return new Detection(
                car,
                split[1],
                LocalDateTime.parse(split[2])
        );
    }

    /**
     * Validates a detection against the purple conditions for entering an environmentally restricted zone
     * I.e.:
     * Diesel trucks and diesel coaches with an emission category of below 6 may not enter a purple zone
     *
     * @return a Violation instance if the detection saw an offence against the purple zone rule/
     * null if no offence was found.
     */
    public Violation validatePurple() {
        // TODO validate that diesel trucks and diesel coaches have an emission category of 6 or above
        if (car.getFuelType() == FuelType.Diesel &&
                (car.getCarType() == CarType.Truck || car.getCarType() == CarType.Coach)) {
            if (car.getEmissionCategory() < 6) {
                return new Violation(this.car, this.city);
            }
        }

        return null;
    }

    public Car getCar() {
        return car;
    }

    public String getCity() {
        return city;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }


    @Override
    public String toString() {
        return "%s/%s/%s".formatted(car.getLicensePlate(), getCity(), getDateTime());
    }

}
