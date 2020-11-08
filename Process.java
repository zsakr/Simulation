/**
* Process class that hold all the process objects and calculate turnaround time
* Project 1: Operating System Simulation
* CPSC 315: Systems Software
* @professor Dr.S
* @author Ziad Sakr, Aadiv Sheth
* @Start Date: Friday October 30th 2020
* @End Date: Sunday Novemeber 8th 2020
*/


/**
* Process class
*/

public class Process {

	char name;
	String priority;
	int in_time;
	int burst_info[];

	String state;
	int burst_index;
	int run_till;
	int blocked_till;

	int run_time;
	int num_preempted;

	int out_time; // check where to put;
	int wait_time; //check where to put
	// int turnaround_time;


	/**
	* Constructor Process
	* @param none
	*/
	public Process() {
		state = "";
		burst_index = -1;
		run_till = -1;
		blocked_till = -1;
		run_time = 0;
		num_preempted = 0;
		wait_time = 0;
	}

	/**
	* Turnaround function that return the total turnaround in_time
	* by calculating the outtime - the in time
	* @param none
	* @return turnaroundtime (left time - arrival time)
	*/
	public int turnaroundTime() {
		return (out_time - in_time);
	}
}
