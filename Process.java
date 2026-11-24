public class Process {
    String id;
    int remainingTime;
    int burstTime;
    int arrivalTime;
    int waitingTime;
    int turnarroundTime;
    int priority;
    public Process(String id,int arrivalTime,int burstTime,int priority){
        this.id=id;
        this.arrivalTime=arrivalTime;
        this.burstTime=burstTime;
        this.remainingTime=burstTime;
        this.priority=priority;
    }
}
