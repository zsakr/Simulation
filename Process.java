/**
 * Project 1: Operating System Simulation
 * CPSC 315: Systems Software
 * @professor Dr.S
 * @author Ziad Sakr
 * @author Aadiv Sheth
 * @Start Date: October 30th 2020
 * @Last modified: November 8th 2020
 */

/**
 * File: Process.java
 * This class holds information and attributes per process and calculates turnaround time
 */

public class Process {
	// Instance Variables
	// Variables for attributes of the process to be input from input text
	char name;			// Name of the process
	String priority;	// Priority of the process
	int in_time;		// Time of Entry of the process
	int burst_info[];	// CPU Bursts and Blocks information of the process

	// Variables to track location and behaviour of the process
	String state;		// Current state of the process
	int burst_index;	// Current CPU Burst/Block of the process
	int run_till;		// Clock tick till which the process has to run
	int blocked_till;	// Clock tick till which the process has to block

	// Variables to implement Round Robin and Migration of Low Priority Jobs
	int run_time;		// How long the process has been on the CPU in the current burst
	int num_preempted;	// Number of times the process has been preempted off the CPU (for Low Priority Jobs)

	// Variables to calculate Total Ready Queue Waiting Time and Turnaround Time of the process
	int out_time; 		// Time of Exit from the system of the process
	int wait_time; 		// Total Ready Queue Waiting Time of the process

	/**
	 * Default Constructor
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
	 * Returns the turnaround time of the process
	 * @return turnaround time = out_time - in_time
	 */
	public int turnaroundTime() {
		return (out_time - in_time);
	}
}