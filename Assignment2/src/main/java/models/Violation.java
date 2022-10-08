package models;

import java.util.Comparator;

public class Violation {
    private final Car car;
    private final String city;
    private int offencesCount;

    public Violation(Car car, String city) {
        this.car = car;
        this.city = city;
        this.offencesCount = 1;
    }

//    public class DragQueenAgeAndGenderComparator implements Comparator<DragQueen> {
//        @Override
//        public int compare(DragQueen o1, DragQueen o2) {
//            int comparison = o1.getGender().compareTo(o2.getGender());
//            if (comparison == 0) {
//                return Integer.compare(o1.getAge(), o2.getAge());
//            }
//            return comparison;
//        }
//    }


    public static int compareByLicensePlateAndCity(Violation v1, Violation v2) {
        // TODO compute the ordening of v1 vs v2 as per conventions of Comparator<Violation>

        int carComparison = v1.getCar().getLicensePlate().compareTo(v2.getCar().getLicensePlate());
        if (carComparison == 0) {
            return v1.getCity().compareTo(v2.getCity());
        }

       return carComparison;   // replace by a proper outcome
    }



    /**
     * Aggregates this violation with the other violation by adding their counts and
     * nullifying identifying attributes car and/or city that do not match
     * identifying attributes that match are retained in the result.
     * This method can be used for aggregating violations applying different grouping criteria
     * @param other
     * @return  a new violation with the accumulated offencesCount and matching identifying attributes.
     */
    public Violation combineOffencesCounts(Violation other) {
        Violation combinedViolation = new Violation(
                // nullify the car attribute iff this.car does not match other.car
                this.car != null && this.car.equals(other.car) ? this.car : null,
                // nullify the city attribute iff this.city does not match other.city
                this.city != null && this.city.equals(other.city) ? this.city : null);

        // add the offences counts of both original violations
        combinedViolation.setOffencesCount(this.offencesCount + other.offencesCount);

        return combinedViolation;
    }

    public Car getCar() {
        return car;
    }

    public String getCity() {
        return city;
    }

    public int getOffencesCount() {
        return offencesCount;
    }

    public void setOffencesCount(int offencesCount) {
        this.offencesCount = offencesCount;
    }

    // TODO represent the violation in the format: licensePlate/city/offencesCount
    @Override
    public String toString() {

        return "TODO:Violation.toString";   // replace by a proper outcome
    }
}
