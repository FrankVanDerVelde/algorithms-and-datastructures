package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ViolationTest {

    Car car1, car2;
    Violation violation1, violation2, violation3, violation4;

    @BeforeEach
    public void setup() {
        car1 = new Car("ABC-123", 1, Car.CarType.Truck, Car.FuelType.Diesel, LocalDate.of(2019, 1, 1));
        car2 = new Car("DEF-456", 2, Car.CarType.Coach, Car.FuelType.Diesel, LocalDate.of(2019, 1, 1));
        violation1 = new Violation(car1, "Berlin", 3);
        violation2 = new Violation(car2, "Amsterdam", 9);
        violation3 = new Violation(car2, "Rotterdam", 5);
        violation4 = new Violation(car2, "Rotterdam", 1);
    }

    @Test
    public void validateMerge() {
        Violation merged = violation1.combineOffencesCounts(violation2);
        Violation merged2 = violation2.combineOffencesCounts(violation3);
        Violation merged3 = violation3.combineOffencesCounts(violation4);

        assertNull(merged.getCar(), "The car of the merged violation should be null because the cars are different");
        assertNull(merged.getCity(), "The city of the merged violation should be null because the cities are different");
        assertEquals(merged.getOffencesCount(), violation1.getOffencesCount() + violation2.getOffencesCount());

        assertEquals(merged2.getCar(), car2, "The car should remain the same because they are equal.");
        assertEquals(merged3.getCity(), "Rotterdam", "The city should remain the same because they are equal.");
    }

    @Test
    public void validateValidationFines() {
        assertEquals(violation1.calculateRevenue(), 25 * 3, "The fine should be 75 because it is 25 per offence.");
        assertEquals(violation2.calculateRevenue(), 35 * 9, "The fine should be 315 because it is 35 per offence.");
    }

}
