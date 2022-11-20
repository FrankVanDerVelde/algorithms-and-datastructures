package spotifycharts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class SongRankingTest {

    @Test
    public void testSongComparing() {
        ChartsCalculator chartsCalculator = new ChartsCalculator(2);
        chartsCalculator.registerStreamedSongs(2);

        // 2 songs
        final List<Song> songs = chartsCalculator.getSongs();

        Song song1 = songs.get(0);
        Song song2 = songs.get(1);

        Assertions.assertEquals(2, songs.size(), "There should be 2 songs");
        Assertions.assertEquals(0, song1.compareForDutchNationalChart(song1), "Song 1 should be equal to itself");
        Assertions.assertEquals(0, song1.compareByHighestStreamsCountTotal(song1), "Song 1 should be equal to itself");

        Assertions.assertEquals(song1.compareForDutchNationalChart(song2),
                -song2.compareForDutchNationalChart(song1), "Reverse comparing should yield same result but negative");
    }

    /**
     * This was created because we ran into problems where non-Dutch songs could end up before Dutch songs because of their stream count
     */
    @Test
    public void testDutchSongComparing() {
        Song spanish = new Song("Frankita", "Latina Goddess", Song.Language.SP);
        spanish.setStreamsCountOfCountry(Song.Country.NL, 200);

        Song english = new Song("Milly Bobby Brown", "Stranger Thangs", Song.Language.EN);
        english.setStreamsCountOfCountry(Song.Country.NL, 300);

        Song dutch = new Song("Bob", "Dutchy", Song.Language.NL);
        dutch.setStreamsCountOfCountry(Song.Country.NL, 100);

        final int comparison = spanish.compareForDutchNationalChart(dutch);

        Assertions.assertTrue(comparison > 0, "Dutch song should be ranked higher than Spanish song " +
                "despite having less streams because it is meant for the Dutch chart");

        final List<Song> songs = new java.util.ArrayList<>(List.of(spanish, english, dutch));
        Collections.shuffle(songs);

        SongSorter sorter = new SongSorter();
        final List<Song> sortedSongs = sorter.quickSort(songs, Song::compareForDutchNationalChart);

        Assertions.assertSame(sortedSongs.get(0), dutch, "Dutch song should be the highest ranked " +
                "song because it's for dutch charts.");
        Assertions.assertSame(sortedSongs.get(1), english, "English song should be second.");
        Assertions.assertSame(sortedSongs.get(2), spanish, "Spanish song should be third.");
    }

    @Test
    public void testStreamCountsSorting() {
        Song spanish = new Song("Frankita", "Latina Goddess", Song.Language.SP);
        spanish.setStreamsCountOfCountry(Song.Country.NL, 200);

        Song english = new Song("Milly Bobby Brown", "Stranger Thangs", Song.Language.EN);
        english.setStreamsCountOfCountry(Song.Country.NL, 300);

        Song dutch = new Song("Bob", "Dutchy", Song.Language.NL);
        dutch.setStreamsCountOfCountry(Song.Country.NL, 100);

        final List<Song> songs = new java.util.ArrayList<>(List.of(spanish, english, dutch));
        Collections.shuffle(songs);

        SongSorter sorter = new SongSorter();
        final List<Song> sortedSongs = sorter.quickSort(songs, Song::compareByHighestStreamsCountTotal);

        Assertions.assertSame(sortedSongs.get(0), english, "Frankita has the most streams.");
        Assertions.assertSame(sortedSongs.get(1), spanish, "MBB has the second most streams.");
        Assertions.assertSame(sortedSongs.get(2), dutch, "Bob has the least streams.");
    }
}
