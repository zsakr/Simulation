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
 * File: Simulation.java
 * This class implements the system simulation
 * 
 * Note: The input file needs to have a blank line at the end.
 */

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Simulation {

    // Instance Variables
    // Constants
    static final int MAX_BURST = 20;            // Maximum CPU bursts and blocks a process can have
    static final int MAX_PREEMPTED = 3;         // Maximum number of times a process can be preempted before it is demoted
    static final int GC_FORMAT = 5;             // Formatting scheme for gannt chart
    static final int GC_LINE_SIZE = 40;         // Maximum number of clock ticks to be output on one line of the gannt chart
    static final char CPU_Idle = '*';           // Symbol for Gannt Chart when the CPU is Idle
    static final String HIGH = "HP";            // Key for process with High Priority/on H1 Queue
    static final String LOW = "LP";             // Key for process with Low Priority
    static final String LOW2 = "L2";            // Key for process on L2 Queue
    static final String LOW3 = "L3";            // Key for process on L3 Queue
    static final String READY = "READY";        // Key for process in Ready State (on one of the ready queues)
    static final String RUNNING = "RUNNING";    // Key for process in Running State (on the CPU)
    static final String BLOCKED = "BLOCKED";    // Key for process in Blocked State (on the blocked queue)
    static final String DONE = "DONE";          // Key for process that has left the system


    // ArrayLists to hold processes and information
    static ArrayList<Process> ProcessesList = new ArrayList<Process>();     // Global list of all Processes
    static ArrayList<Process> H1 = new ArrayList<Process>();                // High Priority Ready Queue
    static ArrayList<Process> L2 = new ArrayList<Process>();                // Low Priority 2 Ready Queue
    static ArrayList<Process> L3 = new ArrayList<Process>();                // Low Priority 3 Ready Queue
    static ArrayList<Process> Blocked = new ArrayList<Process>();           // Blocked Queue
    static ArrayList<Character> ganntChart = new ArrayList<Character>();    // Gannt Chart

    // Object to hold process currently on the CPU
    static Process cur_running;

    // Variables to hold Quantum Sizes
    static int L2quant = 0;     // Quantime Size for L2 Queue
    static int L3quant = 0;     // Quantime Size for L3 Queue


    /**
     * Main Method
     * @param args  command line arguments
     * @throws Exception   if no file name passed in the command line
     */
    public static void main(String args[])throws Exception {
        // Check if input file was not passed as an argument in command line
        if (args.length == 0) {
            System.out.println("No command line arguments were passed.");
            System.exit(0);
        }

        else {
            // Read input text
            File file = new File(args[0]);              // Create a file to hold input text
            readFile(file);                             // Function call to read the file holding input text

            // Run Simulation
            int clockTick = -1;                         // Set clock tick to -1 intially
            while (true) {                              // while loop to run simulation
                clockTick++;                            // Increment clock tick by 1
                CheckNewJobs(clockTick);                // Function call to check if new jobs are entering the system
                BlockedJobReturnCheck(clockTick);       // Function call to check if blocked jobs are returning to the Ready Queues
                CPUJobDoneCheck(clockTick);             // Function call to check if job currently running on the CPU has finished its CPU Burst
                QuantumCheck(clockTick);                // Function call to check if job currently running on the CPU has finished it quantum
                PickProcess(clockTick);                 // Function call to pick a process to run on the CPU if the CPU is idle
                UpdateWaitingTime();                    // Function call to update the waiting time for each process

                if (CheckExit())                        // Function call to check if all the processes have left the system
                    break;
                UpdateGanntChart();                     // Function call to update the gannt chart at the end of each clock tick
            }
            Print();                                    // Function call to print the gannt chart and other information
        }
    }

    /**
     * Reads the input file using Scanner
     * @param file  Input file
     */
    public static void readFile(File file) {
        try {
            Scanner scanner = new Scanner(file);        // Scanner object 

            L2quant = scanner.nextInt();                // Read quantum size for L2 Queue
            scanner.nextLine();                         // Move to the next line

            L3quant = scanner.nextInt();                // Read quantum size for L3 Queue
            scanner.nextLine();                         // Move to the next line

            while(scanner.hasNextLine()) {              // while loop to read information per process
                Process P = new Process();              // Create new process
                    
                P.name = scanner.next().charAt(0);      // Read name of the process
                scanner.nextLine();                     // Move to the next line
                
                P.priority = scanner.next();            // Read priority of the process
                scanner.nextLine();                     // Move to the next line
                    
                P.in_time = scanner.nextInt();          // Read time of entry of the process
                scanner.nextLine();                     // Move to the next line
                    
                int arr[] = new int[MAX_BURST];         // Create array to hold CPU bursts and blocks of the process
                for (int i=0; i<MAX_BURST; i++) {       // for loop to read information of CPU bursts and blocks of the process
                    arr[i] = scanner.nextInt();         // Read CPU burst/blocks information
                    scanner.nextLine();                 // Move to the next line

                    if (arr[i] == -1)                   // Check if process information is over
                        break;
                }
                P.burst_info = arr;                     // Save information of CPU bursts and blocks of the process to the process
                P.burst_index = 0;                      // Set burst_index to start of array

                ProcessesList.add(P);                   // Add process to global list of all processes
            }

            scanner.close();                            // Close the Scanner object
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if new jobs are entering the system at current clock tick
     * @param clockTick  current clock tick
     */
    public static void CheckNewJobs(int clockTick) {
        // Loops through global list of all processes and checks if any process is entering the system
        for (int i=0; i<ProcessesList.size(); i++) {
            Process p = ProcessesList.get(i);           // Temporary variable to hold current process
            if (p.in_time == clockTick) {
                // if the process is of high priority      
                if (p.priority.equals(HIGH)) {
                    p.state = READY;                    // Changes state of the process to ready
                    H1.add(p);                          // Add process to high priority ready queue
                }
                // if the process is of low priority
                else {
                    p.state = READY;                    // Changes state of the process to ready
                    p.priority = LOW2;                  // Changes priority of process to Low2
                    L2.add(p);                          // Add process to low priority 2 ready queue
                }
            }
        }
    }

    /**
     * Checks if blocked jobs are returning to the ready queues
     * @param clockTick  current clock tick
     */
    public static void BlockedJobReturnCheck(int clockTick) {
        // Loops through Blocked Queue and checks if any process is returning to the ready queues
        for (int i=0; i<Blocked.size(); i++) {
            Process p = Blocked.get(i);                 // Temporary variable to hold current process
            if (p.blocked_till == clockTick) {
                // if the process is of high priority
                if (p.priority.equals(HIGH)) {
                    p.state = READY;                    // Changes state of the process to ready
                    p.burst_index++;                    // Increments current CPU Burst/Block of the process
                    H1.add(p);                          // Add process to high priority ready queue
                    Blocked.remove(i);                  // Removes process from the Blocked Queue
                }
                // if the process is of low priority
                else {
                    p.state = READY;                    // Changes state of the process to ready
                    p.burst_index++;                    // Increments current CPU Burst/Block of the process
                    p.priority = LOW2;                  // Changes priority of process to Low2
                    L2.add(p);                          // Add process to low priority 2 ready queue
                    Blocked.remove(i);                  // Removes process from the Blocked Queue
                }
            }
        }
    }

    /**
     * Checks if job currently running on the CPU has finished its CPU Burst
     * @param clockTick  current clock tick
     */
    public static void CPUJobDoneCheck(int clockTick) {
        // if there is a process currently running on the CPU
        if (cur_running != null) {
            if (cur_running.run_till == clockTick) {
                cur_running.burst_index++;              // Increments current CPU Burst/Block of the process
                // if the current running job hs finished all its CPU Bursts and is ready to leave the system
                if (cur_running.burst_info[cur_running.burst_index] == -1) {
                    cur_running.out_time = clockTick;   // Saves the time of exit from the system of the process
                    cur_running.state = DONE;           // Changes state of the process to done
                    cur_running = null;                 // Takes job off the CPU
                }
                else {
                    cur_running.state = BLOCKED;        // Changes state of the process to blocked
                    // Saves the clock tick till which the process has to block
                    cur_running.blocked_till = clockTick + cur_running.burst_info[cur_running.burst_index];
                    cur_running.num_preempted = 0;      // Updates the number of times the process has been preepmted to 0 (for low priority jobs)
                    cur_running.run_time = 0;           // Updates the running time of the process to 0
                    Blocked.add(cur_running);           // Adds job to the blocked queue
                    cur_running = null;                 // Takes job off the CPU
                }
            }
            else
                cur_running.run_time++;                 // Increments the running time of the process
        }
    }

    /**
     * Checks if job currently running on the CPU has finished it quantum
     * @param clockTick  current clock tick
     */
    public static void QuantumCheck(int clockTick) {
        // if there is a process currently running on the CPU
        if (cur_running != null) {
            // if the process is on the low priority 2 ready queue
            if (cur_running.priority.equals(LOW2)) {
                // if the process has completed its quantum
                if (cur_running.run_time == L2quant) {
                    // Subtracts the quantum size from the current CPU burst size 
                    cur_running.burst_info[cur_running.burst_index] -= L2quant;
                    cur_running.num_preempted++;        // Increments the number of times the process has been preempted
                    cur_running.state = READY;          // Changes state of the process to ready
                    // if the process has been preempted the maximum number of times it gets migrated
                    if (cur_running.num_preempted == MAX_PREEMPTED) {
                        cur_running.priority = LOW3;    // Changes priority of process to Low3
                        L3.add(cur_running);            // Adds job to the low priority 3 ready queue
                        cur_running = null;             // Takes job off the CPU
                    }
                    else {
                        L2.add(cur_running);            // Adds job to the low priority 2 ready queue
                        cur_running = null;             // Takes job off the CPU
                    }
                }
            }
            // if the process is on the low priority 3 ready queue
            else if (cur_running.priority.equals(LOW3)) {
                // if the process has completed its quantum
                if (cur_running.run_time == L3quant) {
                    // Subtracts the quantum size from the current CPU burst size 
                    cur_running.burst_info[cur_running.burst_index] -= L3quant;
                    cur_running.num_preempted++;        // Increments the number of times the process has been preempted
                    cur_running.state = READY;          // Changes state of the process to ready
                    L3.add(cur_running);                // Adds job to the low priority 3 ready queue
                    cur_running = null;                 // Takes job off the CPU
                }
            }
        }
    }

    /**
     * Picks a process to run on the CPU if the CPU is idle
     * @param clockTick  current clock tick
     */
    public static void PickProcess(int clockTick) {
        // if the CPU is idle
        if (cur_running == null) {
            // if the high priority ready queue is not empty
            if (!H1.isEmpty()) {
                cur_running = H1.remove(0);             // Removed the first process on the ready queue and puts it on the CPU
                cur_running.state = RUNNING;            // Changes state of the process to running
                // Saves the clock tick till which the process has to run
                cur_running.run_till = clockTick + cur_running.burst_info[cur_running.burst_index];
                cur_running.run_time = 0;               // Updates the running time of the process to 0
            }

            // if the low priority 2 ready queue is not empty
            else if (!L2.isEmpty()) {
                cur_running = L2.remove(0);             // Removed the first process on the ready queue and puts it on the CPU
                cur_running.state = RUNNING;            // Changes state of the process to running
                // Saves the clock tick till which the process has to run
                cur_running.run_till = clockTick + cur_running.burst_info[cur_running.burst_index];
                cur_running.run_time = 0;               // Updates the running time of the process to 0
            }

            // if the low priority 3 ready queue is not empty
            else if (!L3.isEmpty()) {
                cur_running = L3.remove(0);             // Removed the first process on the ready queue and puts it on the CPU
                cur_running.state = RUNNING;            // Changes state of the process to running
                // Saves the clock tick till which the process has to run
                cur_running.run_till = clockTick + cur_running.burst_info[cur_running.burst_index];
                cur_running.run_time = 0;               // Updates the running time of the process to 0
            }
        }
    }

    /**
     * Updates the waiting time for each process 
     */
    public static void UpdateWaitingTime() {
        // Loops through global list of all processes and updates the waiting time if a process is on a ready queue
        for (int i=0; i<ProcessesList.size(); i++) {
            Process p = ProcessesList.get(i);           // Temporary variable to hold current process
            if (p.state.equals(READY))                  // if the process is on a ready queue
                p.wait_time++;                          // Increments the waiting time of the process
        }
    }

    /**
     * Updates the Gannt Chart
     */
    public static void UpdateGanntChart() {
        if (cur_running == null)                        // if the CPU is idle
            ganntChart.add(CPU_Idle);                   // Adds a '*' to the Gannt Chart to denote the CPU is idle
        else                                            // if a process is currently running on the CPU
            ganntChart.add(cur_running.name);           // Adds the name of the process currently running on the CPU
    }

    /**
     * Checks if all the processes have left the system
     * @return true if all processes have left the system, false otherwise
     */
    public static boolean CheckExit() {
        for (int i=0; i<ProcessesList.size(); i++)
            if (!ProcessesList.get(i).state.equals(DONE))
                return false;
        return true;
    }

    /**
     * Print the formatted gannt chart,
     * the turnaround time and total ready queue waiting time for each process,
     * the average turnaround time, average total ready queue waiting time,
     * the CPU utilzation, and the throughput.
     */
    public static void Print() {
        String gaant = "";                              // Initialises a String object to store the formatted gannt chart
        double idle_time = 0;                           // Variable to calculate total CPU idle time
        double total_time = ganntChart.size();          // Variable to store total CPU time
        for (int i=0; i<total_time; i++) {              // Loops through the gannt chart array list 
            char ch = ganntChart.get(i);                // Temporary variable to store current element of the gannt chart

            // Formats the Gannt Chart
            if (i%GC_FORMAT == 0 && i>0)                
                gaant += "|";                           
            
            // Formats the output of the Gannt Chart
            if (i == GC_LINE_SIZE)
                gaant += "\n";

            // Updates total CPU idle time
            if (ch == '*')
                idle_time++;

            gaant += ch;                                // Adds current elemt to the gannt chart
        }
        System.out.println("\nGaant Chart:\n" + gaant);   // Prints the gannt chart

        // Calculates CPU utilisation
        double util_CPU = (total_time-idle_time)/total_time*100.0;

        int num_processes = ProcessesList.size();       // Variable to store total number of processes
        double total_turnaround = 0;                    // Variable to calculate total turnaround time of all processes
        double total_wait = 0;                          // Variable to calculate total ready queue waiting time of all processes
        for (int i=0; i<num_processes; i++) {           // Loops through global list of all processes
            Process p = ProcessesList.get(i);           // Temporary variable to hold current process
            int p_turnaround = p.turnaroundTime();      // Variable to store turnaround time of current process
            int p_wait = p.wait_time;                   // Variable to store total ready queue waiting time of current process
            total_turnaround += p_turnaround;           // Updates total turnaround time of all processes
            total_wait += p_wait;                       // Updates total ready queue waiting time of all processes

            // Prints name, turnaround time, and total ready queue of current process
            System.out.println("\nProcess Name: " + p.name);
            System.out.println("Turnaround Time: " + p_turnaround);
            System.out.println("Total Wait Time: " + p_wait);
        }

        // Prints the average turnaround time, average total ready queue waiting time, the CPU utilzation, and the throughput.
        System.out.printf("\nAverage Turnaround Time: %.2f", (total_turnaround/num_processes));
        System.out.printf("\nAverage Wait Time: %.2f", (total_wait/num_processes));
        System.out.printf("\nCPU Utilisation: %.2f", util_CPU);
        System.out.print("%");
        System.out.println("\nThroughput: " + num_processes + " Jobs/" + (int)total_time + " Units\n");
    }
}

/**
 * End of project
 */