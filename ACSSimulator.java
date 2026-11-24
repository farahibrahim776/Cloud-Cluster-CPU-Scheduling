import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ACSSimulator {
    private int currentTime = 0;
    private int quantum;
    private int contextSwitch;
    private int agingInterval;
    private List<Process> allProcesses;
    private Tier1 tier1;
    private Tier2 tier2;

    public ACSSimulator(List<Process> processes, int quantum, int contextSwitch, int agingInterval) {
        this.allProcesses = processes;
        this.quantum = quantum;
        this.contextSwitch = contextSwitch;
        this.agingInterval = agingInterval;
        tier1 = new Tier1(quantum);
        tier2 = new Tier2();
    }


    public void runSimulation() {

        int completed = 0;
        int index = 0;

        while (completed < allProcesses.size()) {

            while (index < allProcesses.size() &&
                    allProcesses.get(index).arrivalTime <= currentTime) {

                tier1.addProcess(allProcesses.get(index));
                index++;
            }

            if (!tier1.isEmpty()) {

                Process pr = tier1.getNextProcess();

                if (pr.firstStartTime == -1) {
                    pr.firstStartTime = currentTime;
                }

                int execTime = Math.min(
                        pr.remainingTime,
                        tier1.getQuantum()
                );

                pr.remainingTime -= execTime;
                currentTime += execTime;

                if (pr.remainingTime == 0) {

                    pr.completionTime = currentTime;
                    completed++;

                } else {

                    tier2.addProcess(pr, currentTime);
                }

            }

            else if (!tier2.isEmpty()) {
                tier2.updateAging(currentTime, agingInterval);

                Process pr = tier2.getNextProcess();

                int execTime = pr.remainingTime;

                pr.remainingTime = 0;
                currentTime += execTime;

                pr.completionTime = currentTime;
                completed++;

            }

            else if (index < allProcesses.size()) {

                currentTime = allProcesses.get(index).arrivalTime;
            }
        }
    }

    public void printResults() {
    }
}
