import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Simulation {
    static ArrayList<Process> ProcessesList = new ArrayList<Process>();//In list
        
    static ArrayList<Process> H1 = new ArrayList<Process>();//High priority list
    static ArrayList<Process> L2 = new ArrayList<Process>();//first low priority list
    static ArrayList<Process> L3 = new ArrayList<Process>();//second low priority list
    static ArrayList<Process> Blocked = new ArrayList<Process>();//blocked queue
    static ArrayList<Character> ganntChart = new ArrayList<Character>(); //gannt chart list

    static Process cur_running;

    static int MAX_BURST = 20;
    static int MAX_PREEMPTED = 3;
    static String ready = "READY";
    static String running = "RUNNING";
    static String blocked = "BLOCKED";
    static String done = "DONE";

    static String high = "HP";
    static String low = "LP";
    static String low2 = "L2";
    static String low3 = "L3";

    static int inL3;
    static int L2quant = 0;
    static int L3quant = 0;
    static char CPU_Idle = '*'; //* indicates idle CPU
    static int clockTick = -1; //set to -1 initially

    public static void main(String args[])throws Exception {
        if (args.length == 0) {//if no file is passed on the command line
            System.out.println("No command line arguments were passed.");
            System.exit(0);
        }

        else {
            File file = new File(args[0]);
            readFile(file);

            //run simulation
            while (true) {
                clockTick++;
                CheckNewJobs(clockTick);
                BlockedJobReturnCheck(clockTick);
                CPUJobDoneCheck(clockTick);
                QuantumCheck(clockTick);
                PickProcess(clockTick);
                UpdateWaitingTime();

                if (CheckExit())
                    break;
                UpdateGanntChart();
            }
            Output();
        }
    }

    public static void print(ArrayList<Process> list) {
        for (int i=0; i<list.size(); i++) 
            System.out.print(list.get(i).name + " ");
        System.out.println();
    }

    public static void readFile(File file) {
        try {
            Scanner scanner = new Scanner(file);

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
                if (p.priority.equals(high)) {
                    p.state = ready;
                    H1.add(p);
                }
                else {
                    p.state = ready;
                    p.priority = low2;
                    L2.add(p);
                }
            }
        }
    }

    public static void BlockedJobReturnCheck(int clockTick) {
        for (int i=0; i<Blocked.size(); i++) {
            Process p = Blocked.get(i);
            if (p.state.equals(blocked)) {
                if (p.blocked_till == clockTick) {
                    if (p.priority.equals(high)) {
                        p.state = ready;
                        p.burst_index++;
                        H1.add(p);
                        Blocked.remove(i);
                    }
                    else {
                        p.state = ready;
                        p.burst_index++;
                        p.priority = low2;
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
                    cur_running.state = done;
                    cur_running = null;
                }
                else {
                    cur_running.state = blocked;
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
            if (cur_running.priority.equals(low2)) {
                if (cur_running.run_time == L2quant) {
                    cur_running.num_preempted++;
                    cur_running.burst_info[cur_running.burst_index] -= L2quant;
                    cur_running.state = ready;
                    if (cur_running.num_preempted == MAX_PREEMPTED) {
                        cur_running.priority = low3;
                        L3.add(cur_running);
                        cur_running = null;
                    }
                    else {
                        L2.add(cur_running);
                        cur_running = null;
                    }
                }
            }
            else if (cur_running.priority.equals(low3)) {
                if (cur_running.run_time == L3quant) {
                    cur_running.burst_info[cur_running.burst_index] -= L3quant;
                    cur_running.state = ready;
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
                cur_running.state = running;
                cur_running.run_till = clockTick + cur_running.burst_info[cur_running.burst_index];
                cur_running.run_time = 0;
                //ganntChart.add(cur_running.name);
            }

            else if (!L2.isEmpty()) {
                cur_running = L2.remove(0);
                cur_running.state = running;
                cur_running.run_till = clockTick + cur_running.burst_info[cur_running.burst_index];
                cur_running.run_time = 0;
                //ganntChart.add(cur_running.name);
            }
            else if (!L3.isEmpty()) {
                cur_running = L3.remove(0);
                cur_running.state = running;
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
            if (p.state.equals(ready))
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
            if (!ProcessesList.get(i).state.equals(done))
                b = false;
        return b;
    }

    public static void Output() {
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