# System Simulation

Each program is to be the effort of the team members submitting it.  Team members may help each other, but no other individual, in the class or not, should be assisting with designing or writing the code or test cases.
*********************************************************************
CPSC 315
Programming Assignment 1
Due: Friday, November 6, 11:59 p.m. (no late assignments accepted)

Objective:  To gain experience in implementing a system simulation.

Program:  For this program, you are to write a system simulation which meets the following specifications:  An external priority is assigned to all processes entering the system.  When entering the system, jobs are designated as High Priority (HP) or Low Priority (LP).  The CPU scheduling strategy is a 3-level priority-based system.  Ready queue H1, which is the High Priority (HP) job queue, has the highest priority and is scheduled non-preemptive First Come First Served.  Queues L2 and L3 are for Low Priority (LP) jobs.  Queue L2 has the second highest priority overall (and is the highest priority queue for LP jobs) and is scheduled Round Robin with a quantum designated L2quant, which will be provided in the input file for the simulation.  Queue L3 is the lowest priority overall (and is the lower priority queue for LP jobs) and is scheduled Round Robin with a quantum of L3quant, which will also be provided in the input file for the simulation.  A job is selected to run from the L2 queue only if there are no ready H1 jobs.  Similarly, a job is only selected to run from the L3 queue only if there are no ready jobs on the H1 or L2 ready queues. If a lower priority job is running when a higher priority job becomes ready, the lower priority job will NOT be preempted.  Rather, the running job will be allowed to complete its current time allotment on the CPU (i.e. will remain on the CPU until it blocks or is preempted at the end of its quantum), after which the highest priority ready job will be chosen to run.

	High priority jobs will only ever be placed on the highest priority (H1) queue.  They will never be placed on or migrate onto queues L2 or L3. However, LP jobs can migrate between the L2 and L3 queues.  The aim of the migration strategy is to lower the priority of LP jobs that are “acting CPU bound” in their CURRENT CPU burst, in which “acting CPU bound” means having a CPU burst length that is larger than 3 times the L2quant, the quantum length of the L2 queue.  Thus, an LP job which enters the system is automatically placed on queue L2.  If at any point an LP job which had been chosen from the L2 queue has been preempted 3 times due to reaching the end of its quantum for the CURRENT CPU burst, the job will be demoted to the L3 queue. A low priority job returning from being blocked will be placed on queue L2, since, on return, it has not been preempted in its current CPU burst. 

As an example, assume a low priority (LP) job A is on queue L2 and has a current remaining CPU burst size of 12, and a low priority (LP) job B on queue L3 with current remaining CPU burst of size 13. Further, assume there are no HP jobs ready and there are no other ready jobs. Finally assume that the quantum for queue L2 is 2. The CPU is currently idle so job A is selected to run and runs for its first quantum of 2.  At the end of its quantum, since there are no other higher or equal priority jobs ready, job A would be assigned another quantum of 2. At the end of that quantum, it will still have 8 units left in its current CPU burst. However, assume that at this point, a High Priority job, C, is waiting on the H1 ready queue.  C will be chosen to run and Job A will be placed onto queue L2.  After job C, completes, Job B will again be chosen to run and preempted again (remember, it is still on its original CPU burst).  However, at this point, it had used up an entire quantum 3 consecutive times while working on a single CPU burst, so it will be demoted. Since there is a ready job B on queue L3, job A will be removed from the CPU and placed onto the back of the L3 queue and, job B will be chosen to run.  Remember however, being on the L3 queue is only designed to demote a job that seems to be currently experiencing a long CPU burst.  So be careful that when an LP job comes back from being blocked, it will always be placed on the L2 queue. Again, it only migrates to being placed on the L3 queue if the current burst length is more than 3 times the quantum associated with the L2 queue. And it will remain on the lowest priority level until it returns from being blocked after the end of its current CPU burst.


Input Files for Simulation

A simulation input test file will consist first of, on separate lines, the values for L2quant and L3quant, followed by a sequence of job descriptions of the form:

Job id (a single character)
Priority (HP or LP)
Arrival time
CPU burst length
Blocked Time
CPU burst length
Etc
-1(delimits the end of this job description)

For example, the file data

3
6
A
HP
0
3
4
5
-1
B
LP
5
6
2
3
1
4
-1

It indicates that, for this particular simulation, the value of L2quant is 3, the value of L3quant is 6. It then indicates that a High Priority job, A, arrives at time 0 and consists of a CPU burst of 3 followed by a blocked time of 4 followed by a CPU burst of 5, and B, a Low Priority job, arrives at time 5 and consists of a CPU burst of 6 followed by a blocked time of 2 followed by a CPU burst of 3, a blocked time 1 and a CPU burst of 4. 

As in the homework, the blocked time indicates the length of time that passes between when the job is taken off the CPU due to it blocking and the time it is ready again.  So if a job reaches the end of its CPU burst at time 10 to begin a blocked time of 2, it will be ready again at time 12.

Note:

•	Jobs will be ordered in the file according to arrival time, thus you can read the descriptions as needed one at a time.  It is not necessary to read all of the descriptions at once.  As with many aspects of the simulation, how your group chooses to implement it is up to you.

•	You may assume that the maximum number of bursts for any one job is 10 (however it may be less).  Also, different jobs can naturally have different numbers of bursts.

•	Every job begins and ends with a CPU burst.

Output

Gantt Chart 

Your program is to output a Gantt chart depicting the CPU activity.  For each unit of time, the character identifier of the job currently on the CPU is to be output.  An * is to be output to indicate a unit during which the CPU is idle.  A vertical tick mark ( | )should be output every 5 ticks.  For example, a portion of a Gantt chart may appear as:

AAABB|BAA**|*CCCA|A

The Gantt chart is to meet the following specifications:

•	The Gantt chart is to be output horizontally.

•	There should be no more than 40 units output on any one line of the chart.  If the chart extends beyond 40 units, it should be displayed on multiple lines.

•	You may assume that each chart will be no more that 80 units in length.

•	You may want to build you Gantt chart as a simple list or array, and format it when are you ready to output it.

Other Metrics

In addition, for EACH process you are to output its turnaround time, its total ready queue waiting time.  The average turnaround time and average total ready queue waiting times for all process is to be output, as the CPU utilization and throughput.  Proper and clear labeling and formatting of all output are expected.


Implementation requirements and assumptions:

•	Your program can be written in Python, Java or C/C++.  It goes without saying, that both team members must be comfortable with the programming language used.

•	Your program is to be implemented using solid program structuring and documentation techniques (ie. Comments (heading and section), meaningful variable names, not having meaning literal values in your code but rather must be held in a variable with a meaningful name, clearly readable code (use blank lines), clearly readable and labelled output (use blank lines) etc).

•	Your programs will be tested against a series of test files…and you will be there for the event.  How exciting!

•	You may assume that there will be at most 10 active processes at any one time, although there may be more that 10 processes in the system over time.

•	Each burst length is expressed in clock ticks and it is assumed that context switch time in negligible and therefore is ignored.

•	Test your program in small increments.  For instance, make sure the input information is being read in properly and stored. Then, for instance,  test if your scheduling works with it 1 HP process (Are the CPU bursts, blocking times being handled correctly?), then 1 LP process (is Round Robin working correctly?), then 1 LP process that migrates onto queue L3 due to its burst length (is the migration working correctly? Is the job going back on the queue L2 when it returns from being blocked?) etc.  As this is an upper level class, you are required to design your own test cases to make sure your program is working correctly. And I will also be creating test cases to test your programs.

Logical Structure of the Simulation

The following loop should serve as a basic outline of the main body of your simulation:

/* The simulation is driven by incrementing the variable representing your clock by 1 
    and then determining what actions will take place in the system at that (current)  time.          
    The loop body contains all checks which are to be made on the current (clock) “time”.
*/

Loop
	clock = clock + 1;		/* advance the time */
	Check_new_jobs;		/* check if new jobs are entering the system */
	Block_job_return_check;	/* check if job on is returning from being blocked */
	Cpu_job_done_check;		/* check if current running job terminates/blocks */
	Quantum_check;		/* check if current running job’s quantum expired */
	Cpu_check;			/* check if CPU idle and if so pick job to run */
	
Note:  The order in which the above checks are listed dictates the order with which jobs being placed on the queue “at the same time” will in fact be placed on the queue.


Submitting Assignment

You are to drop 3 documents: your program file, a description of the division of labor, and a testing document, as described below.

When your program is complete, drop the file into the appropriate moodle folder of each team member.  You must also drop a document that indicates how the work was divided and implemented between team members.  It is expected that, while some design will need to be agreed upon together, such as the main data structures, that there is also a division of labor when it comes to coding (i.e. One person should not be doing all the coding).  Both partners will be expected to be able to discuss and answer questions about your design, implementation and coding decisions. All the test files that your team has designed should also be contained in a single pdf document, with a short notation before each test case indicating what aspects of the simulation your case is testing.
