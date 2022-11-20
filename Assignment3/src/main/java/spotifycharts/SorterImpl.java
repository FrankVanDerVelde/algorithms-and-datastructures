package spotifycharts;

import java.util.Comparator;
import java.util.List;

public class SorterImpl<E> implements Sorter<E> {

    /**
     * Sorts all items by selection or insertion sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     *
     * @param items
     * @param comparator
     * @return the items sorted in place
     */
    public List<E> selInsBubSort(List<E> items, Comparator<E> comparator) {
        // TODO implement selection sort or insertion sort or bubble sort

        int n = items.size();
        for (int i = 1; i < n; ++i) {
            E key = items.get(i);
            int prevIndex = i - 1;

            /* Move elements of arr[0..i-1], that are
               greater than key, to one position ahead
               of their current position */

            while (prevIndex >= 0 && comparator.compare(items.get(prevIndex), key) > 0) {
                items.set(prevIndex + 1, items.get(prevIndex));
                prevIndex = prevIndex - 1;
            }
            items.set(prevIndex + 1, key);
        }

        return items;   // replace as you find appropriate
    }

    /**
     * Sorts all items by quick sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     *
     * @param items
     * @param comparator
     * @return the items sorted in place
     */
    public List<E> quickSort(List<E> items, Comparator<E> comparator) {
        // TODO provide a recursive quickSort implementation,
        //  that is different from the example given in the lecture

        recursiveQuickSort(items, 0, items.size() - 1, comparator);

        return items;   // replace as you find appropriate
    }

    public void recursiveQuickSort(List<E> items, int start, int end, Comparator<E> comparator) {
        if (start < end) {
            int partitionIndex = partition(items, start, end, comparator);

            recursiveQuickSort(items, start, partitionIndex - 1, comparator);
            recursiveQuickSort(items, partitionIndex + 1, end, comparator);
        }
    }


    int partition(List<E> part, int start, int end, Comparator<E> comparator) {
        E pivot = part.get(end);
        int i = start - 1;

        for (int j = start; j < end; j++) {
            if (comparator.compare(part.get(j), pivot) < 0) {
                i++;

                E tempSwapElem = part.get(i);
                part.set(i, part.get(j));
                part.set(j, tempSwapElem);
            }
        }

        E tempSwapElem = part.get(i + 1);
        part.set(i + 1, part.get(end));
        part.set(end, tempSwapElem);

        return i + 1;
    }


    /**
     * Identifies the lead collection of numTops items according to the ordening criteria of comparator
     * and organizes and sorts this lead collection into the first numTops positions of the list
     * with use of (zero-based) heapSwim and heapSink operations.
     * The remaining items are kept in the tail of the list, in arbitrary order.
     * Items are sorted 'in place' without use of an auxiliary list or array or other positions in items
     *
     * @param numTops    the size of the lead collection of items to be found and sorted
     * @param items
     * @param comparator
     * @return the items list with its first numTops items sorted according to comparator
     * all other items >= any item in the lead collection
     */
    public List<E> topsHeapSort(int numTops, List<E> items, Comparator<E> comparator) {

        // the lead collection of numTops items will be organised into a (zero-based) heap structure
        // in the first numTops list positions using the reverseComparator for the heap condition.
        // that way the root of the heap will contain the worst item of the lead collection
        // which can be compared easily against other candidates from the remainder of the list
        Comparator<E> reverseComparator = comparator.reversed();

        // initialise the lead collection with the first numTops items in the list
        for (int heapSize = 2; heapSize <= numTops; heapSize++) {
            // repair the heap condition of items[0..heapSize-2] to include new item items[heapSize-1]
            heapSwim(items, heapSize, reverseComparator);
        }

        // insert remaining items into the lead collection as appropriate
        for (int i = numTops; i < items.size(); i++) {
            // loop-invariant: items[0..numTops-1] represents the current lead collection in a heap data structure
            //  the root of the heap is the currently trailing item in the lead collection,
            //  which will lose its membership if a better item is found from position i onwards
            E item = items.get(i);
            E worstLeadItem = items.get(0);
            if (comparator.compare(item, worstLeadItem) < 0) {
                // item < worstLeadItem, so shall be included in the lead collection
                items.set(0, item);
                // demote worstLeadItem back to the tail collection, at the orginal position of item
                items.set(i, worstLeadItem);
                // repair the heap condition of the lead collection
                heapSink(items, numTops, reverseComparator);
            }
        }

        // the first numTops positions of the list now contain the lead collection
        // the reverseComparator heap condition applies to this lead collection
        // now use heapSort to realise full ordening of this collection
        System.out.println();
        System.out.println(items.stream().map(s -> s instanceof Song song ? song.getTitle() : s.toString()).limit(5).toList());
        for (int i = numTops - 1; i > 0; i--) {
            // loop-invariant: items[i+1..numTops-1] contains the tail part of the sorted lead collection
            // position 0 holds the root item of a heap of size i+1 organised by reverseComparator
            // this root item is the worst item of the remaining front part of the lead collection

            System.out.println(i);

            // TODO swap item[0] and item[i];
            //  this moves item[0] to its designated position
            swap(items, 0, i);
            E temp = items.get(0);
            System.out.println("Moving: " + temp);

            // TODO the new root may have violated the heap condition
            //  repair the heap condition on the remaining heap of size i
            heapSink(items, numTops, comparator);
            System.out.println("Moved to: " + items.indexOf(temp));
        }

        return items;
    }

    private void swap(List<E> items, int i, int j) {
        E temp = items.get(i);
        items.set(i, items.get(j));
        items.set(j, temp);
    }

    /**
     * Repairs the zero-based heap condition for items[heapSize-1] on the basis of the comparator
     * all items[0..heapSize-2] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     * all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     *
     * @param items
     * @param heapSize
     * @param comparator
     */
    protected void heapSwim(List<E> items, int heapSize, Comparator<E> comparator) {
        int childi = heapSize - 1;
        int parenti = childi / 2;

        while (parenti >= 0 && comparator.compare(items.get(childi), items.get(parenti)) < 0) {
            swap(items, childi, parenti);

            childi = parenti;
            parenti = (childi - 1) / 2;
        }
    }

    /**
     * Repairs the zero-based heap condition for its root items[0] on the basis of the comparator
     * all items[1..heapSize-1] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     * all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     *
     * @param items
     * @param heapSize
     * @param comparator
     */
    protected void heapSink(List<E> items, int heapSize, Comparator<E> comparator) {
        int parenti = 0;
        int childi = 1;

        // sink the top item to its designated position, starting from index 0 using heap condition

        while (childi < heapSize) {
            // checking if there is another child to this parent, if so, determine which one is bigger.
            if (childi + 1 < heapSize && comparator.compare(items.get(childi), items.get(childi + 1)) > 0) {
                childi++;
            }
            // checking if the parent is bigger than the child, if so, swap them.
            if (comparator.compare(items.get(parenti), items.get(childi)) > 0) {
                swap(items, parenti, childi);
            } else {
                // if the parent is smaller than the child, we are done.
                break;
            }
            // update the parent and child index.
            parenti = childi;
            childi = 2 * parenti + 1;
        }

    }
}
