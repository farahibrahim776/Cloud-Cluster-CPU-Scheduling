import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ACSSimulator {
    private int currentTime = 0;
    private int quantum;
    private int contextSwitch;
    private List<Process> allProcesses;
    private Tier1 tier1;
    private Tier2 tier2;

    public ACSSimulator(List<Process> processes, int quantum, int contextSwitch) {
        this.allProcesses = processes;
        this.quantum = quantum;
        this.contextSwitch = contextSwitch;
        tier1 = new Tier1(quantum);
        tier2 = new Tier2();
    }

    public void runSimulation() {
        Process pr=tier1.getNextProcess();
        int execTime=Math.min(pr.remainingTime,tier1.getQuantum());
        pr.remainingTime=-execTime;
        currentTime+=execTime;
    }

    public void printResults() {
    }
}
