package Hw_1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedWriter;

/**
 * BagContainer is a utility class for creating and managing bags of integers.
 * It allows generating random integers within specified bounds and writing them
 * into files.
 */
public class BagContainer {

	private Random random = new Random();

	private int size;
	private int lowerBound;
	private int upperBound;

	/**
	 * Initializes a new BagContainer with the specified size and bounds for random
	 * integers.
	 * 
	 * @param size       The size of the bag.
	 * @param lowerBound The lower bound for the random integers.
	 * @param upperBound The upper bound for the random integers.
	 */
	public BagContainer(int size, int lowerBound, int upperBound) {
		// TODO Auto-generated constructor stub
		this.size = size;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public int[] generateData(int distribution, int order) {
		int[] data = new int[size];

		// Generate data based on distribution
		switch (distribution) {
		case 1: // Uniform distribution
			for (int i = 0; i < size; i++) {
				data[i] = lowerBound + random.nextInt(upperBound - lowerBound + 1);
			}
			break;

		case 2: // Normal distribution
			for (int i = 0; i < size; i++) {
				double gaussian = (random.nextGaussian() * (upperBound - lowerBound) / 6)
						+ (upperBound + lowerBound) / 2;
				data[i] = (int) Math.round(Math.max(lowerBound, Math.min(upperBound, gaussian)));
			}
			break;
		}

		// Process data based on order
		switch (order) {
		case 1: // Sorted
			Arrays.sort(data);
			break;

		case 2: // Almost sorted
			Arrays.sort(data);
			// Introduce some randomness to make it "almost" sorted
			for (int i = 0; i < size / 10; i++) {
				int pos1 = random.nextInt(size);
				int pos2 = random.nextInt(size);
				int temp = data[pos1];
				data[pos1] = data[pos2];
				data[pos2] = temp;
			}
			break;

		default: // Not sorted
			// Already randomly distributed, no further changes needed.
			break;
		}

		return data;
	}

	/**
	 * The main method to execute the application. It prompts the user for input
	 * parameters, generates data, and times the performance of different bag
	 * implementations.
	 * 
	 * @param args The command-line arguments (not used).
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);

		System.out.println("Please enter desired size of data ");

		int size = scanner.nextInt();

		System.out.println("Please enter desired lower bound of data ");

		int lowerBound = scanner.nextInt();

		System.out.println("Please enter desired upper bound of data ");

		int upperBound = scanner.nextInt();

		System.out.println("Please enter desired type of distribution ");
		System.out.println("1: Uniform distribution , 2: Normal distribution");

		int distribution = scanner.nextInt();

		System.out.println("Please enter desired type of Order ");
		System.out.println("1: Sorted , 2: Almost sorted , 3: Random");

		int order = scanner.nextInt();

		BagContainer bagContainer = new BagContainer(size, lowerBound, upperBound);

		int[] data = bagContainer.generateData(distribution, order);

		// Instances of ArrayBag and ArrayListBag
		ArrayBag arrayBag = new ArrayBag();
		ArrayListBag arrayListBag = new ArrayListBag();

		try (BufferedWriter writerAddArrayBag = new BufferedWriter(new FileWriter("add_time_ArrayBag.csv", false));
				BufferedWriter writerAddArrayListBag = new BufferedWriter(
						new FileWriter("add_time_ArrayListBag.csv", false));
				BufferedWriter writerContainsArrayBag = new BufferedWriter(
						new FileWriter("contains_time_ArrayBag.csv", false));
				BufferedWriter writerContainsArrayListBag = new BufferedWriter(
						new FileWriter("contains_time_ArrayListBag.csv", false));
				BufferedWriter writerRemoveArrayBag = new BufferedWriter(
						new FileWriter("remove_time_ArrayBag.csv", false));
				BufferedWriter writerRemoveArrayListBag = new BufferedWriter(
						new FileWriter("remove_time_ArrayListBag.csv", false))) {

			timeAddMethod(data, arrayBag, "ArrayBag", writerAddArrayBag);
			timeAddMethod(data, arrayListBag, "ArrayListBag", writerAddArrayListBag);

			timeContainsMethod(data, arrayBag, "ArrayBag", writerContainsArrayBag);
			timeContainsMethod(data, arrayListBag, "ArrayListBag", writerContainsArrayListBag);

			timeRemoveMethod(data, arrayBag, "ArrayBag", writerRemoveArrayBag);
			timeRemoveMethod(data, arrayListBag, "ArrayListBag", writerRemoveArrayListBag);
			
		} catch (Exception e) {
			// TODO: handle exception
		} // end try catch

		// Timing the add method

	}// end main

	/**
	 * Times the performance of the add method for a given bag.
	 * 
	 * @param data    The data to be added to the bag.
	 * @param bag     The bag implementation.
	 * @param bagType The type of the bag (for reporting purposes).
	 * @param writer  The BufferedWriter to write the timing results to a CSV file.
	 * @throws IOException if an I/O error occurs.
	 */
	private static void timeAddMethod(int[] data, BagInterface bag, String bagType, BufferedWriter writer)
			throws IOException {
		long totalTime = 0;
		for (int value : data) {
			long startTime = System.nanoTime();
			bag.add(value);
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			totalTime += duration;
			writeToCSV(writer, bagType, duration);
		}
		System.out.println("Time taken for " + bagType + " add method: " + totalTime + " ns");

	}

	/**
	 * Times the performance of the contains method for a given bag.
	 * 
	 * @param data    The data to be checked for containment in the bag.
	 * @param bag     The bag implementation.
	 * @param bagType The type of the bag (for reporting purposes).
	 * @param writer  The BufferedWriter to write the timing results to a CSV file.
	 * @throws IOException if an I/O error occurs.
	 */
	private static void timeContainsMethod(int[] data, BagInterface bag, String bagType, BufferedWriter writer)
			throws IOException {

		long totalTime = 0;
		for (int value : data) {
			long startTime = System.nanoTime();
			bag.contains(value);
			long endTime = System.nanoTime();

			long duration = endTime - startTime;
			totalTime += duration;
			writeToCSV(writer, bagType, duration);
		}

		System.out.println("Time taken for " + bagType + " contains method: " + totalTime + " ns");

	}

	/**
	 * Times the performance of the remove method for a given bag.
	 * 
	 * @param data    The data to be removed from the bag.
	 * @param bag     The bag implementation.
	 * @param bagType The type of the bag (for reporting purposes).
	 * @param writer  The BufferedWriter to write the timing results to a CSV file.
	 * @throws IOException if an I/O error occurs.
	 */
	private static void timeRemoveMethod(int[] data, BagInterface bag, String bagType, BufferedWriter writer)
			throws IOException {
		long totalTime = 0;
		for (int value : data) {
			long startTime = System.nanoTime();
			bag.remove(value);
			long endTime = System.nanoTime();

			long duration = endTime - startTime;
			totalTime += duration;
			writeToCSV(writer, bagType, duration);
		}

		System.out.println("Time taken for " + bagType + " remove method: " + totalTime + " ns");

	}

	/**
	 * Writes the timing results to a CSV file.
	 * 
	 * @param writer  The BufferedWriter to write to the CSV file.
	 * @param bagType The type of the bag (for reporting purposes).
	 * @param time    The time taken for the operation.
	 * @throws IOException if an I/O error occurs.
	 */
	private static void writeToCSV(BufferedWriter writer, String bagType, long time) throws IOException {
		writer.append(bagType + ", " + time + "\n");
	}

}// end class
