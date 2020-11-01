/*
* Operating System Simulation
* Systems Software Class for CPSC 315
* @professor Dr.S
* @authors Ziad Sakr, Aadiv Sheth
* @Date: Sunday November 1st 2020
*/
public class main {

	/*
	* Function to Calcuate turnaround time
	* @params process
	* @params size
	* @params burst: an array of burst time
	* @params waiting: an array of the waiting time
	* @params quantum
	*/
	public void waitingTime(int process[], int size, int burst[], int wait[], int quantum){
		//Calcualte turnaround time

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
