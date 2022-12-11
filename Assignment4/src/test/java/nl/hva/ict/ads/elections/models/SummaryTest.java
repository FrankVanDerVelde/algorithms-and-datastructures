package nl.hva.ict.ads.elections.models;

import nl.hva.ict.ads.utils.PathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SummaryTest {

    private static Election election;

    @BeforeAll
    public static void setup() throws XMLStreamException, IOException {
        election = Election.importFromDataFolder(PathUtils.getResourcePath("/EML_bestanden_TK2021_HvA_UvA"));
    }

    @Test
    public void arePartiesSortedCorrectly() {
        List<Party> sortedParties = election.getSortedPartiesById();

        Assertions.assertEquals(election.getParties().size(), sortedParties.size(), "The number of parties should be the same.");
        Assertions.assertEquals("VVD", sortedParties.get(0).getName(), "The first party should be VVD.");
        Assertions.assertEquals("Partij voor de Republiek", sortedParties.get(sortedParties.size() - 1).getName(), "The first party should be Partij voor de Republiek.");
    }

    @Test
    public void doDuplicateCandidateNamesMatch() {
        Set<Candidate> duplicateCandidates = election.getCandidatesWithDuplicateNames();

        Assertions.assertEquals(6, duplicateCandidates.size(), "There should be 6 duplicate candidates.");
        testCandidateNameDuplicate(duplicateCandidates, "Felix Tangelder");
        testCandidateNameDuplicate(duplicateCandidates, "Christian Kromme");
        testCandidateNameDuplicate(duplicateCandidates, "Theo Vos");
    }

    @Test
    public void areElectionResultsCorrect() {
        final List<Map.Entry<Party, Double>> results = Election.sortedElectionResultsByPartyPercentage(election.getParties().size(), election.getVotesByParty());

        System.out.println(results.get(0).getValue());

        Assertions.assertEquals("Party{id=4,name='D66'}=26.902173913043477", results.get(0).toString());
        Assertions.assertEquals("Party{id=5,name='GROENLINKS'}=13.768115942028986", results.get(1).toString());
        Assertions.assertEquals("Party{id=37,name='Partij voor de Republiek'}=0.0", results.get(results.size()-1).toString());
    }

    @Test
    public void areWibautstraatResultsCorrect() {
        final Collection<PollingStation> pollingStations = election.getPollingStationsByZipCodeRange("1091AA", "1091ZZ");
        final Map<Party, Integer> votesByPartyAcrossPollingStations = election.getVotesByPartyAcrossPollingStations(pollingStations);
        final List<Map.Entry<Party, Double>> results = Election.sortedElectionResultsByPartyPercentage(10, votesByPartyAcrossPollingStations);

        Assertions.assertEquals(10, results.size(), "There should be 10 parties in the results.");
        Assertions.assertEquals("Party{id=4,name='D66'}=26.957494407158837", results.get(0).toString());
        Assertions.assertEquals("Party{id=5,name='GROENLINKS'}=11.856823266219239", results.get(1).toString());
        Assertions.assertEquals("Party{id=13,name='Forum voor Democratie'}=2.237136465324385", results.get(results.size()-1).toString());
    }

    public void testCandidateNameDuplicate(Set<Candidate> candidates, String candidateName) {
        Assertions.assertTrue(candidates.stream().filter(c -> c.getFullName().equals(candidateName)).count() >= 2,
                "There should be at least 2 candidates with the name " + candidateName);
    }
}
