/*
* Operating System Simulation
* Systems Software Class for CPSC 315
* @professor Dr.S
* @authors Ziad Sakr, Aadiv Sheth
* @Date: Sunday November 1st 2020
*/
import java.io.*;
import java.util.*;

public class main {

	public static int MAX_BURST = 20;

	public static int high = 2;
	public static int L2 = 1;
	public static int L3 = 0;

	public static String high_stuff = "HP";
	public static String low = "LP";

	public static int L2quant;
	public static int L3quant;

	private static SinglyLinkedList<Process> Processes = new SinglyLinkedList<>(); 
	private static SinglyLinkedList<Process> HP = new SinglyLinkedList<>(); 
	private static SinglyLinkedList<Process> LP2 = new SinglyLinkedList<>(); 
	private static SinglyLinkedList<Process> LP3 = new SinglyLinkedList<>(); 


	/*
	* Function to Calcuate waitting time
	* @params process
	* @params size
	* @params burst: an array of burst time
	* @params waiting: an array of the waiting time
	* @params quantum
	*/
	public void waitingTime(int process[], int size, int burst[], int wait[], int quantum) {
		//Calcualte turnaround time for high
		//first come first serve
		while(high > L2 && high > L3){
				//first process wait time is 0
				wait[0] = 0;
				for(int i =1; i < size; i++){
					wait[i] = burst[i -1] + wait[i - 1];
				}
		}
		// NOT DONE for Round robin
	}

	/*
	* Function to Calcuate turnaround time
	* @params process
	* @params size
	* @params burst: an array of burst time
	* @params waiting: an array of the waiting time
	* @params total an array to store the sum
	*/
	public void findTurnAroundTime(int process[], int size, int burst[], int wait[], int total[]) {
		//Calcualte turnaround time
		for(int i = 0; i < size; i++) {
			total[i] = burst[i] + wait[i];
		}
	}


	/*
	* Function to read file
	*/
	public static void readFile() {
		try {
			Scanner scanner = new Scanner(new File("input.txt"));

			L2quant = scanner.nextInt();
			scanner.nextLine();

			L3quant = scanner.nextInt();
			scanner.nextLine();

			while(scanner.hasNextLine()) {
				while(!scanner.next().equals("-1")) {
					Process P = new Process();
					
					P.name = scanner.next().charAt(0);
					scanner.nextLine();
					
					P.priority = scanner.next();
					scanner.nextLine();
					
					P.in_time = scanner.nextInt();
					scanner.nextLine();
					
					int arr[] = new int[MAX_BURST];
					for (int i=0; i<MAX_BURST; i++) {
						arr[i] = scanner.nextInt();
						scanner.nextLine();
					}
					P.burst_info = arr;

					Processes.addLast(P);

					System.out.println(scanner.nextLine());
				}

			}

			scanner.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	* Main Function
	*/

	public static void main(String[] args) {
			readFile();
 	}
}