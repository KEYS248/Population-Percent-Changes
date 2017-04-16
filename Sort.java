
import java.util.ArrayList;
import java.util.Arrays;

	/*
	 * Created: April 7, 2017
	 * Author: David Klein
	 * 
	 * Role: Create sorting algorithms for ArrayLists and improve the Quick Sort algorithm
	 * 		Algorithms include:	Bubble Sort, Merge Sort, Quick Sort, and Improved Quick Sort
	 */
public class Sort {
	
	/*
	 * Bubble Sort iterates through an arraylist, decreasing the ending length by one each time
	 * 		for each iteration it compares two elements and moves the smaller number to the later element slot
	 * 		so for each iteration the smaller number is moved to the end of the unsorted array, 
	 * 			becoming the beginning of the sorted array, and not accessed again
	 */
	public ArrayList<Area> bubbleSort(ArrayList<Area> elements) {
		for (int i = elements.size(); i > 0; --i) {
			for (int j = 0; j < i - 1; ++j) {
				if (elements.get(j).percentChange < elements.get(j+1).percentChange) {
					Area temp = elements.get(j);
					elements.set(j, elements.get(j+1));
					elements.set(j+1, temp);
				}
			}
		}
		return elements;
	}
	
	/*
	 * Merge Sort recursively breaks down an arraylist into two arraylists, each having half the previous arraylist, until an arraylist only has a single element
	 * 		then merges the two arraylists into an arraylist big enough for both using a separate method called 'merge'
	 * 		and does this by iteratively adding the lowest numbers to the new arraylist from each arraylist
	 */
	public void mergeSort(ArrayList<Area> elements, int anterior, int posterior) {
		if (anterior == posterior) {
			return;
		}
		int half = (posterior+anterior)/2;
		mergeSort(elements, anterior, half);
		mergeSort(elements, half+1, posterior);
		merge(elements, anterior, half, posterior);
	}

	public ArrayList<Area> merge(ArrayList<Area> elements, int anterior, int half, int posterior) {
		int firstIndex = anterior;
		int secondIndex = half+1;
		int mainIndex = 0;
		ArrayList<Area> temp = new ArrayList<Area>();
		while (firstIndex <= half && secondIndex <= posterior) {
			if (elements.get(firstIndex).percentChange >= elements.get(secondIndex).percentChange) {
				temp.add(elements.get(firstIndex));
				firstIndex++;
			} else {
				temp.add(elements.get(secondIndex));
				secondIndex++;
			}
		}
		if (firstIndex <= half && secondIndex > posterior) {
			while (firstIndex <= half) {
				temp.add(elements.get(firstIndex));
				firstIndex++;
			}
		} else {
			while (secondIndex <= posterior) {
				temp.add(elements.get(secondIndex));
				secondIndex++;
			}
		}
		for (mainIndex = 0; mainIndex < temp.size(); mainIndex++) {
			elements.set((mainIndex + anterior), temp.get(mainIndex));
		}
		return elements;
	}
	
	/*
	 * Quick Sort picks an element within the arraylist known as a pivot
	 * 		then loops from front to back and back to front of the arraylist
	 * 		switching elements from the front with the back if they are lower than the pivot
	 * 			and switching elements from the back with the front if they are higher than the pivot
	 * 			so at the end all numbers to the left of the pivot are lower than the pivot
	 * 				and all numbers to the right of the pivot are higher than the pivot
	 * 		then recursively calls Quick Sort on the elements left of the pivot if there are more than 2
	 * 		and recursively calls Quick Sort on the elements right of the pivot if there are more than 2
	 */
	public ArrayList<Area> quickSort(ArrayList<Area> elements, int anterior, int posterior) {
		if (anterior == posterior || anterior+1 == posterior) {
			return elements;
		}
		Area pivot = elements.get(anterior + 1);
		Area temp;
		int back = anterior;
		int front = posterior;
		while (back <= front) {
			while (elements.get(back).percentChange > pivot.percentChange) {
				back++;
			}
			while (elements.get(front).percentChange < pivot.percentChange) {
				front--;
			}
			if (back <= front) {
				temp = elements.get(front);
				elements.set(front, elements.get(back));
				elements.set(back, temp);
				back++;
				front--;
			}
		}
		if (anterior < back) {
			quickSort(elements, anterior, back-1);
		} 
		if (front < posterior) {
			quickSort(elements, front+1, posterior);
		}
		return elements;
	}
	
	/*
	 * Improved Quick Sort does the same thing as quick sort but faster and protects against worst case scenarios
	 * It picks the median as the pivot as opposed to just one of the ends otherwise it would result in O(N^2) performance time
	 */
	
	public ArrayList<Area> improvedQuickSort(ArrayList<Area> elements, int anterior, int posterior) {
		if (anterior == posterior || anterior+1 == posterior) {
			return elements;
		}
		Area pivot = elements.get((anterior + posterior) / 2);
		Area temp;
		
		int back = anterior;
		int front = posterior;
		while (back <= front) {
			while (elements.get(back).percentChange > pivot.percentChange) {
				back++;
			}
			while (elements.get(front).percentChange < pivot.percentChange) {
				front--;
			}
			if (back <= front) {
				temp = elements.get(front);
				elements.set(front, elements.get(back));
				elements.set(back, temp);
				back++;
				front--;
			}
		}
		if (anterior < back) {
			quickSort(elements, anterior, back-1);
		} 
		if (front < posterior) {
			quickSort(elements, front+1, posterior);
		}
		return elements;
	}
	
}

