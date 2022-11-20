package spotifycharts;

import org.junit.jupiter.api.BeforeEach;

import java.util.List;

public class EfficiencyTest {

    private List<Song> songs;

    @BeforeEach
    public void setUp() {

        int songCount = 100;

        while (songCount < 5000000) {
            ChartsCalculator calc = new ChartsCalculator(0);
            calc.registerStreamedSongs(songCount);

            SongSorter sorter = new SongSorter();

            for (int i = 0; i< 10; i++) {
                final List<Song> songs = calc.getSongs();

                long start = System.nanoTime();


                sorter.quickSort(songs, Song::compareByHighestStreamsCountTotal);
                long end = System.nanoTime();

                System.out.println("Sorting " + songCount + " songs took " + (end - start) + " nanoseconds");
                System.gc();
            }

            songCount *= 2;
        }


    }

}
