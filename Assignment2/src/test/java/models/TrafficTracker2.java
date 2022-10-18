package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrafficTracker2 {

    TrafficTracker trafficTracker, trafficTracker2;

    @BeforeEach
    public void setup() {
        trafficTracker = new TrafficTracker();
        trafficTracker2 = new TrafficTracker();

        trafficTracker.importCarsFromVault("/test2/cars.txt");
        trafficTracker.importDetectionsFromVault("/test2/detections.txt");

        trafficTracker2.importCarsFromVault("/test2/cars.txt");
        trafficTracker2.importDetectionsFromVault("/test2/detections2.txt");
    }

    @Test
    public void validateTrafficTrackerOneImports() {
        assertEquals(trafficTracker.getCars().size(), 4, "Number of cars is incorrect.");
        assertEquals(trafficTracker.getViolations().size(), 7, "Number of violations is incorrect.");
    }

    @Test
    public void validateTopViolationsByCar() {
        final List<Violation> violationsByCar = trafficTracker.topViolationsByCar(3);

        assertEquals(trafficTracker.topViolationsByCar(1).size(), 1, "Number of violations is incorrect.");
        assertEquals(violationsByCar.size(), 2, "There are only two different cars with violations so the list should be maxed to 2.");
        assertEquals(violationsByCar.get(0).getCar().getLicensePlate(), "OOP-MA-8", "The top 1 violator is incorrect.");
        assertEquals(violationsByCar.get(1).getCar().getLicensePlate(), "BBB-CC-2", "The top 2 violator is incorrect.");

        assertNull(violationsByCar.get(0).getCity(), "The city of the violation should be null.");
        assertEquals(violationsByCar.get(0).getOffencesCount(), 5, "The number of offences is incorrect.");
        assertEquals(violationsByCar.get(1).getOffencesCount(), 3, "The number of offences is incorrect.");
    }

    @Test
    public void validateTopViolationsByCity() {
        final List<Violation> violationsByCity = trafficTracker2.topViolationsByCity(3);

        assertEquals(trafficTracker2.topViolationsByCity(1).size(), 1, "Number of violations is incorrect.");
        assertEquals(violationsByCity.size(), 3, "The number of violations is incorrect.");

        assertEquals(violationsByCity.get(0).getCity(), "Amsterdam", "The top 1 violator is incorrect.");
        assertEquals(violationsByCity.get(1).getCity(), "Rotterdam", "The top 2 violator is incorrect.");
        assertEquals(violationsByCity.get(2).getCity(), "Purmerend", "The top 3 violator is incorrect.");

        assertEquals(violationsByCity.get(0).getOffencesCount(), 6, "The number of offences is incorrect.");
        assertEquals(violationsByCity.get(1).getOffencesCount(), 4, "The number of offences is incorrect.");
        assertEquals(violationsByCity.get(2).getOffencesCount(), 2, "The number of offences is incorrect.");

        assertNull(violationsByCity.get(0).getCar(), "The car of the violation should be null.");
    }

}
