package Hw_1;

/**
 * BagInterface defines the structure for a bag of integers. It declares methods
 * for adding, checking containment, checking emptiness, counting items, and
 * removing an integer from the bag.
 */
public interface BagInterface {
	/**
	 * Adds a new integer to the bag.
	 * 
	 * @param x The integer to be added to the bag.
	 */
	void add(int x);

	/**
	 * Checks if the specified integer is contained in the bag.
	 * 
	 * @param x The integer to check for containment in the bag.
	 * @return true if the integer is contained in the bag; false otherwise.
	 */
	boolean contains(int x);

	/**
	 * Checks if the bag is empty.
	 * 
	 * @return true if the bag is empty; false otherwise.
	 */
	boolean isEmpty();

	/**
	 * Retrieves the number of items currently in the bag.
	 * 
	 * @return The number of items in the bag.
	 */
	int getNumberOfItems();

	/**
	 * Removes the specified integer from the bag if it is present. If the integer
	 * is not present, the bag remains unchanged.
	 * 
	 * @param x The integer to be removed from the bag.
	 */
	void remove(int x);

}
