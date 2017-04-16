import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

	/*
	 * Created: April 7, 2017
	 * Author: David Klein
	 * 
	 * Role: To read in population data from various years across different regions and states within the United States
	 * 		and allow the user to perform three actions:
	 * 		- Get a list of populations and percent changes for all regions and areas between two years the user specifies
	 * 		- Get the percent change and the closest percent change for another state for a state and two years the user specifies
	 * 		- Get the runtimes for four sorting algorithms by sorting areas by percent change for two years the user specifies
	 */

public class PopulationProgram {
	public static void main(String[] args) throws InvalidFormatException, IOException {
		/*
		 * This code below reads in an excel file titled 'ds-edited.xlsx' using the Apache POI Jar files, objects, and methods
		 * 		putting the regions into an ArrayList titled 'regions' and the states into an ArrayList titled 'states'
		 * 		and these areas include the name of the area and their population years in an ArrayList
		 * An ArrayList of years is also created to hold the names of the years 
		 */
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter the xlsx file name in directory: ");
		String filename = in.next();
		
		File file = new File(filename);
		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFFormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
		ArrayList<Area> regions = new ArrayList<Area>();
		ArrayList<Area> states = new ArrayList<Area>();
		ArrayList<Area> areas = new ArrayList<Area>();
		ArrayList<Integer> years = new ArrayList<Integer>();
		Sort sort = new Sort();
		int rowNum = 0;
		
		for (Row row : sheet) {
			ArrayList<Integer> yearPops = new ArrayList<Integer>();
			String name = "";
			for (Cell cell : row) {
				if (formulaEvaluator.evaluateInCell(cell).getCellTypeEnum() == CellType.STRING) {
					name = (cell.getStringCellValue());
				}
				else {
					if (rowNum == 0) {
						years.add((int) (cell.getNumericCellValue()));
					}
					else {
					yearPops.add((int) (cell.getNumericCellValue()));
					}
				}
			}
			if (rowNum == 0) {
				rowNum++;
				continue;
			}
			if (name.length() > 15 || name.length() < 1) {
				continue;
			}
			if (name.charAt(0) == '.') {
				Area temp = new Area(yearPops, name.substring(1, name.length()));
				states.add(temp);
			}
			else if (name.equals("Puerto Rico")) {
				Area temp = new Area(yearPops, name);
				states.add(temp);
			}
			else {
				Area temp = new Area(yearPops, name);
				regions.add(temp);
			}
		}
		
		/*
		 * The program will continuously print out a menu until the user chooses the option to end the program
		 * All choices utilize inputValidator to protect against incorrect user input
		 * The first choice gives asks the user for two years and for the sorting algorithm they wish to us
		 * 		the program then finds the percent changes for each area between the two years 
		 * 		and sorts the areas from highest to lowest percent change using the specified sorting alorithm
		 * 		then prints all the areas out with the populations for the two years and the percent change
		 * 		and prints the runtime of the sorting algorithm for the run
		 */
		ArrayList<Integer> menuOptions = new ArrayList<Integer>(Arrays.asList(1,2,3,4));
		while (true) {
			String menu = "\n\t---MENU---\n"
					+ "Enter 1 to get a sorted list of percent changes "
					+ "for all regions and areas\n\tbetween two years that you specify\n"
					+ "Enter 2 to get states with similar percent changes for each year\n\tof a state and two years that you specify\n"
					+ "Enter 3 to compare the speeds of all four sorting algorithms\n"
					+ "Enter 4 to end program\n";
			int choice = inputValidator(menu, in, menuOptions);
			
			if (choice == 1) {	
				int[] choices = yearsAndPercents(regions, states, years, in);
				String sortChoice = "Which sorting algorithm do you want to use?"
						+ "\nEnter 1 for Merge Sort\t\tEnter 2 for Bubble Sort\nEnter 3 for Plain Quick Sort\tEnter 4 for Improved Quick Sort";
				int sortType = inputValidator(sortChoice, in, menuOptions);
				/*
				areas.addAll(regions);
				areas.addAll(states);
				*/
				long startTime = System.nanoTime();
				if (sortType == 1) {
					sort.mergeSort(regions, 0, regions.size()-1);
					sort.mergeSort(states, 0, states.size()-1);
				}
				else if (sortType == 2) {
					regions = sort.bubbleSort(regions);
					states = sort.bubbleSort(states);
				}
				else if (sortType == 3) {
					regions = sort.quickSort(regions, 0, regions.size()-1);
					states = sort.quickSort(states, 0, states.size()-1);
				}
				else {
					regions = sort.improvedQuickSort(regions, 0, regions.size()-1);
					states = sort.improvedQuickSort(states, 0, states.size()-1);
				}
				long endTime = System.nanoTime();
				long taken = (endTime - startTime);
				System.out.println("\n\t==== REGIONS ====");
				printAllAreas(regions, getIndex(choices[0], years), getIndex(choices[1], years));
				System.out.println("\n\t==== STATES ====");
				printAllAreas(states, getIndex(choices[0], years), getIndex(choices[1], years));
				System.out.println("Sorting took: " + taken + " nanoseconds");
				continue;
			}
			/*
			 * The second choice takes in user input specifying the desired state and two years
			 * 		then finds the percent changes for all states for those two years
			 * 		and sorts the states according to percent change 
			 * 			so the program can compare the states immediately before and after the desired state
			 * 			and find the state that has the most similar percent change
			 * 		finally prints the desired state, percent change, two years, and most similar state by percent change
			 */
			else if (choice == 2) {
				int stateIndex = -1;
				String stateName = "";
				while (stateIndex == -1) {
					System.out.println("Choose a state from this list to find states with similar percent changes: " + printStateNames(states));
					stateName = in.next();
					stateIndex = findAreaIndex(states, stateName);
					if (stateIndex == -1) {
						System.out.println("Error, please enter a valid choice");
					}
				}
				int[] choices = yearsAndPercents(regions, states, years, in);
				
				states = sort.quickSort(states, 0, states.size()-1);
				stateIndex = findAreaIndex(states, stateName);
				int similarIndex = getCloserState(states, stateIndex);
				System.out.printf("%s has percent change of %,f between years %d and %d\n%s has the next closest percent change with %,f"
						+ "\n", stateName, states.get(stateIndex).percentChange, choices[0], choices[1], states.get(similarIndex).name, states.get(similarIndex).percentChange);
				continue;
			}
			/*
			 * The third choice has the user specify two years then finds all the percent changes for all areas between these two years
			 * 		then makes four copies of this ArrayList for sorting by four sorting algorithms
			 * 		and prints the resulting run times for each sorting algorithm
			 */
			else if (choice == 3) {
				yearsAndPercents(regions, states, years, in);
				areas.addAll(regions);
				areas.addAll(states);
				@SuppressWarnings("unchecked")
				ArrayList<Area> areas1 = (ArrayList<Area>) areas.clone();
				@SuppressWarnings("unchecked")
				ArrayList<Area> areas2 = (ArrayList<Area>) areas.clone();
				@SuppressWarnings("unchecked")
				ArrayList<Area> areas3 = (ArrayList<Area>) areas.clone();
				@SuppressWarnings("unchecked")
				ArrayList<Area> areas4 = (ArrayList<Area>) areas.clone();
				
				long st1 = System.nanoTime();
				sort.quickSort(areas1, 0, areas.size()-1);
				long et1 = System.nanoTime();
				System.out.println("Quick Sort took:\t" + (et1 - st1) + " nanoseconds");
				
				long st2 = System.nanoTime();
				sort.bubbleSort(areas2);
				long et2 = System.nanoTime();
				System.out.println("Bubble Sort took:\t" + (et2 - st2) + " nanoseconds");
				
				long st3 = System.nanoTime();
				sort.mergeSort(areas3, 0, areas.size()-1);
				long et3 = System.nanoTime();
				System.out.println("Merge Sort took:\t" + (et3 - st3) + " nanoseconds");
				
				long st4 = System.nanoTime();
				sort.improvedQuickSort(areas4, 0, areas.size()-1);
				long et4 = System.nanoTime();
				System.out.println("Improved Quick took:\t" + (et4 - st4) + " nanoseconds");
				continue;
			}
			// The fourth choice ends the while look and program.
			else if (choice == 4); break;
		}	
		wb.close();
		in.close();
	}
	
	/*
	 * Input Validator prints out a message continuously 
	 * until the user inputs a number that exists in the 'allowed' ArrayList
	 * then returns this value
	 */
	public static int inputValidator(String output, Scanner in, ArrayList<Integer> allowed) {
		int input = 0;
		boolean repeater = true;
		
		while (repeater) {
			System.out.println(output);
			if (in.hasNextInt()) {
				input = in.nextInt();
				if (allowed.contains(input)) {
					repeater = false;
				}
				else {
					System.out.println("Error, please enter a valid choice");
				}
			}
			else if (repeater) {
				System.out.println("Error, please enter a valid choice");
				in.next();
				continue;
			}
		}
		return input;
	}
	
	
	// Get Index takes in an integer and returns the index that that integer is found in the 'years' parameter ArrayList variable
	public static int getIndex(int choice, ArrayList<Integer> years) {
		for (int index = 0; index < years.size(); ++index) {
			if (choice == years.get(index)) {
				return index;
			}
		}
		return choice;
	}
	
	/*
	 * Get Closer State compares the percent change between the percent changes of the state before and after the state indicated
	 * 		by the 'stateIndex' parameter variable, then returns the index of the state that has the lower percent change from the indicated state
	 */
	public static int getCloserState(ArrayList<Area> areas, int stateIndex) {
		double previousIncrease = areas.get(stateIndex - 1).percentChange - areas.get(stateIndex).percentChange;
		double previousPercentChange = previousIncrease / areas.get(stateIndex).percentChange;
		double nextIncrease = areas.get(stateIndex + 1).percentChange - areas.get(stateIndex).percentChange;
		double nextPercentChange = nextIncrease / areas.get(stateIndex).percentChange;
		if (previousPercentChange < nextPercentChange) {
			return stateIndex - 1;
		}
		else; return stateIndex + 1;
	}
	
	/*
	 * Set Percent Changes iterates through the 'areas' parameter ArrayList variable and calculates the percent error
	 * between the two populations within that area based on the two years taken in as parameters
	 * and sets the percent change variable for each area to be that number
	 */
	public static void setPercentChanges(ArrayList<Area> areas, int index1, int index2) {
		for (int area = 0; area < areas.size(); ++area) {
			double increase = (areas.get(area).yearPops.get(index2) - areas.get(area).yearPops.get(index1));
			double percent = (increase / areas.get(area).yearPops.get(index1));
			areas.get(area).percentChange = percent;
		}
	}
	
	// Print All Areas iterates through the 'areas' parameter ArrayList variable and prints the name, two populations, and percent change for each element
	public static void printAllAreas(ArrayList<Area> areas, int index1, int index2) {
		for (int area = 0; area < areas.size(); ++area) {
			System.out.printf("%s: %d, %d\n\tPercent change: %,f\n\n", areas.get(area).name, areas.get(area).yearPops.get(index1), areas.get(area).yearPops.get(index2), areas.get(area).percentChange);
		}
	}
	
	// Get Index takes in a String and returns the index that that String is found in the 'areas' parameter ArrayList variable
	public static int findAreaIndex(ArrayList<Area> areas, String key) {
		for (int area = 0; area < areas.size(); ++area) {
			if (areas.get(area).name.equals(key)) {
				return area;
			}
		}
		return -1;
	}
	
	// Print State Names iterates through all the states in the 'areas' ArrayList parameter variable and prints the names of each
	public static String printStateNames(ArrayList<Area> areas) {
		String output = "";
		for (int area = 0; area < areas.size(); ++area) {
			output = output + areas.get(area).name + ", ";
		} 
		return output;
	}
	
	/*
	 * Years And Percents gets the user input for two years and finds the index of these two years within the 'years' ArrayList parameter variable
	 * 		and finds the percent changes for each region and state between those two years and sets that number for each
	 * 		and return an array of the two user specified years
	 */
	public static int[] yearsAndPercents(ArrayList<Area> regions, ArrayList<Area> states, ArrayList<Integer> years, Scanner in) {
		System.out.println("Choose two years to compare from this list of years: " + years.toString());
		int choice1 = inputValidator("Choose the first year: ", in, years);
		int choice2 = inputValidator("Choose the second year: ", in, years);
		
		int index1 = getIndex(choice1, years);
		int index2 = getIndex(choice2, years);
		setPercentChanges(regions, index1, index2);
		setPercentChanges(states, index1, index2);
		
		int[] output = {choice1, choice2};
		return output;
	}
	
}
