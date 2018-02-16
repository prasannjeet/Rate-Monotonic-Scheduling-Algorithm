import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class RMSImplementation {
    public class Task {
        int p;  //period
        int c;  //computational time
        int r;  //remaining computational time after preemption
        int entryTime;
        int id;

        Task(int period, int cTime, int identification) {
            p = period;
            c = cTime;
            r = cTime;
            id = identification;
        }
    }

    public class ReadyQueue {
        Task lastExecutedTask;
        LinkedList<Task> TheQueue;
        int timeLapsed;

        ReadyQueue() {
            TheQueue = new LinkedList<>();
            timeLapsed = 0;
            lastExecutedTask = null;
        }

        //0 -> Nothing to do
        //1 -> Everything went normally
        //-1 -> Deadline Missed!
        //2 -> Process completely executed without missing the deadline
        int executeOneUnit() throws Exception {
            if (TheQueue.isEmpty()) {
                timeLapsed++;
                return 0;
            }
            timeLapsed++;
            Task T = TheQueue.getFirst();
            //Somehow remaining time became negative, or is 0 from first
            if (T.r <= 0) {
                throw new Exception();
            }
            T.r--;
            if ((T.r + 1 == T.c) || (T != lastExecutedTask && lastExecutedTask != null)) {
                System.out.println("At time " + (timeLapsed - 1) + ", task " + T.id + " has started execution");
                //return 1;
            }

            lastExecutedTask = T;
            //After running if remaining time becomes zero, i.e. process is completely executed
            if (T.r == 0) {
                if (T.entryTime + T.p >= timeLapsed) {
                    System.out.println("At time " + (timeLapsed) + ", task " + T.id + " has been completely executed.");
                    TheQueue.pollFirst();
                    return 2;
                } else {
                    System.out.println("Task " + TheQueue.getFirst().id + " finished at time " + timeLapsed + " thus, missing it's deadline of time " + (TheQueue.getFirst().entryTime + TheQueue.getFirst().p) + ".");
                    TheQueue.pollFirst();
                    return -1;
                }
            }
            return 99;

        }
        //Added task in empty queue -> 0
        //Added identical task to the first task -> 1
        //Added the most prioritized task
        //Previous Process Not Pre-Empted -> 2
        //Previous Process Pre-Empted -> 3
        //Added the second task with less priority -> 4
        //Added task somewhere in the middle -> 5
        //Added the least prioritized task in a list with size more than 2 -> 6
        //Impossible -> 7

        int addNewTask(Task T) {
            if (TheQueue.isEmpty()) {
                TheQueue.addFirst(T);
                T.entryTime = timeLapsed;
                return 0;
            }
            if (T.p == TheQueue.getFirst().p) {
                TheQueue.add(1, T);
                T.entryTime = timeLapsed;
                return 1;
            }
            if (T.p < TheQueue.getFirst().p) {
                boolean tFlag = TheQueue.getFirst().c == TheQueue.getFirst().r;
                TheQueue.addFirst(T);
                T.entryTime = timeLapsed;
                if (tFlag) return 2;
                else {
                    System.out.println("At time " + timeLapsed + ", task " + TheQueue.get(1).id + " has been preempted.");
                    return 3;
                }
            }
            if (T.p > TheQueue.getFirst().p) {
                if (TheQueue.size() == 1) {
                    TheQueue.add(T);
                    T.entryTime = timeLapsed;
                    return 4;
                }
                for (int i = 1; i < TheQueue.size(); i++) {
                    if (T.p < TheQueue.get(i).p) {
                        TheQueue.add(i, T);
                        T.entryTime = timeLapsed;
                        return 5;
                    }
                    if (T.p > TheQueue.get(i).p) {
                        if (i == TheQueue.size() - 1) {
                            TheQueue.add(T);
                            T.entryTime = timeLapsed;
                            return 6;
                        }
                    }
                }
            }
            return 7;
        }
    }

    private static int gcd(int a, int b) {
        while (b > 0) {
            int temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    private static int lcm(int a, int b) {
        return a * (b / gcd(a, b));
    }

    private static int lcm(int[] input) {
        int result = input[0];
        for (int i = 1; i < input.length; i++) result = lcm(result, input[i]);
        return result;
    }

    private static double sigma(LinkedList<Task> taskList) {
        double returnValue = 0.00;
        for (Task eachTask : taskList) {
            returnValue = returnValue + (((double) eachTask.c) / ((double) eachTask.p));
        }
        return returnValue;
    }

    private static double muSigma(int n) {
        return ((double) n) * ((Math.pow((double) 2, ((1 / ((double) n)))) - (double) 1));
    }

    public static void main(String[] args) {
        LinkedList<Task> PeriodicTaskList = new LinkedList<>();
        ReadyQueue ReadyQueue = new RMSImplementation().new ReadyQueue();
        try {
            Scanner scan = new Scanner(System.in);
            int totalTask = scan.nextInt();
            ArrayList<Integer> periodList = new ArrayList<>();

            System.out.println("This task set consists of " + totalTask + " tasks. They are:");
            for (int i = 0; i < totalTask; i++) {
                int period = scan.nextInt();
                periodList.add(period);
                int cTime = scan.nextInt();
                Task tempTask = new RMSImplementation().new Task(period, cTime, i);
                PeriodicTaskList.add(tempTask);
                System.out.println("Task " + tempTask.id + ":\n\tPeriod: " + tempTask.p + "\n\tComputation Time: " + tempTask.c);
            }

            boolean isFailure;
            isFailure = !(sigma(PeriodicTaskList) <= muSigma(PeriodicTaskList.size()));

            if (isFailure) {
                System.out.println("\n" +
                        "\n" +
                        "##Note that this task set does not satisfy the schedulability check,\n" +
                        "therefore there are chances of deadline misses.");
            } else {
                System.out.println("\n" +
                        "\n" +
                        "##Note that this task set satisfies the schedulability check,\n" +
                        "therefore it is definitely schedulable by RMS");
            }

            int[] periodArray = periodList.stream().mapToInt(i -> i).toArray();
            int periodLCM = lcm(periodArray);


            System.out.println("=========================================================\n" +
                    "The Execution Begins:\n" +
                    "=========================================================");
            for (int i = 0; ; i++) {
                //System.out.println("Current Time = "+i);
                //Add tasks to queue
                //for (Task individualTask : PeriodicTaskList){
                for (Task individualTask : PeriodicTaskList) {
                    if (i % individualTask.p == 0) {
                        ReadyQueue.addNewTask(new RMSImplementation().new Task(individualTask.p, individualTask.c, individualTask.id));
                    }
                }

                //Check cycle complete or not
                if (i != 0 && i % periodLCM == 0) {
                    int tempVar = 0;
                    if (ReadyQueue.TheQueue.size() == PeriodicTaskList.size()) {
                        for (int k = 0; k < ReadyQueue.TheQueue.size(); k++) {
                            if (ReadyQueue.TheQueue.get(k).r != ReadyQueue.TheQueue.get(k).c) {
                                tempVar = 1;
                                break;
                            }
                        }
                    }
                    if (tempVar == 0) {
                        System.out.println("End of one complete cycle");
                        return;
                    }
                }

                //Execute Once!
                int output = ReadyQueue.executeOneUnit();

                if (output == -1) {
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Some error occurred. Program will now terminate: " + e);
        }
    }
}

