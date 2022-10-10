package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class OrderedArrayList<E>
        extends ArrayList<E>
        implements OrderedList<E> {

    protected Comparator<? super E> ordening;   // the comparator that has been used with the latest sort
    protected int nSorted;                      // the number of sorted items in the first section of the list
    // representation-invariant
    //      all items at index positions 0 <= index < nSorted have been ordered by the given ordening comparator
    //      other items at index position nSorted <= index < size() can be in any order amongst themselves
    //              and also relative to the sorted section

    public OrderedArrayList() {
        this(null);
    }

    public OrderedArrayList(Comparator<? super E> ordening) {
        super();
        this.ordening = ordening;
        this.nSorted = 0;
    }

    public Comparator<? super E> getOrdening() {
        return this.ordening;
    }

    @Override
    public void clear() {
        super.clear();
        this.nSorted = 0;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        super.sort(c);
        this.ordening = c;
        this.nSorted = this.size();
    }

    // TODO override the ArrayList.add(index, item), ArrayList.remove(index) and Collection.remove(object) methods
    //  such that they both meet the ArrayList contract of these methods (see ArrayList JavaDoc)
    //  and sustain the representation invariant of OrderedArrayList
    //  (hint: only change nSorted as required to guarantee the representation invariant,
    //   do not invoke a sort or reorder items otherwise differently than is specified by the ArrayList contract)


    @Override
    public void sort() {
        if (this.nSorted < this.size()) {
            this.sort(this.ordening);
        }
    }

    @Override
    public int indexOf(Object item) {
        // efficient search can be done only if you have provided an ordening for the list
        if (this.getOrdening() != null) {
            return indexOfByIterativeBinarySearch((E) item);
        } else {
            return super.indexOf(item);
        }
    }

    @Override
    public int indexOfByBinarySearch(E searchItem) {
        if (searchItem != null) {
            // some arbitrary choice to use the iterative or the recursive version
            return indexOfByRecursiveBinarySearch(searchItem);
        } else {
            return -1;
        }
    }

    /**
     * finds the position of the searchItem by an iterative binary search algorithm in the
     * sorted section of the arrayList, using the this.ordening comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.ordening comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for ordening items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.ordening
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByIterativeBinarySearch(E searchItem) {
        // TODO implement an iterative binary search on the sorted section of the arrayList, 0 <= index < nSorted
        //   to find the position of an item that matches searchItem (this.ordening comparator yields a 0 result)

        // the index to start searching from
        int left = 0;
        // the index to stop searching at
        int right = nSorted - 1;

        // iterating until an item has been found on the left side
        // or when it found nothing or something on the right side.
        while (left <= right) {
            // calculating the middle of the left and right
            int mid = (right + left) / 2;

            // checking if the searched item is equal, below, or higher in the list.
            // -1 is lower, 0 is equal, +1 is higher.
            final int compare = ordening.compare(searchItem, get(mid));

            if (compare == 0) {
                // they're the same item, returning.
                return mid;
            } else if (compare < 0) {
                // the searched item is on the left side of the list.
                // changing the right bounds to the middle (ignore current mid)
                right = mid - 1;
            } else {
                // the searched item is on the right side of the list.
                // changing the left bounds to the middle (ignore current mid)
                left = mid + 1;
            }
        }

        // nothing was found during binary search, moving on to linear.
        return linearIndexOf(searchItem);
    }

    /**
     * finds the position of the searchItem by a recursive binary search algorithm in the
     * sorted section of the arrayList, using the this.ordening comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.ordening comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for ordening items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.ordening
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByRecursiveBinarySearch(E searchItem) {
        // TODO implement a recursive binary search on the sorted section of the arrayList, 0 <= index < nSorted
        //   to find the position of an item that matches searchItem (this.ordening comparator yields a 0 result)

        int recursiveIndex = recursiveIndexOf(searchItem, 0, nSorted - 1);
        if (recursiveIndex != -1) return recursiveIndex;

        // nothing was found during binary search, moving on to linear.
        return linearIndexOf(searchItem);
    }

    /**
     * Search for an item linearly in the unsorted section of the list.
     *
     * @param searchItem The item to search for.
     * @return The index of the item, or -1 if not found.
     */
    private int linearIndexOf(E searchItem) {
        final int totalSize = size();
        if (nSorted < totalSize) {
            for (int i = nSorted; i < totalSize; i++) {
                if (this.ordening.compare(get(i), searchItem) == 0) return i;
            }
        }

        // returning -1 if nothing was found during linear search.
        return -1;
    }

    /**
     * Search for an item recursively between two bounds.
     *
     * @param searchItem The item to search for.
     * @param left       The left bound.
     * @param right      The right bound.
     * @return The index of the item, or -1 if not found.
     */
    private int recursiveIndexOf(E searchItem, int left, int right) {
        if (left > right) return -1;
        int mid = left + (right - left) / 2;

        // checking if the searched item is equal, below, or higher in the list.
        // -1 is lower, 0 is equal, +1 is higher.
        final int compare = ordening.compare(searchItem, get(mid));

        if (compare == 0) {
            // they're the same item, returning.
            return mid;
        } else if (compare < 0) {
            // the searched item is on the left side of the list.
            // changing the right bounds to the middle (ignore current mid)
            return recursiveIndexOf(searchItem, left, mid - 1);
        } else {
            // the searched item is on the right side of the list.
            // changing the left bounds to the middle (ignore current mid)
            return recursiveIndexOf(searchItem, mid + 1, right);
        }
    }


    /**
     * finds a match of newItem in the list and applies the merger operator with the newItem to that match
     * i.e. the found match is replaced by the outcome of the merge between the match and the newItem
     * If no match is found in the list, the newItem is added to the list.
     *
     * @param newItem
     * @param merger  a function that takes two items and returns an item that contains the merged content of
     *                the two items according to some merging rule.
     *                e.g. a merger could add the value of attribute X of the second item
     *                to attribute X of the first item and then return the first item
     * @return whether a new item was added to the list or not
     */
    @Override
    public boolean merge(E newItem, BinaryOperator<E> merger) {
        if (newItem == null) return false;
        int matchedItemIndex = this.indexOfByRecursiveBinarySearch(newItem);

        if (matchedItemIndex < 0) {
            this.add(newItem);
            return true;
        } else {
            E found = get(matchedItemIndex);
            E apply = merger.apply(found, newItem);

            this.remove(matchedItemIndex);
            this.add(matchedItemIndex, apply);
            return false;
        }
    }

    /**
     * calculates the total sum of contributions of all items in the list
     *
     * @param mapper a function that calculates the contribution of a single item
     * @return the total sum of all contributions
     */
    @Override
    public double aggregate(Function<E, Double> mapper) {
        double sum = 0.0;

        for (E item : this) {
            sum += mapper.apply(item);
        }
        return sum;
    }
}
