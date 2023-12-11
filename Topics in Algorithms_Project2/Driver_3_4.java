package pro_Hw4gra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

// drawing
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ID: 5852258 
 * Andrew Salazar
 */


public class Driver_3_4 {

	//Globals 
	static Random random = new Random();
	static int[][] matrix;
	static HashMap<String, Double> graph = new HashMap<>();
	static ArrayList<String> path = new ArrayList<>();

	// Local search - variables 
		// Min-Heap
	static PriorityQueue<Double> minHeap = new PriorityQueue<>();
		// Found Paths
	static HashMap<Double, Set<String>> localPaths = new HashMap<>();
		// BestDistance at iteration
	static LinkedList<Double> localBD = new LinkedList<>();

	
	// Local search Simulated Annealing variables 
		// Min-Heap
	static PriorityQueue<Double> minHeap_SA = new PriorityQueue<>();
		// Found Paths
	static HashMap<Double, Set<String>> localPaths_SA = new HashMap<>();
		// BestDistance at iteration
	static LinkedList<Double> localBD_SA = new LinkedList<>();

	
	// Genetic variables 
		//Generations
	static HashMap<Integer, Set<ArrayList<String>>> population = new HashMap<>();
	static int generationCounter = 0;
	static PriorityQueue<Double> geneticMinHeap = new PriorityQueue<>();
		// Found Paths
	static HashMap<Double, Set<String>> geneticPaths = new HashMap<>();
		// BestDistance at iteration
	static LinkedList<Double> geneticBD = new LinkedList<>();

	
	public static void main(String[] args) {
// TODO Auto-generated method stub
		System.out.println("Driver_3_4");

		System.out.println("Grabbing input... ");
		grabInput();
		graph = calculateAllDistances(matrix);
		System.out.println("Done");

		// Picking a random path
		shuffle2DArray(matrix);
		createPath();

		System.out.println("\nUsing Input File 1");
		System.out.println("---------------------------------------------------------------");
		localSearch_2opt(path);
		System.out.println("Local Search Algorithm ");
		System.out.println("Shortest Path: " + localPaths.get(minHeap.peek()));
		System.out.println("Shortest Path Distance: " + minHeap.peek());
		drawPoints(localPaths.get(minHeap.peek()),"Local Search " );
		
		System.out.println("---------------------------------------------------------------");
		localSearch_2opt_SA(path);
		System.out.println("Local Search Algorithm with Simulated Annealing ");
		System.out.println("Shortest Path: " + localPaths_SA.get(minHeap_SA.peek()));
		System.out.println("Shortest Path Distance: " + minHeap_SA.peek());
		drawPoints(localPaths_SA.get(minHeap_SA.peek()),"Local Search Simulated Annealing" );
		
		System.out.println("---------------------------------------------------------------");
		geneticSearch(path, 10, 100);
		System.out.println("Genetic Algorithm ");
		System.out.println("Shortest Path: " + geneticPaths.get(geneticMinHeap.peek()));
		System.out.println("Shortest Path Distance: " + geneticMinHeap.peek());
		drawPoints(geneticPaths.get(geneticMinHeap.peek()), "Genetic Algorithm");


		String outputFilename1 = "output_localSearch.csv";
		String outputFilename2 = "output_localSearch_SA.csv";
		String outputFilename3 = "output_Genetic.csv";
		PrintWriter output1 = null;
		PrintWriter output2 = null;
		PrintWriter output3 = null;
		// open output stream
		try {
			output1 = new PrintWriter(new FileWriter(outputFilename1));
			output2 = new PrintWriter(new FileWriter(outputFilename2));
			output3 = new PrintWriter(new FileWriter(outputFilename3));
			for (Double distance : localBD) {
				output1.println(distance);
			}
			
			for (Double distance : localBD_SA) {
				output2.println(distance);
			}
			
			for (Double distance : geneticBD) {
				output3.println(distance);
			}
			
		} catch (IOException ex) {
			System.exit(1);
		}finally {
			output1.close();
			output2.close();
			output3.close();
		}
		

		
	
		
	}

	/**
	 * STARTING DRAWING
	 */

	private static void drawPoints(Set<String> bestPath, String algorithmName) {

		System.out.println(bestPath.iterator().next());
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Draw Points " + algorithmName);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(new PointsPanel(bestPath.iterator().next()));
			frame.setSize(400, 400);
			frame.setVisible(true);
		});
	}

	static class PointsPanel extends JPanel {
		private String pointsPath;

		public PointsPanel(String pointsPath) {
			this.pointsPath = pointsPath;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			// Set max values based on your coordinate range
			int maxCoordValue = determineMaxCoordinate(pointsPath);
			double scaleX = (getWidth() - 40) / (double) maxCoordValue;
			double scaleY = (getHeight() - 40) / (double) maxCoordValue;

			// Draw the path which includes lines and arrowheads
			g2d.setStroke(new BasicStroke(2)); // Set the line thickness
			g2d.setColor(Color.BLUE); // Set the line color
			drawPath(g2d, pointsPath, scaleX, scaleY);

			// Draw points on top of the path
			g2d.setColor(Color.RED); // Set the point color
			drawPoints(g2d, pointsPath, scaleX, scaleY);
		}

		private void drawPath(Graphics2D g2, String points, double scaleX, double scaleY) {
			Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");
			Matcher matcher = pattern.matcher(points);
			int prevX = -1;
			int prevY = -1;
			int firstX = -1;
			int firstY = -1;

			while (matcher.find()) {
				int x = Integer.parseInt(matcher.group(1));
				int y = Integer.parseInt(matcher.group(2));
				int drawX = (int) (x * scaleX);
				int drawY = (int) (y * scaleY);

				// Draw the point (circle)
				g2.fillOval(drawX - 2, drawY - 2, 4, 4); // Draw a small oval centered on the point

				// If not the first point, draw a line from the previous point
				if (prevX != -1 && prevY != -1) {
					g2.drawLine(prevX, prevY, drawX, drawY);
					drawArrowHead(g2, prevX, prevY, drawX, drawY); // Use Graphics2D for the drawArrowHead method
				} else {
					// first point to draw a line from the last point to the first
					firstX = drawX;
					firstY = drawY;
				}

				// Update previous point
				prevX = drawX;
				prevY = drawY;
			}

			// Draw a line from the last point to the first to complete the circuit
			if (firstX != -1 && firstY != -1) {
				g2.drawLine(prevX, prevY, firstX, firstY);
				drawArrowHead(g2, prevX, prevY, firstX, firstY);
			}
		}

		private int determineMaxCoordinate(String path) {
			Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");
			Matcher matcher = pattern.matcher(path);
			int maxX = 0;
			int maxY = 0;

			while (matcher.find()) {
				int x = Integer.parseInt(matcher.group(1));
				int y = Integer.parseInt(matcher.group(2));
				if (x > maxX) {
					maxX = x;
				}
				if (y > maxY) {
					maxY = y;
				}
			}

			// Return the larger of the two maxima as the scale factor
			return Math.max(maxX, maxY);
		}

		private void drawPoints(Graphics2D g, String path, double scaleX, double scaleY) {
			Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");
			Matcher matcher = pattern.matcher(path);
			int firstX = -1;
			int firstY = -1;
			boolean isFirstPoint = true; // Flag to identify the first point

			while (matcher.find()) {
				int x = Integer.parseInt(matcher.group(1));
				int y = Integer.parseInt(matcher.group(2));
				int drawX = (int) (x * scaleX);
				int drawY = (int) (y * scaleY);

				if (isFirstPoint) {
					firstX = drawX;
					firstY = drawY;
					isFirstPoint = false; // Not the first point anymore
				} else {
					// Draw the point (circle) for other points
					g.setColor(Color.RED); // Default color for other points
					g.fillOval(drawX - 5, drawY - 5, 10, 10); // Adjust size as necessary
					// Draw labels for points
					g.drawString(String.format("(%d,%d)", x, y), drawX + 5, drawY - 5);
				}
			}

			// drawing the first point in a different color
			if (firstX != -1 && firstY != -1) {
				g.setColor(Color.GREEN); 
				g.fillOval(firstX - 5, firstY - 5, 10, 10); 
				// Draw labels for the first point
				Pattern firstPointPattern = Pattern.compile("\\((\\d+),(\\d+)\\)");
				Matcher firstPointMatcher = firstPointPattern.matcher(path);
				if (firstPointMatcher.find()) {
					int x = Integer.parseInt(firstPointMatcher.group(1));
					int y = Integer.parseInt(firstPointMatcher.group(2));
					g.drawString(String.format("(%d,%d)", x, y), firstX + 5, firstY - 5);
				}
			}
		}

		private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {
			//offset point from the destination point
			double t = 0.75;
			int x = (int) (t * x1 + (1 - t) * x2);
			int y = (int) (t * y1 + (1 - t) * y2);

			double dx = x2 - x1, dy = y2 - y1;
			double angle = Math.atan2(dy, dx);
			int len = (int) Math.sqrt(dx * dx + dy * dy) / 4; // Length of the arrowhead; adjust as needed

			AffineTransform at = AffineTransform.getTranslateInstance(x, y);
			at.concatenate(AffineTransform.getRotateInstance(angle));
			g2.setTransform(at);

			// Draw horizontal arrow starting in (0, 0)
			g2.fillPolygon(new int[] { len, len - 5, len - 5, len }, new int[] { 0, -5, 5, 0 }, 4);

			// Reset the transform to draw the next arrow correctly
			g2.setTransform(new AffineTransform());
		}

	}

	/**
	 * ENDING DRAWING
	 */

	/**
	 * STARTING GENETIC ALGORITHMS
	 */

	public static void geneticSearch(ArrayList<String> initialPath, int populationSize, int generations) {
		createPopulation(populationSize); // Initialize population
//	    System.out.println("Initial Population Created");

		while (generationCounter < generations) {
			// for (int gen = 0; gen < generations; gen++) {
			Set<ArrayList<String>> newGeneration = new HashSet<>();

			while (newGeneration.size() < populationSize) {
//	            System.out.println("Generation: " + generationCounter + ", Population Size: " + newGeneration.size());
				ArrayList<String> parent1 = selectParent();
				ArrayList<String> parent2 = selectParent();

				if (parent1 == null || parent2 == null) {
					break; // Break the loop if no valid parents are found
				}

				ArrayList<String> child = crossover(parent1, parent2);
				mutate(child, 0.01); // Example mutation rate

				double pathValue = getPathValue(child);

				geneticBD.add(pathValue);
				geneticMinHeap.add(pathValue);
				geneticPaths.computeIfAbsent(pathValue, k -> new HashSet<>()).add(child.toString());

				newGeneration.add(child);
			}

			population.put(generationCounter, newGeneration); // Update the population with the new generation
			generationCounter++;
		}
	}

	private static ArrayList<String> crossover(ArrayList<String> parent1, ArrayList<String> parent2) {
		Set<String> childSet = new HashSet<>();
		ArrayList<String> child = new ArrayList<>();

		int crossoverPoint = random.nextInt(parent1.size() - 1) + 1;
;

		child.add(parent1.get(0)); // Start with the starting point of parent1
		childSet.add(parent1.get(0));

		for (int i = 1; i < crossoverPoint; i++) {
			child.add(parent1.get(i));
			childSet.add(parent1.get(i));
		}
		for (String city : parent2) {
			if (!childSet.contains(city)) {
				child.add(city);
			}
		}

		child.add(parent1.get(0)); // end with the starting point of parent1 to maintain the circuit
		return child;
	}


	private static void mutate(ArrayList<String> path, double mutationRate) {
		for (int i = 1; i < path.size() - 1; i++) {
			if (random.nextDouble() < mutationRate) {
				int indexToSwap;
				do {
					indexToSwap = random.nextInt(path.size() - 2) + 1;
				} while (i == indexToSwap); // Ensure i and indexToSwap are not the same

				String temp1 = path.get(i);
				String temp2 = path.get(indexToSwap);

				// does not create consecutive duplicates and that temp2 is not
				// equal to temp1
				if (!temp1.equals(temp2) && !temp1.equals(path.get(indexToSwap - 1))
						&& !temp1.equals(path.get(indexToSwap + 1)) && !temp2.equals(path.get(i - 1))
						&& !temp2.equals(path.get(i + 1))) {
					path.set(i, temp2);
					path.set(indexToSwap, temp1);
//	                System.out.println("Mutation at Index: " + i + " with Index: " + indexToSwap);
				}
			}
		}
	}

	private static ArrayList<String> selectParent() {
		int temp = generationCounter;
		temp--;
		Set<ArrayList<String>> pathsSet = population.get(temp);
		if (pathsSet == null) {
//		    System.out.println("Warning: No paths found for generation " + temp);
			return null; // Or handle this case as needed
		}
		ArrayList<ArrayList<String>> paths = new ArrayList<>(pathsSet);
		double totalFitness = paths.stream().mapToDouble(Driver_3_4::getPathValue).sum();
		double randomFitness = random.nextDouble() * totalFitness;
		double currentSum = 0;
		for (ArrayList<String> path : paths) {
			currentSum += getPathValue(path);
			if (currentSum >= randomFitness) {
				return path;
			}
		}
		return paths.get(paths.size() - 1); // Fallback
	}

	public static void createPopulation(int popSize) {

		ArrayList<String> originalPath = new ArrayList<>(path);

		int i = 0;
		while (i < popSize) {
			ArrayList<String> newPath = new ArrayList<>(originalPath);
			int ranIndex1;
			int ranIndex2;
			// Generate the first random index
			do {
				// Generate the second random index
				ranIndex1 = random.nextInt(path.size());
				ranIndex2 = random.nextInt(path.size());
			} while (ranIndex2 == ranIndex1); // Repeat if the indices are the same

//			System.out.println("RANDOM VALUES: ran1- " + ranIndex1 + " ran2- " + ranIndex2);
			// swap
			if (ranIndex1 == 0 || ranIndex1 == newPath.size() - 1) {
				String temp1 = newPath.get(ranIndex1);
				String temp2 = newPath.get(ranIndex2);

				newPath.set(0, temp2);
				newPath.set(newPath.size() - 1, temp2);

				newPath.set(ranIndex2, temp1);

			} else if (ranIndex2 == 0 || ranIndex2 == newPath.size() - 1) {
				String temp1 = newPath.get(ranIndex1);
				String temp2 = newPath.get(ranIndex2);

				newPath.set(0, temp1);
				newPath.set(newPath.size() - 1, temp1); // Note: Changed to size() - 1 to avoid
														// IndexOutOfBoundsException

				newPath.set(ranIndex1, temp2);

			} else {
				Collections.swap(newPath, ranIndex1, ranIndex2);
			}

			boolean pathExists = population.values().stream().flatMap(Set::stream)
					.anyMatch(existingPath -> existingPath.equals(newPath));

			if (!pathExists) {
//				System.out.println("Adding new path: " + newPath); // Debugging print statement
				population.computeIfAbsent(generationCounter, k -> new HashSet<>()).add(newPath);

				i++;
			} else {
//	            System.out.println("Path already exists: " + newPath); // Debugging print statement
			}

		} // end while
		generationCounter++;
	}

	/**
	 * ENDING GENETIC ALGORITHMS
	 */

	/**
	 * STARTING Local Search Simulated Annealing
	 * 
	 */

	public static void localSearch_2opt_SA(ArrayList<String> path) {
		int iterations = 0;
		int maxIterations = 1000;
		double bestValue = Double.MAX_VALUE;
		ArrayList<String> bestPath = new ArrayList<>(path);
		ArrayList<String> tempBestPath = new ArrayList<>(bestPath);

		double temperature = 1000000; // Initial temperature
		double coolingRate = 0.99; // Cooling rate

		double MAX_TEMP = 10000;
		double MIN_TEMP = 0.00001;
		double temp = MAX_TEMP;

		Random random = new Random();

		while (temp > MIN_TEMP && iterations <= maxIterations) {
//		while (temp > MIN_TEMP ) {	
			int ranIndex1;
			int ranIndex2;
			// Generate the first random index
			do {
				// Generate the second random index
				ranIndex1 = random.nextInt(path.size());
				ranIndex2 = random.nextInt(path.size());
			} while (ranIndex2 == ranIndex1); // Repeat if the indices are the same

//			System.out.println("RANDOM VALUES: ran1- " + ranIndex1 + " ran2- " + ranIndex2);
			// swap
			if (ranIndex1 == 0 || ranIndex1 == bestPath.size() - 1) {
				String temp1 = bestPath.get(ranIndex1);
				String temp2 = bestPath.get(ranIndex2);

				bestPath.set(0, temp2);
				bestPath.set(bestPath.size() - 1, temp2);

				bestPath.set(ranIndex2, temp1);

			} else if (ranIndex2 == 0 || ranIndex2 == bestPath.size() - 1) {
				String temp1 = bestPath.get(ranIndex1);
				String temp2 = bestPath.get(ranIndex2);

				bestPath.set(0, temp1);
				bestPath.set(bestPath.size() - 1, temp1); // Note: Changed to size() - 1 to avoid
															// IndexOutOfBoundsException

				bestPath.set(ranIndex1, temp2);

			} else {
				Collections.swap(bestPath, ranIndex1, ranIndex2);
			}

			String bestPathString = bestPath.toString();
			boolean pathExists = localPaths_SA.values().stream().anyMatch(set -> set.contains(bestPathString));

			if (!pathExists) {
				double pvalue = getPathValue(bestPath);
				localBD_SA.add(pvalue);
				minHeap_SA.add(pvalue);
				localPaths_SA.computeIfAbsent(pvalue, k -> new HashSet<>()).add(bestPathString);

				// Modified acceptance logic with simulated annealing

				if (acceptNewSolution(pvalue, bestValue, temperature)) {
					// Accept the new path
					bestValue = pvalue;
					tempBestPath = new ArrayList<>(bestPath);
				} else {
					// Revert to previous path
					bestPath = new ArrayList<>(tempBestPath);
				}

			}

			// Update temperature
			temp /= 1.01;

			temperature *= coolingRate;
			iterations++;

		}
	}

	
	private static boolean acceptNewSolution(double currentEnergy, double previousEnergy, double temperature) {
	    Random randomGenerator = new Random();
	    if (currentEnergy < previousEnergy) {
	        return true;
	    } else {
	        double energyDifference = currentEnergy - previousEnergy;
	        double randomValue = randomGenerator.nextDouble();
	        double acceptanceProbability = Math.exp(-energyDifference / temperature);
	        return randomValue < acceptanceProbability;
	    }
	}
	
//
//	private static double acceptanceProbability(double bestValue, double currentValue, double temperature) {
//		if (currentValue < bestValue) {
//			return 1.0;
//		}
//		return Math.exp((bestValue - currentValue) / temperature);
//	}

	/**
	 * ENDING Local Search Simulated Annealing
	 * 
	 */

	/**
	 * STARTING Local Search
	 * 
	 */
	public static void localSearch_2opt(ArrayList<String> path) {

		int iterations = 0;
		int maxIterations = 1000; // Set a limit for iterations
		double bestValue = Double.MAX_VALUE;
		ArrayList<String> bestPath = new ArrayList<>(path);

		// Store the previous state of bestPath
		ArrayList<String> tempBestPath = new ArrayList<>(bestPath);

		//System.out.println("printing best path from begining " + bestPath.toString());

		// local search
		while (iterations < maxIterations) {

			int ranIndex1;
			int ranIndex2;
			// Generate the first random index
			do {
				// Generate the second random index
				ranIndex1 = random.nextInt(path.size());
				ranIndex2 = random.nextInt(path.size());
			} while (ranIndex2 == ranIndex1); // Repeat if the indices are the same

			//System.out.println("RANDOM VALUES: ran1- " + ranIndex1 + " ran2- " + ranIndex2);
			// swap
			if (ranIndex1 == 0 || ranIndex1 == bestPath.size() - 1) {
				String temp1 = bestPath.get(ranIndex1);
				String temp2 = bestPath.get(ranIndex2);

				bestPath.set(0, temp2);
				bestPath.set(bestPath.size() - 1, temp2);

				bestPath.set(ranIndex2, temp1);

			} else if (ranIndex2 == 0 || ranIndex2 == bestPath.size() - 1) {
				String temp1 = bestPath.get(ranIndex1);
				String temp2 = bestPath.get(ranIndex2);

				bestPath.set(0, temp1);
				bestPath.set(bestPath.size() - 1, temp1); // Note: Changed to size() - 1 to avoid
															// IndexOutOfBoundsException

				bestPath.set(ranIndex1, temp2);

			} else {
				Collections.swap(bestPath, ranIndex1, ranIndex2);
			}

			String bestPathString = bestPath.toString();

			// Check if the current path already exists in any of the sets in localPaths
			boolean pathExists = localPaths.values().stream().anyMatch(set -> set.contains(bestPathString));

			if (!pathExists) {

				double pvalue = getPathValue(bestPath);

				localBD.add(pvalue);
				minHeap.add(pvalue);
				localPaths.computeIfAbsent(pvalue, k -> new HashSet<>()).add(bestPathString);

				//System.out.println("above call printing bestpath: " + bestPathString);

				if (!(pvalue <= minHeap.peek())) {
					bestPath = new ArrayList<>(tempBestPath);
				} else {
					tempBestPath = new ArrayList<>(bestPath);
				}

			} else {
				// If the path already exists, restore bestPath to its previous state
				bestPath = new ArrayList<>(tempBestPath);
				// You might want to skip this iteration or handle it in some other way
			}

			iterations++;
		} // end loop

	}// end localSearch_2opt

	/**
	 * ENDING Local Search
	 * 
	 */

	public static double getPathValue(ArrayList<String> path) {
		double pathValue = 0;
		//System.out.println("PATH--- "+ path.toString());
		//System.out.println("inside path Value size: "+ path.size());
		for (int i = 1; i < path.size(); i++) {
			//System.out.println("top  i : " + i );
			//System.out.println(path.get(i - 1) + "->" + path.get(i));
			String key = path.get(i - 1) + "->" + path.get(i);
			double value = graph.get(key);
			pathValue += value;

		}

		return pathValue;
	}

	public static void createPath() {

		for (int i = 0; i < matrix.length; i++) {
			path.add("(" + matrix[i][0] + "," + matrix[i][1] + ")");
		}
		path.add(path.get(0));

	}// end create path

	public static void grabInput() {
		String inputFile = "input.txt";
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
			// Read the first line to get the number of points
			String line = br.readLine();
			int rows = Integer.parseInt(line.trim());
			matrix = new int[rows][2];

			// Read the points from the file
			for (int i = 0; i < rows; i++) {
				line = br.readLine();
				if (line != null) {
					String[] parts = line.trim().split("\\s+");
					matrix[i][0] = Integer.parseInt(parts[0]);
					matrix[i][1] = Integer.parseInt(parts[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}// end granbInput

	public static int[][] shuffle2DArray(int[][] array) {
		List<int[]> list = new ArrayList<>();

		// Convert the 2D array to a list of int[] for easy shuffling
		for (int[] row : array) {
			list.add(row);
		}

		Collections.shuffle(list);

		// Convert back to 2D array
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}

		return null;
	}// shuffle2DArray

	public static HashMap<String, Double> calculateAllDistances(int[][] points) {
		int n = points.length;
		HashMap<String, Double> distanceMap = new HashMap<>();

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {

					String key = String.format("(%d,%d)->(%d,%d)", points[i][0], points[i][1], points[j][0],
							points[j][1]);
					String reverseKey = String.format("(%d,%d)->(%d,%d)", points[j][0], points[j][1], points[i][0],
							points[i][1]);

					// Check if the key or reverseKey already exists in the map
					if (!distanceMap.containsKey(key) && !distanceMap.containsKey(reverseKey)) {
						double distance = Math.sqrt(
								Math.pow(points[j][0] - points[i][0], 2) + Math.pow(points[j][1] - points[i][1], 2));
						distanceMap.put(key, distance);
						distanceMap.put(reverseKey, distance); // Store the reverse key as well
					}
				} else {
					String key = String.format("(%d,%d)->(%d,%d)", points[i][0], points[i][1], points[j][0],
							points[j][1]);
					String reverseKey = String.format("(%d,%d)->(%d,%d)", points[j][0], points[j][1], points[i][0],
							points[i][1]);

					if (!distanceMap.containsKey(key) && !distanceMap.containsKey(reverseKey)) {

						distanceMap.put(key, Double.MAX_VALUE);
						distanceMap.put(reverseKey, Double.MAX_VALUE); // Store the reverse key as well
					}
				}
			}
		}
		return distanceMap;
	}// end calculateAllDistances

	public static double[][] calculateAllDistances2(int[][] points) {
		int n = points.length;
		double[][] distanceMatrix = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) { // j starts from i+1 to avoid repeat calculations
				double distance = Math
						.sqrt(Math.pow(points[j][0] - points[i][0], 2) + Math.pow(points[j][1] - points[i][1], 2));
				distanceMatrix[i][j] = distance;
				distanceMatrix[j][i] = distance; // Matrix is symmetric
			}
		}
		return distanceMatrix;
	}

}// end driver
