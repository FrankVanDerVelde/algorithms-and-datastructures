package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class TrafficTracker {
    private final String TRAFFIC_FILE_EXTENSION = ".txt";
    private final String TRAFFIC_FILE_PATTERN = ".+\\" + TRAFFIC_FILE_EXTENSION;

    private OrderedList<Car> cars;                  // the reference list of all known Cars registered by the RDW
    private OrderedList<Violation> violations;      // the accumulation of all offences by car and by city

    public TrafficTracker() {
        this.cars = new OrderedArrayList<>(Comparator.comparing(Car::getLicensePlate));
        this.violations = new OrderedArrayList<>(Violation::compareByLicensePlateAndCity);
    }

    /**
     * imports a collection of items from a text file which provides one line for each item
     *
     * @param items     the list to which imported items shall be added
     * @param file      the source text file
     * @param converter a function that can convert a text line into a new item instance
     * @param <E>       the (generic) type of each item
     */
    public static <E> int importItemsFromFile(List<E> items, File file, Function<String, E> converter) {
        int numberOfLines = 0;

        Scanner scanner = createFileScanner(file);

        // read all source lines from the scanner,
        // convert each line to an item of type E
        // and add each successfully converted item into the list
        while (scanner.hasNext()) {
            // input another line with author information
            String line = scanner.nextLine();
            numberOfLines++;

            E convertedLine = converter.apply(line);
            items.add(convertedLine);
        }

        //System.out.printf("Imported %d lines from %s.\n", numberOfLines, file.getPath());
        return numberOfLines;
    }

    /**
     * helper method to create a scanner on a file and handle the exception
     *
     * @param file
     * @return
     */
    private static Scanner createFileScanner(File file) {
        try {
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound exception on path: " + file.getPath());
        }
    }

    private static File createFileFromURL(URL url) {
        try {
            return new File(url.toURI().getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI syntax error found on URL: " + url.getPath());
        }
    }

    /**
     * imports all registered cars from a resource file that has been provided by the RDW
     *
     * @param resourceName
     */
    public void importCarsFromVault(String resourceName) {
        this.cars.clear();

        // load all cars from the text file
        int numberOfLines = importItemsFromFile(this.cars,
                createFileFromURL(TrafficTracker.class.getResource(resourceName)),
                Car::fromLine);

        // sort the cars for efficient later retrieval
        this.cars.sort();

        System.out.printf("Imported %d cars from %d lines in %s.\n", this.cars.size(), numberOfLines, resourceName);
    }

    /**
     * imports and merges all raw detection data of all entry gates of all cities from the hierarchical file structure of the vault
     * accumulates any offences against purple rules into this.violations
     *
     * @param resourceName
     */
    public void importDetectionsFromVault(String resourceName) {
        this.violations.clear();

        int totalNumberOfOffences =
                this.mergeDetectionsFromVaultRecursively(
                        createFileFromURL(TrafficTracker.class.getResource(resourceName)));

        System.out.printf("Found %d offences among detections imported from files in %s.\n",
                totalNumberOfOffences, resourceName);
    }

    /**
     * traverses the detections vault recursively and processes every data file that it finds
     *
     * @param file
     */
    private int mergeDetectionsFromVaultRecursively(File file) {
        int totalNumberOfOffences = 0;

        if (file.isDirectory()) {
            // the file is a folder (a.k.a. directory)
            //  retrieve a list of all files and sub folders in this directory
            File[] filesInDirectory = Objects.requireNonNullElse(file.listFiles(), new File[0]);
            // TODO recursively process all files and sub folders from the filesInDirectory list.
            //  also track the total number of offences found
            for (File nestedFile : filesInDirectory) {
                totalNumberOfOffences += mergeDetectionsFromVaultRecursively(nestedFile);
            }
        } else if (file.getName().matches(TRAFFIC_FILE_PATTERN)) {
            // the file is a regular file that matches the target pattern for raw detection files
            // process the content of this file and merge the offences found into this.violations
            totalNumberOfOffences += this.mergeDetectionsFromFile(file);
        }

        return totalNumberOfOffences;
    }

    /**
     * imports another batch detection data from the filePath text file
     * and merges the offences into the earlier imported and accumulated violations
     *
     * @param file
     */
    private int mergeDetectionsFromFile(File file) {

        // re-sort the accumulated violations for efficient searching and merging
        this.violations.sort();

        // use a regular ArrayList to load the raw detection info from the file
        List<Detection> newDetections = new ArrayList<>();
        importItemsFromFile(newDetections, file, line -> Detection.fromLine(line, this.cars));

        System.out.printf("Imported %d detections from %s.\n", newDetections.size(), file.getPath());

        int totalNumberOfOffences = 0; // tracks the number of offences that emerges from the data in this file

        for (Detection detection : newDetections) {
            Violation violation = detection.validatePurple();
            if (violation != null) {
                this.violations.merge(violation, Violation::combineOffencesCounts);
                totalNumberOfOffences++;
            }
        }

        return totalNumberOfOffences;
    }

    /**
     * calculates the total revenue of fines from all violations,
     * Trucks pay €25 per offence, Coaches €35 per offence
     *
     * @return the total amount of money recovered from all violations
     */
    public double calculateTotalFines() {
        return this.violations.aggregate(Violation::calculateRevenue);
    }

    /**
     * Prepares a list of topNumber of violations that show the highest offencesCount
     * when this.violations are aggregated by car across all cities.
     *
     * @param topNumber the requested top number of violations in the result list
     * @return a list of topNum items that provides the top aggregated violations
     */
    public List<Violation> topViolationsByCar(int topNumber) {
        return topViolationsByComparing(topNumber, Comparator.comparing(Violation::getCar), this::mergeViolationsByCar, Comparator.comparingInt(Violation::getOffencesCount).reversed());
    }

    /**
     * Prepares a list of topNumber of violations that show the highest offencesCount
     * when this.violations are aggregated by city across all cars.
     *
     * @param topNumber the requested top number of violations in the result list
     * @return a list of topNum items that provides the top aggregated violations
     */
    public List<Violation> topViolationsByCity(int topNumber) {
        return topViolationsByComparing(topNumber, Comparator.comparing(Violation::getCity), this::mergeViolationsByCity, Comparator.comparingInt(Violation::getOffencesCount).reversed());
    }

    /**
     * Prepares a list of topNumber of violations based on two sorting logics and a merger.
     *
     * @param topNumber  the requested top number of violations in the result list
     * @param firstSort  the first sorting logic
     * @param merger     the merger
     * @param secondSort the second sorting logic
     * @return a list of topNum items that provides the top aggregated violations
     */
    private List<Violation> topViolationsByComparing(int topNumber, Comparator<Violation> firstSort, BinaryOperator<Violation> merger, Comparator<Violation> secondSort) {
        OrderedArrayList<Violation> newViolations = new OrderedArrayList<>(firstSort);

        for (Violation violation : this.violations) {
            newViolations.merge(violation, merger);
        }

        newViolations.sort(secondSort);
        return newViolations.subList(0, topNumber);
    }

    /**
     * Merges two violations if they apply to the same car.
     *
     * @param v1 the first violation
     * @param v2 the second violation
     * @return A modified copy of the first violation when not the same car, otherwise a new violation with the combined offences counts.
     */
    private Violation mergeViolationsByCar(Violation v1, Violation v2) {
        boolean sameCar = v1.getCar() != null && v1.getCar().equals(v2.getCar());
        if (!sameCar) return new Violation(v1.getCar(), null, v1.getOffencesCount());

        return new Violation(v1.getCar(), null, v1.getOffencesCount() + v2.getOffencesCount());
    }

    /**
     * Merges two violations if they apply to the same city.
     *
     * @param v1 the first violation
     * @param v2 the second violation
     * @return A modified copy of the first violation when not from the same city, otherwise a new violation with the combined offences counts.
     */
    private Violation mergeViolationsByCity(Violation v1, Violation v2) {
        boolean sameCar = v1.getCity() != null && v1.getCity().equals(v2.getCity());
        if (!sameCar) return new Violation(null, v1.getCity(), v1.getOffencesCount());

        return new Violation(null, v1.getCity(), v1.getOffencesCount() + v2.getOffencesCount());
    }

    public OrderedList<Car> getCars() {
        return this.cars;
    }

    public OrderedList<Violation> getViolations() {
        return this.violations;
    }
}
