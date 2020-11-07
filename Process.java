public class Process {
	
	char name;
	String priority;
	int in_time;
	int burst_info[];

	String state;
	int run_till;
	int block_till;

	int out_time;
	int wait_time;
	// int turnaround_time;

	public Process() {
		state = "READY";
		run_till = -1;
		block_till = -1;
		wait_time = 0;
	}

	public int turnaroundTime() {
		return (out_time - in_time);
	}
}