package nl.hva.ict.ads.elections.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class ExtraTest {

    private Election election;
    private PollingStation polling1;
    private PollingStation polling2;
    private PollingStation polling3;
    private PollingStation polling4;
    private PollingStation polling5;

    @BeforeEach
    public void setup() {
        this.election = new Election("test");

        Constituency constituency1 = new Constituency(1, "const1");
        Constituency constituency2 = new Constituency(2, "const2");

        this.polling1 = new PollingStation("1", "1111AA", "Polly1");
        this.polling2 = new PollingStation("2", "1111FF", "Polly2");
        this.polling3 = new PollingStation("3", "1111ZZ", "Polly3");
        this.polling4 = new PollingStation("4", "1448SL", "Polly4");
        this.polling5 = new PollingStation("5", "9999ZZ", "Polly5");

        constituency1.add(polling1);
        constituency1.add(polling2);
        constituency1.add(polling3);
        constituency2.add(polling4);
        constituency2.add(polling5);

        election.constituencies.add(constituency1);
        election.constituencies.add(constituency2);
    }

    @Test
    public void testInclusiveZipCodeSearch() {
        final Collection<PollingStation> test1 = this.election.getPollingStationsByZipCodeRange(polling1.getZipCode(), polling3.getZipCode());
        Assertions.assertEquals(3, test1.size(), "There should be 3 polling stations in the range.");

        final Collection<PollingStation> test2 = this.election.getPollingStationsByZipCodeRange(polling5.getZipCode(), polling5.getZipCode());
        Assertions.assertEquals(1, test2.size(), "There should be 1 polling stations in the range.");
    }

    @Test
    public void zipCodeValidator() {
        // testing invalid zip codes
        testInvalidZipcode("0111AA");
        testInvalidZipcode("1111SD");
        testInvalidZipcode("1111SA");
        testInvalidZipcode("1111SS");
        testInvalidZipcode("1111 AB");
        testInvalidZipcode("111111");
        testInvalidZipcode("S111PP");

        // testing valid zip codes
        testValidZipcode("1111AA");
        testValidZipcode("9999ZZ");
    }

    private void testInvalidZipcode(String zipcode) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> this.election.getPollingStationsByZipCodeRange(zipcode, "9999ZZ"),
                "First zip code should be marked as invalid."
        );

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> this.election.getPollingStationsByZipCodeRange("1000AA", zipcode),
                "Second zip code should be marked as invalid."
        );
    }

    private void testValidZipcode(String zipcode) {
        Assertions.assertDoesNotThrow(
                () -> this.election.getPollingStationsByZipCodeRange(zipcode, zipcode),
                "Both zipcodes should be marked as valid"
        );
    }

}
