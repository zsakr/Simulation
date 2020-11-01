/*
* Operating System Simulation
* Systems Software Class for CPSC 315
* @professor Dr.S
* @authors Ziad Sakr, Aadiv Sheth
* @Date: Sunday November 1st 2020
*/
public class main {

			public int high = 2;
			public int L2 = 1;
			public int L3 = 0;


	/*
	* Function to Calcuate waitting time
	* @params process
	* @params size
	* @params burst: an array of burst time
	* @params waiting: an array of the waiting time
	* @params quantum
	*/
	public void waitingTime(int process[], int size, int burst[], int wait[], int quantum){
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
	public void findTurnAroundTime(int process[], int size, int burst[], int wait[], int total[]){
		//Calcualte turnaround time
		for(int i = 0; i < size; i++){
			total[i] = burst[i] + wait[i];
		}
	}




	/*
	* Main Function
	*/

	public static void main(String[] args) {





 	}


}
