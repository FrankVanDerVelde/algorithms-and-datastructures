package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DetectionTest2 {

    Car bmw, mercedes, volvo1, daf1, daf2;

    @BeforeEach
    public void setup() {
        mercedes = new Car("VV-11-BB", 4, Car.CarType.Van, Car.FuelType.Diesel, LocalDate.of(1998, 1, 31));
        bmw = new Car("A-123-BB", 4, Car.CarType.Car, Car.FuelType.Gasoline, LocalDate.of(2019, 1, 31));
        volvo1 = new Car("1-TTT-01", 5, Car.CarType.Truck, Car.FuelType.Diesel, LocalDate.of(2009, 1, 31));
        daf1 = new Car("1-CCC-01", 5, Car.CarType.Coach, Car.FuelType.Diesel, LocalDate.of(2009, 1, 31));
        daf2 = new Car("1-CCC-02", 6, Car.CarType.Coach, Car.FuelType.Diesel, LocalDate.of(2011, 1, 31));
    }

    @Test
    public void checkPurpleViolation() {
        Detection dieselCoach5 = new Detection(daf1, "Amsterdam", LocalDateTime.now());
        Detection dieselTruck5 = new Detection(volvo1, "Amsterdam", LocalDateTime.now());

        // no violations
        Detection dieselVan = new Detection(mercedes, "Amsterdam", LocalDateTime.now());
        Detection gasolineVan = new Detection(bmw, "Amsterdam", LocalDateTime.now());
        Detection dieselCoach6 = new Detection(daf2, "Amsterdam", LocalDateTime.now());

        assertNull(dieselVan.validatePurple());
        assertNull(gasolineVan.validatePurple());
        assertNull(dieselCoach6.validatePurple());

        assertNotNull(dieselCoach5.validatePurple());
        assertNotNull(dieselTruck5.validatePurple());
    }


}
