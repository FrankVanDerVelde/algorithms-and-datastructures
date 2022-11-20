package spotifycharts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class EfficiencyTest {

    private List<Song> songs;

    @Test
    public void setUp() {
        System.gc();

        int songCount = 100;

        double averageTime = 0.0;

        while (songCount < 5000000 && averageTime < 20.0) {
            System.gc();

            ChartsCalculator calc = new ChartsCalculator(0);
            calc.registerStreamedSongs(songCount);

            List<Song> immutableSongs = new ArrayList<>(calc.getSongs());

            SongSorter sorter = new SongSorter();

            List<Double> measuredTimes = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                System.gc();

                final List<Song> songs = new ArrayList<>(immutableSongs);

                // start timer
                long start = System.nanoTime();

                // sort
                sorter.selInsBubSort(songs, Song::compareByHighestStreamsCountTotal);

                // stop timer
                long end = System.nanoTime();

                long elapsedTime = end - start;
                double elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;
                measuredTimes.add(elapsedTimeInSecond);

                System.out.println("Sorting " + songCount + " songs took " + elapsedTimeInSecond + " nanoseconds");
            }
            averageTime = measuredTimes
                    .stream()
                    .mapToDouble(a -> a)
                    .average().orElse(0.0);

            System.out.println("----");
            System.out.println("Average time for " + songCount + " songs: " + averageTime + " seconds");
            System.out.println("----");
            songCount *= 2;
        }


    }

}
