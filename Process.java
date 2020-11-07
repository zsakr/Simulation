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

	public Process() {
		state = "";
		burst_index = -1;
		run_till = -1;
		blocked_till = -1;
		run_time = 0;
		num_preempted = 0;
		wait_time = 0;
	}

	public int turnaroundTime() {
		return (out_time - in_time);
	}
}