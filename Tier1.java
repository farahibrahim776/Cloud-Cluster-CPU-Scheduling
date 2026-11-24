import java.util.LinkedList;
import java.util.Queue;

public class Tier1 {
    private LinkedList<Process> queue;
    private int quantum;
    public Tier1(int quantum){
        this.quantum=quantum;
        queue=new LinkedList<>();
    }
    public int getQuantum(){
        return quantum;
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
