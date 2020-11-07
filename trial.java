//input is still borken
import java.io.*;
import java.util.ArrayList;

public class trial{
    static ArrayList<Process> InList = new ArrayList<Process>();//In list
    static int inL3;
    
    static ArrayList<Process> H1 = new ArrayList<Process>();//High priority list
    static ArrayList<Process> L2 = new ArrayList<Process>();//first low priority list
    static ArrayList<Process> L3 = new ArrayList<Process>();//second low priority list
    static ArrayList<Process> Blocked = new ArrayList<Process>();//blocked queue
    static ArrayList<Character> ganntList = new ArrayList<Character>(); //gannt chart list
    static int L2Quant = 0;
    static int L3Quant = 0;
    static char CPU = '*'; //* indicates idle CPU
    static int clockTick = -1; //set to -1 initially
    public static void main(String args[])throws Exception{
        if (args.length == 0){//if no file is passed on the command line
            System.out.println("No command line arguments were passed.");
            System.exit(0);
        }
        else{
            File file = new File(args[0]);
            BufferedReader br = new BufferedReader(new FileReader(file));
            L2Quant = Integer.parseInt(br.readLine());
            L3Quant = Integer.parseInt(br.readLine());
            //L2Quant and L3Quant works
            String str = br.readLine();
            while (str != null){
                String ID = str;//process ID
                String priority = br.readLine();
                int aTime = Integer.parseInt(br.readLine());
                String seq = br.readLine();
                ArrayList<Integer> sequence = new ArrayList<Integer>();
                while(Integer.parseInt(seq) != -1){
                    sequence.add(Integer.parseInt(seq));
                    seq = br.readLine();
                }
                InList.add(new Process(ID, priority, aTime, sequence));
                str = br.readLine();
            }
            //run simulation
            while (clockTick < 30){
                clockTick += 1;
                CheckNewJobs();
                BlockJobReturnCheck();
                //The check for current job terminating or blocking is done when we chose a process to run in CpuCheck()
                CpuCheck();
            }
            System.out.println(ganntList);
        }
    }
    public static void CheckNewJobs(){
        if (InList.size() != 0){
        if (InList.get(0).getArrivalTime() == clockTick){
            if (InList.get(0).getPriority().equals("HP")){
                H1.add(InList.get(0));
                InList.remove(0);
                }
            else{
                L2.add(InList.get(0));
                InList.remove(0);
                }
            }
        }

    }
    public static void CpuCheck(){
            //check lists in the following order
            if (!H1.isEmpty()){
                if (H1.get(0) != null){
                CPU = runProcess(H1.get(0));
                ganntList.add(CPU);
                }
            }
            else if (!L2.isEmpty()){
                CPU = runProcess(L2.get(0));
                ganntList.add(CPU);
            }
            else if (!L3.isEmpty()){
                CPU = runProcess(L3.get(0));
                ganntList.add(CPU);
            }
            else{//no processes ready to run
                CPU = '*';
                ganntList.add(CPU);
            }
    }
    public static char runProcess(Process p){
        for ( int i = 0; i < p.getSequence().size(); i++){
            if ( p.getSequence().get(i) != 0){
                if ( i % 2 == 0){//CPU burt
                    CPU = p.getProcessId().charAt(0);
                    p.getSequence().set(i, p.getSequence().get(i) - 1);
                    if ( (i == p.getSequence().size() - 1 && p.getSequence().get(i) == 0)){//process is complete
                        p.setLeavingTime(clockTick);
                        removeProcessFromQueue(p);//remove process from appropriate queue
                    }
                    else if (p.getSequence().get(i) == 0 && i != p.getSequence().size() - 1){//burst is done but process is not complete
                        moveToBlockedQueue(p);
                    }
                    break;
                    }
            }
        }
        return CPU;
    }
    public static void BlockJobReturnCheck(){
        if (!Blocked.isEmpty()){
        for ( int i = 0; i < Blocked.size(); i++){
            for ( int j = 0; j < Blocked.get(i).getSequence().size(); j++){
                if ( Blocked.get(i).getSequence().get(j) != -1 && j % 2 != 0){
                    Blocked.get(i).getSequence().set(j, Blocked.get(i).getSequence().get(j) - 1);
                    System.out.println("ClockTick = " + clockTick);
                    Blocked.get(i).print();
                    System.out.println();
                    if (Blocked.get(i).getSequence().get(j) == -1){//done blocking
                        //-1 because CPU check happens after block job return check
                            moveToReadyQueue(Blocked.get(i));
                     }
                     else if (Blocked.get(i).getSequence().get(j) == 0 && CPU != '*'){
                            moveToReadyQueue(Blocked.get(i));
                     }
                     break;
                    }
                }
            }
        }
    }
    public static void moveToReadyQueue(Process p){
        //gotta remove from blocked queue and add to ready queue
        Blocked.remove(p);
        if (p.getPriority().equals("HP")){
            H1.add(p);
        }
        else if (!p.inL3){
            L2.add(p);
        }
        else{
            L3.add(p);
        }
    }
    public static void removeProcessFromQueue(Process p){
        if (p.getPriority().equals("HP")){
            H1.remove(p);
        }
        else if (!p.inL3){
            L2.remove(p);
        }
        else{
            L3.remove(p);
        }
    }
    public static void moveToBlockedQueue(Process p){
            boolean flag = false;
            for (int i = 0; i < Blocked.size(); i++){
                if (p.getProcessId() == Blocked.get(i).getProcessId()){
                    flag = true;
                }
            }
            if (!flag){
            if( p.getPriority().equals("HP")){
                H1.remove(p);
            }
            else if(!p.inL3){
                L2.remove(p);
            }
            else{
                L3.remove(p);
            }
            Blocked.add(p);
            }
    }
}
