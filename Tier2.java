import java.util.PriorityQueue;

public class Tier2 {
    private PriorityQueue<Process> queue;

    public Tier2() {
        queue = new PriorityQueue<>((p1, p2) -> Integer.compare(p1.priority, p2.priority));
}

    public void addProcess(Process p){
        queue.add(p);
    }

     public Process getNextProcess(){
        return queue.poll();
    }

     public boolean isEmpty(){
        return queue.isEmpty();
    }

}
