package spotifycharts;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public class SongSortingEfficiencyMeasuring {

    private final static int REPEATS = 10;
    private final static int SONG_COUNT = 100;
    private final static int HEAP_NUM_TOPS = 10;
    private static final int MAX_DURATION = 20;
    private static final Comparator<Song> COMPARATOR = Song::compareByHighestStreamsCountTotal;

    public static void main(String[] args) {
        System.out.println("Measuring time for sorting " + SONG_COUNT + " songs " + REPEATS + " times to calculate averages.");

        SongSorter songSorter = new SongSorter();

        System.out.println("=== Insertion sort");
        measureTime(songSorter::selInsBubSort);

        System.out.println("=== Quick Sort");
        measureTime(songSorter::quickSort);

        System.out.println("=== Heap Sort");
        measureHeapTime(songSorter);
    }

    private static void measureTime(BiFunction<List<Song>, Comparator<Song>, List<Song>> sorterMethod) {
        ChartsCalculator chartsCalculator = new ChartsCalculator(0);

        int songs = SONG_COUNT;
        int iteration = 1;

        while (songs <= 5000000) {
            System.out.printf("Testing with %d songs (#%d)%n", songs, iteration);

            songs *= 2;
            double averageDuration = 0;

            for (int j = 1; j <= REPEATS; j++) {
                setupSongList(chartsCalculator, songs);
                Collections.shuffle(chartsCalculator.getSongs());

                System.gc();
                long started = System.nanoTime();
                sorterMethod.apply(chartsCalculator.getSongs(), COMPARATOR);

                long duration = System.nanoTime() - started;
                averageDuration += duration;

//                System.out.printf("  #%d: Took %d ns (%.4f s)%n", j, duration, getSecondsFromNano(duration));
            }
            averageDuration = averageDuration / REPEATS;
            final double averageInSeconds = getSecondsFromNano(averageDuration);

            System.out.printf("-- Average duration: %.0f ns (%.4f s)%n%n", averageDuration, averageInSeconds);

            if (averageInSeconds > MAX_DURATION) {
                System.out.printf("Program stopped, duration %.0f was longer than %d sec %n%n", averageDuration, MAX_DURATION);
                return;
            }
            iteration++;
        }
    }

    private static void measureHeapTime(SongSorter sorterMethod) {
        ChartsCalculator chartsCalculator = new ChartsCalculator(0);

        int songs = SONG_COUNT;
        int iteration = 1;

        while (true) {
            System.out.printf("Testing with %d songs (#%d)%n", songs, iteration);
            songs *= 2;
            double averageDuration = 0;

            for (int j = 1; j <= REPEATS; j++) {
                setupSongList(chartsCalculator, songs);
                Collections.shuffle(chartsCalculator.getSongs());

                System.gc();

                long started = System.nanoTime();
                sorterMethod.topsHeapSort(HEAP_NUM_TOPS, chartsCalculator.getSongs(), COMPARATOR);

                long duration = System.nanoTime() - started;
                averageDuration += duration;

//                System.out.printf("  #%d: Took %d ns (%.4f s)%n", j, duration, getSecondsFromNano(duration));
            }

            averageDuration = averageDuration / REPEATS;
            final double averageInSeconds = getSecondsFromNano(averageDuration);

            System.out.printf("-- Average duration: %.0f ns (%.4f s)%n%n", averageDuration, averageInSeconds);

            if (averageInSeconds > MAX_DURATION) {
                System.out.printf("Program stopped, duration %.0f was longer than %d sec %n%n", averageDuration, MAX_DURATION);
                return;
            }
            iteration++;
        }
    }

    private static double getSecondsFromNano(double nano) {
        // convert nano to seconds
        return nano / 1000000000;
    }

    private static void setupSongList(ChartsCalculator chartsCalculator, int songAmount) {
        chartsCalculator.getSongs().clear();
        chartsCalculator.registerStreamedSongs(songAmount);
    }

}
