package spotifycharts;

import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class EfficiencyTest {

    private List<Song> songs;

    @BeforeEach
    public void setUp() {

        int songCount = 100;

        double averageTime = 0.0;

        while (songCount < 5000000 && averageTime < 20.0) {
            ChartsCalculator calc = new ChartsCalculator(0);
            calc.registerStreamedSongs(songCount);

            SongSorter sorter = new SongSorter();

            List<Double> measuredTimes = new ArrayList<>();

            for (int i = 0; i< 10; i++) {
                final List<Song> songs = calc.getSongs();

                // start timer
                long start = System.nanoTime();

                // sort
                sorter.quickSort(songs, Song::compareByHighestStreamsCountTotal);

                // stop timer
                long end = System.nanoTime();

                long elapsedTime = end - start;
                double elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;
                measuredTimes.add(elapsedTimeInSecond);

                System.out.println("Sorting " + songCount + " songs took " + elapsedTime + " nanoseconds");
                System.gc();
            }
            averageTime = measuredTimes
                    .stream()
                    .mapToDouble(a -> a)
                    .average().orElse(0.0);

            songCount *= 2;
        }


    }

}
