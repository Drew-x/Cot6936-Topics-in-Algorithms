package Hw_1;

/**
 * ArrayBag is a concrete implementation of BagInterface using an array. It
 * provides methods to manipulate a bag of integers.
 */
public class ArrayBag implements BagInterface {

	int index;
	int[] array = new int[5];

	/**
	 * Initializes a new ArrayBag with default capacity. The index is set to 0,
	 * indicating the bag is empty.
	 */
	public ArrayBag() {
		// TODO Auto-generated constructor stub
		this.index = 0;
	}

	@Override
	/**
	 * Adds a new integer to the bag. If the bag is full, it is resized to
	 * accommodate new elements.
	 * 
	 * @param x The integer to be added to the bag.
	 */
	public void add(int x) {
		if (index >= array.length) {
			int newCapacity = array.length + (array.length >> 1) + 1;
			int[] newArray = new int[newCapacity];

			for (int i = 0; i < array.length; i++) {
				newArray[i] = array[i];
			}

			this.array = newArray;

		} // end if

		array[this.index] = x;
		this.index++;

	} // add

	@Override
	/**
	 * Checks if the specified integer is contained in the bag.
	 * 
	 * @param x The integer to check for containment in the bag.
	 * @return true if the integer is contained in the bag; false otherwise.
	 */
	public boolean contains(int x) {
		for (int i = 0; i < index; i++) {
			if (array[i] == x) {
				return true;
			}
		}
		return false;
	}// end contains

	@Override
	/**
	 * Checks if the bag is empty.
	 * 
	 * @return true if the bag is empty; false otherwise.
	 */
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return (index == 0) ? true : false;
	} // isEmpty

	@Override
	/**
	 * Retrieves the number of items currently in the bag.
	 * 
	 * @return The number of items in the bag.
	 */
	public int getNumberOfItems() {
		// TODO Auto-generated method stub
		return index;
	}// end getNumberOfItems

	@Override
	/**
	 * Removes the specified integer from the bag if it is present. Shifts any
	 * subsequent elements to the left, reducing the size of the bag.
	 * 
	 * @param x The integer to be removed from the bag.
	 */
	public void remove(int x) {
		// TODO Auto-generated method stub

		for (int i = 0; i < index; i++) {
			if (array[i] == x) {

				for (int j = i; j < index - 1; j++) {
					array[j] = array[j + 1];

				} // end inner loop

				index--;
				break;
			} // end if

		} // end loop

	}// end remove

}// end class
