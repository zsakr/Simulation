import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Simulation {

    // Constant Variables
    static final int MAX_BURST = 20;            // Maximum CPU bursts and blocks a process can have
    static final int MAX_PREEMPTED = 3;         // Maximum number of times a process can be preempted before it is demoted
    static final int GC_LINE_SIZE = 40;         // Maximum CPU bursts and blocks a process can have
    static final int GC_FORMAT = 5;             // Maximum CPU bursts and blocks a process can have
    static final char CPU_Idle = '*';           // Symbol for Gannt Chart when the CPU is Idle
    static final String HIGH = "HP";            // Key for Process with High Priority/on H1 Queue
    static final String LOW = "LP";             // Key for Process with Low Priority
    static final String LOW2 = "L2";            // Key for Process on L2 Queue
    static final String LOW3 = "L3";            // Key for Process on L3 Queue
    static final String READY = "READY";        // Key for Process in Ready State (on one of the Ready Queues)
    static final String RUNNING = "RUNNING";    // Key for Process in Running State (on the CPU)
    static final String BLOCKED = "BLOCKED";    // Key for Process in Blocked State (on the Blocked Queue)
    static final String DONE = "DONE";          // Key for Process that has left the system


    // ArrayLists to hold processes and information
    static ArrayList<Process> ProcessesList = new ArrayList<Process>();     // Global list of all Processes
    static ArrayList<Process> H1 = new ArrayList<Process>();                // High Priority Ready Queue
    static ArrayList<Process> L2 = new ArrayList<Process>();                // Low Priority 2 Ready Queue
    static ArrayList<Process> L3 = new ArrayList<Process>();                // Low Priority 3 Ready Queue
    static ArrayList<Process> Blocked = new ArrayList<Process>();           // Blocked Queue
    static ArrayList<Character> ganntChart = new ArrayList<Character>();    // Gannt Chart

    // Object to hold Process currently on the CPU
    static Process cur_running;

    // Variables to hold Quantum Sizes
    static int L2quant = 0;     // Quantime Size for L2 Queue
    static int L3quant = 0;     // Quantime Size for L3 Queue

    public static void main(String args[])throws Exception {
        // Check if input file was not passed as an argument in command line
        if (args.length == 0) {
            System.out.println("No command line arguments were passed.");
            System.exit(0);
        }

        else {
            // Read input text
            File file = new File(args[0]);          // Create a file to hold input text
            readFile(file);                         // Function call to read the file holding input text

            // Run Simulation
            int clockTick = -1;                     // Set clock tick to -1 intially
            while (true) {                          // while loop to run simulation
                clockTick++;                        // Increment clock tick by 1
                CheckNewJobs(clockTick);            // Function call to check if new jobs are entering the system
                BlockedJobReturnCheck(clockTick);   // Function call to check if block jobs are returning to the Ready Queues
                CPUJobDoneCheck(clockTick);         // Function call to check if job currently running on the CPU has finished its CPU Burst
                QuantumCheck(clockTick);            // Function call to check if job currently running on the CPU has finished it Quantum
                PickProcess(clockTick);             // Function call to pick a process to run on the CPU if the CPU is Idle
                UpdateWaitingTime();                // Function call to update the waiting time for each process

                if (CheckExit())                    // Function call to check if all the processes have left the system
                    break;
                UpdateGanntChart();                 // Function call to update the Gannt Chart at the end of each clock tick
            }
            Print();                                // Function call to print Gannt Chart and other information
        }
    }

    public static void readFile(File file) {
        try {
            Scanner scanner = new Scanner(file);    // Scanner object 

            L2quant = scanner.nextInt();
            scanner.nextLine();

            L3quant = scanner.nextInt();
            scanner.nextLine();

            while(scanner.hasNextLine()) {
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

                    if (arr[i] == -1)
                        break;
                }
                P.burst_info = arr;
                P.burst_index = 0;

                ProcessesList.add(P);
            }

            scanner.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void CheckNewJobs(int clockTick) {
        for (int i=0; i<ProcessesList.size(); i++) {
            Process p = ProcessesList.get(i);
            if (p.in_time == clockTick) {
                if (p.priority.equals(HIGH)) {
                    p.state = READY;
                    H1.add(p);
                }
                else {
                    p.state = READY;
                    p.priority = LOW2;
                    L2.add(p);
                }
            }
        }
    }

    public static void BlockedJobReturnCheck(int clockTick) {
        for (int i=0; i<Blocked.size(); i++) {
            Process p = Blocked.get(i);
            if (p.state.equals(BLOCKED)) {
                if (p.blocked_till == clockTick) {
                    if (p.priority.equals(HIGH)) {
                        p.state = READY;
                        p.burst_index++;
                        H1.add(p);
                        Blocked.remove(i);
                    }
                    else {
                        p.state = READY;
                        p.burst_index++;
                        p.priority = LOW2;
                        L2.add(p);
                        Blocked.remove(i);
                    }
                }
            }
        }
    }

    public static void CPUJobDoneCheck(int clockTick) {
        if (cur_running != null) {
            if (cur_running.run_till == clockTick) {
                cur_running.burst_index++;
                if (cur_running.burst_info[cur_running.burst_index] == -1) {
                    cur_running.out_time = clockTick;
                    cur_running.state = DONE;
                    cur_running = null;
                }
                else {
                    cur_running.state = BLOCKED;
                    cur_running.blocked_till = clockTick + cur_running.burst_info[cur_running.burst_index];
                    cur_running.num_preempted = 0;
                    cur_running.run_time = 0;
                    Blocked.add(cur_running);
                    cur_running = null;
                }
            }
            else
                cur_running.run_time++;
        }
    }

    public static void QuantumCheck(int clockTick) {
        if (cur_running != null) {
            if (cur_running.priority.equals(LOW2)) {
                if (cur_running.run_time == L2quant) {
                    cur_running.num_preempted++;
                    cur_running.burst_info[cur_running.burst_index] -= L2quant;
                    cur_running.state = READY;
                    if (cur_running.num_preempted == MAX_PREEMPTED) {
                        cur_running.priority = LOW3;
                        L3.add(cur_running);
                        cur_running = null;
                    }
                    else {
                        L2.add(cur_running);
                        cur_running = null;
                    }
                }
            }
            else if (cur_running.priority.equals(LOW3)) {
                if (cur_running.run_time == L3quant) {
                    cur_running.burst_info[cur_running.burst_index] -= L3quant;
                    cur_running.state = READY;
                    L3.add(cur_running);
                    cur_running = null;
                }
            }
        }
    }

    public static void PickProcess(int clockTick) {
        if (cur_running == null) {
            if (!H1.isEmpty()) {
                cur_running = H1.remove(0);
                cur_running.state = RUNNING;
                cur_running.run_till = clockTick + cur_running.burst_info[cur_running.burst_index];
                cur_running.run_time = 0;
                //ganntChart.add(cur_running.name);
            }

            else if (!L2.isEmpty()) {
                cur_running = L2.remove(0);
                cur_running.state = RUNNING;
                cur_running.run_till = clockTick + cur_running.burst_info[cur_running.burst_index];
                cur_running.run_time = 0;
                //ganntChart.add(cur_running.name);
            }
            else if (!L3.isEmpty()) {
                cur_running = L3.remove(0);
                cur_running.state = RUNNING;
                cur_running.run_till = clockTick + cur_running.burst_info[cur_running.burst_index];
                cur_running.run_time = 0;
              //  ganntChart.add(cur_running.name);
            }
            //else 
                //ganntChart.add(CPU_Idle);
        }
    }

    public static void UpdateWaitingTime() {
        for (int i=0; i<ProcessesList.size(); i++) {
            Process p = ProcessesList.get(i);
            if (p.state.equals(READY))
                p.wait_time++;
        }
    }

    public static void UpdateGanntChart() {
        if (cur_running == null)
            ganntChart.add(CPU_Idle);
        else
            ganntChart.add(cur_running.name);
    }

    public static boolean CheckExit() {
        boolean b = true;
        for (int i=0; i<ProcessesList.size(); i++)
            if (!ProcessesList.get(i).state.equals(DONE))
                b = false;
        return b;
    }

    public static void Print() {
        String gaant = "";
        double idle_time = 0;
        double total_time = ganntChart.size();
        for (int i=0; i<total_time; i++) {
            char ch = ganntChart.get(i);
            if (i%5 == 0 && i>0)
                gaant += "|";
            if (i == 40)
                gaant += "\n";
            gaant += ch;

            if (ch == '*')
                idle_time++;
        }
        System.out.println("Gaant Chart:\n" + gaant);

        double util_CPU = (total_time-idle_time)/total_time*100.0;

        int num_processes = ProcessesList.size();
        double avg_turnaround = 0;
        double avg_wait = 0;
        for (int i=0; i<num_processes; i++) {
            Process p = ProcessesList.get(i);
            int p_turnaround = p.turnaroundTime();
            int p_wait = p.wait_time;
            avg_turnaround += p_turnaround;
            avg_wait += p_wait;
            System.out.println("\nProcess Name: " + p.name);
            System.out.println("Turnaround Time: " + p_turnaround);
            System.out.println("Total Wait Time: " + p_wait);
        }

        System.out.printf("\nAverage Turnaround Time: %.2f", (avg_turnaround/num_processes));
        System.out.printf("\nAverage Wait Time: %.2f", (avg_wait/num_processes));
        System.out.printf("\nCPU Utilisation: %.2f", util_CPU);
        System.out.print("%");
        System.out.println("\nThroughput: " + num_processes + " Jobs/" + (int)total_time + " Units\n");
    }
}