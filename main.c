/*
* Operating System Simulation
* Systems Software Class for CPSC 315
* @professor Dr.S
* @authors Ziad Sakr, Aadiv Sheth
* @Date: Sunday November 1st 2020
*/

#include<stdio.h>

/*
* Function to Calcuate waitting time
* @params process
* @params size
* @params burst: an array of burst time
* @params waiting: an array of the waiting time
* @params quantum
*/
	void waitingTime(int process[], int size, int burst[], int wait[], int quantum){
		//Calcualte turnaround time for high
		//first come first serve
				//first process wait time is 0
				wait[0] = 0;
				for(int i =1; i < size; i++){
					wait[i] = burst[i -1] + wait[i - 1];
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
	void TurnAroundTime(int process[], int size, int burst[], int wait[], int total_turnaround[]){
		//Calcualte turnaround time
		for(int i = 0; i < size; i++){
			total_turnaround[i] = burst[i] + wait[i];
		}
	}

/*
* Function to Calcuate average waiting time of waittime and turnaround
* @params process
* @params size
* @params burst: an array of burst time
*/
	void AverageWaitTime(int process[], int size, int burst[]){
		//initializing variables
		int wait[size];
		int turnaroundTime[size];
		int total_waitTime = 0;
		int total_turnaround = 0;
		int quantum = 0;

		// call functions
		//Wait time
		waitingTime(process, size, burst, wait, quantum);
		///turnaround time
		TurnAroundTime(process, size, burst, wait, turnaroundTime);


int main(){

}



/*
* End of the program
*/
