import java.util.ArrayList;

	/*
	 * Created: April 7, 2017
	 * Author: David Klein
	 * 
	 * Role: Creates a class to hold the name of an area (either region or state), 
	 * 		an ArrayList of population integers for various years, and a percent change that can be applied later
	 */

public class Area {

	public ArrayList<Integer> yearPops;
	public String name;
	public double percentChange;
	
	public Area(ArrayList<Integer> yearPops, String name) {
		this.yearPops = yearPops;
		this.name = name;
	}
	
}
