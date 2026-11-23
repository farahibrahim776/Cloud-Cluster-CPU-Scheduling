import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ACSSimulator {
    private int currentTime = 0;
    private int quantum;
    private int contextSwitch;
    private List<Process> tier1;
    private List<Process> tier2;
    private List<Process> allProcesses;

    public ACSSimulator(List<Process> processes, int quantum, int contextSwitch) {
        this.allProcesses = processes;
        this.quantum = quantum;
        this.contextSwitch = contextSwitch;
        tier1 = new LinkedList<>();
        tier2 = new ArrayList<>();
    }

    public void runSimulation() {
    }

    public void printResults() {
    }
}
