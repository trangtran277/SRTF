import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SRTF {

    static class Task {
        int id;
        int burstTime;
        public Task(int id, int burstTime) {
            this.id = id;
            this.burstTime = burstTime;
        }
    }

    public static void main(String[] args) {
        Comparator<Task> comparator = new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.burstTime - o2.burstTime;
            }
        };
        PriorityQueue<Task> pq = new PriorityQueue<Task>(10, comparator);
        HashMap<Integer, ArrayList<Task>> map = new HashMap<Integer, ArrayList<Task>>();
        int taskNum = 10;
        Scanner input = null;

        if(args.length < 1) {
            generateTasks(pq, map);
        }
        else {
            try {
                input = new Scanner(new File(args[0]));
                readFile(input, map);
                taskNum = map.size();
                pq.addAll(map.get(0));
            }
            catch (NullPointerException e) {           // from File()
                System.out.println("No file specified");
                System.exit(1);
            }
            catch(FileNotFoundException e)          //from Scanner()
            {
                System.out.println("File \"" + args[0] + "\" cannot be open");
                System.exit(1);
            }
        }

        int time = 0;
        double totalWaitTime = 0;
        int lastTaskId = pq.peek().id;

        System.out.print("0 - P" + lastTaskId);

        //process tasks
        while(!pq.isEmpty()) {
            if(time > 0 && map.containsKey(time)) {
                pq.addAll(map.get(time));
                map.remove(time);
            }

            Task shortest = pq.remove();
            if(shortest.id != lastTaskId) {
                System.out.print(" - " + time + " - P" + shortest.id);
                lastTaskId = shortest.id;
            }
            shortest.burstTime--;
            totalWaitTime += pq.size(); //every other task in pq is waiting at this time
            if(shortest.burstTime > 0) {
                pq.add(shortest);
            }
            time++;
        }
        System.out.print(" - " + time);
        System.out.println();
        System.out.println("Waiting time: " + totalWaitTime / (double)taskNum + " ms");
    }

    private static void readFile(Scanner input, HashMap<Integer, ArrayList<Task>> map) {
        int index = 1;
        try
        {
            while(input.hasNextLine()) {
                int arriveTime = input.nextInt();
                int burstTime = input.nextInt();
                addToMap(map, arriveTime, burstTime, index);
                index++;
            }
        }
        catch(NullPointerException e)        // from StringTokenizer()
        {
            System.out.println("Incorrect file format");
            System.exit(1);
        }
        catch(NoSuchElementException e)      // from next() and nextLine()
        {
            //System.out.println("Empty table");
            //e.printStackTrace();
            //System.exit(1);
        }
        catch(NumberFormatException e)       // from parseInt()
        {
            System.out.println("Arrival and burst time must be integer");
            System.exit(1);
        }
    }

    private static void addToMap(HashMap<Integer, ArrayList<Task>> map, int arriveTime, int burstTime, int index) {
        if(!map.containsKey(arriveTime)) {
            map.put(arriveTime, new ArrayList<Task>());
        }
        ArrayList<Task> taskList = map.get(arriveTime);
        taskList.add(new Task(index, burstTime));
    }

    private static void generateTasks(PriorityQueue<Task> pq, HashMap<Integer, ArrayList<Task>> map) {
        Random gen = new Random();
        //System.out.println("id - burst - arrival time");
        //first Task
        pq.add(new Task(1, gen.nextInt(9) + 2));
        //System.out.println(pq.peek().id + " - " + pq.peek().burstTime + " - 0");

        //generate 9 more Tasks
        for(int i = 2; i <= 10; i++) {
            int arriveTime = gen.nextInt(3) + 1;
            int burstTime = gen.nextInt(9) + 2;
            addToMap(map, arriveTime, burstTime, i);
            //System.out.println(i + " - " + burstTime + " - " + arriveTime);
        }
    }
}
