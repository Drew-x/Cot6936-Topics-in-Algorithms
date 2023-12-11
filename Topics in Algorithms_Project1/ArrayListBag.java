package Hw_1;

import java.util.ArrayList;

public class ArrayListBag implements BagInterface {

	ArrayList<Integer> arrayList = new ArrayList<Integer>();

	@Override
	/**
	 * Adds a new integer to the bag.
	 * 
	 * @param x The integer to be added to the bag.
	 */
	public void add(int x) {
		// TODO Auto-generated method stub
		arrayList.add(x);
	}

	@Override
	/**
	 * Checks if the specified integer is contained in the bag.
	 * 
	 * @param x The integer to check for containment in the bag.
	 * @return true if the integer is contained in the bag; false otherwise.
	 */
	public boolean contains(int x) {
		// TODO Auto-generated method stub
		return arrayList.contains(x);
	}

	@Override
	/**
	 * Checks if the bag is empty.
	 * 
	 * @return true if the bag is empty; false otherwise.
	 */
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return arrayList.isEmpty();
	}

	@Override
	/**
	 * Retrieves the number of items currently in the bag.
	 * 
	 * @return The number of items in the bag.
	 */
	public int getNumberOfItems() {
		// TODO Auto-generated method stub
		return arrayList.size();
	}

	@Override
	/**
	 * Removes the specified integer from the bag if it is present. If the integer
	 * is not present, the bag remains unchanged.
	 * 
	 * @param x The integer to be removed from the bag.
	 */
	public void remove(int x) {
		// TODO Auto-generated method stub
		arrayList.remove(new Integer(x));
	}

	@Override
	public String toString() {
		return "ArrayListBag [arrayList=" + arrayList + "]";
	}

}
