import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ACSSimulator {
    private int currentTime = 0;
    private int contextSwitch;
    private int agingInterval;

    private List<Process> allProcesses;
    private List<Process> admitted = new ArrayList<>();
    private Process lastProcess;
    private List<String> timeline;
    private Tier1 tier1;
    private Tier2 tier2;
    private String pad = "               ";
    private StringBuilder log = new StringBuilder();
    public ACSSimulator(List<Process> processes, int quantum, int contextSwitch, int agingInterval) {
        this.allProcesses = processes;
        this.tier1 = new Tier1(quantum);
        this.tier2 = new Tier2();

        this.contextSwitch = contextSwitch;
        this.agingInterval = agingInterval;
        this.timeline = new ArrayList<>();
    }
    private void addArrival() {
        for (Process p : allProcesses) {
            if (p.arrivalTime <= currentTime && !admitted.contains(p)) {
                tier1.addProcess(p);
                admitted.add(p);
            }
        }
    }
    private void timeUnitPass(String label, String text) {

        if (text == null)
            text = "Tier 1: " + tier1.snapshot() + "\n" + pad + "Tier 2: " + tier2.snapshot();


        String last = timeline.isEmpty() ? "" : timeline.get(timeline.size() - 1);
        String name = (label.equals(last) || label.equals("CS")) ? "" : label;

        System.out.printf("%-6d%-9s%s%n", currentTime, name, text == null ? "" : text);

        timeline.add(label);
        currentTime++;
        addArrival();
    }

    public void runSimulation() {
        System.out.printf("%-6s%-9s%s%n", "Time", "Process", "Tier");
        int completed = 0;
        addArrival();

        while (completed < allProcesses.size()) {
            if (tier1.isEmpty() && !tier2.isEmpty()) {
                tier2.updateAging(currentTime, agingInterval);
            }

            List<Process> waiting = tier2.getProcesses();
            waiting.sort(Comparator.comparingInt(Process::getTier2EntryTime));

            if (waiting.size() > 1 && waiting.stream().anyMatch(p -> p.getEffectivePriority() != p.getInitialPriority())) {
                for (Process w : waiting) {
                    int waited = currentTime - w.getTier2EntryTime();
                    int levels = waited / agingInterval;
                    int init = w.getInitialPriority();
                    String line = w.id + ": Waited from T=" + w.getTier2EntryTime() + " to T=" + currentTime + " (" + waited + " units).";

                    if (waited > 0)
                        line += "Aging: floor(" + waited + "/" + agingInterval + ") = " + levels + ".";

                    line += "Effective Priority = ";

                    if (waited > 0)
                        line += "max(1, " + init + " - " + levels + ") = ";

                    System.out.println(line + w.getEffectivePriority());
                }
            }

            String before = "Tier 1: " + tier1.snapshot() + "\n" + pad + "Tier 2: " + tier2.snapshot();

            Process p = null;

            if (!tier1.isEmpty())
                p = tier1.getNextProcess();
            else if (!tier2.isEmpty()) {
                p = tier2.getNextProcess();
            }

            if (p == null) {
                lastProcess = null;
                timeUnitPass("Idle", null);
                continue;
            }

            if (lastProcess != null && lastProcess != p && contextSwitch > 0) {
                String note = "Context switch " + (lastProcess.remainingTime > 0 ? "saves " + lastProcess.id + " and loads " : "loads ") + p.id;

                for (int i = 0; i < contextSwitch; ++i) {
                    if (i == 0)
                        timeUnitPass("Cs", note + "\n" + pad);
                    else {
                        timeUnitPass("Cs", null);
                    }
                }
            }

            if (p.firstStartTime == -1)
                p.firstStartTime = currentTime;


            int slice;
            if (p.getTier2EntryTime() == -1)
                slice = Math.min(tier1.getQuantum(), p.remainingTime);
            else {
                slice = p.remainingTime;
            }

            for (int i = 0; i < slice; ++i) {
                if (i == 0)
                    timeUnitPass(p.id, before);
                else {
                    timeUnitPass(p.id, null);
                }
                p.remainingTime--;
            }

            lastProcess = p;
            if (p.remainingTime == 0) {
                p.completionTime = currentTime;
                completed++;
            } else {
                tier2.addProcess(p, currentTime);
            }

            System.out.printf("%-6d%-9sTier 1: %s%n%sTier 2: %s%n", currentTime, "", tier1.snapshot(), pad,
                    tier2.snapshot());
            for (Process pro : allProcesses) {
                pro.turnarroundTime = pro.completionTime - pro.arrivalTime;
                pro.waitingTime = pro.turnarroundTime - pro.burstTime;
                pro.responseTime = pro.firstStartTime - pro.arrivalTime;
            }
        }
    }

    public void printResults() {
        System.out.printf("%n%-9s%-9s%-7s%-8s%-12s%-9s%-9s%n", "Process", "Arrival", "Burst", "Finish", "Turnaround", "Waiting", "Response");
        double totalWT = 0, totalTAT = 0;
        for (Process p : allProcesses) {
            System.out.printf("%-9s%-9d%-7d%-8d%-12d%-9d%-9d%n", p.id, p.arrivalTime, p.burstTime, p.completionTime, p.turnarroundTime, p.waitingTime, p.responseTime);
            totalWT += p.waitingTime;
            totalTAT += p.turnarroundTime;
        }
        System.out.printf("Average Turnaround Time: %.2f%nAverage Waiting Time:     %.2f%n",
                totalTAT / allProcesses.size(), totalWT / allProcesses.size());
    }
    public String getLog() {
        return log.toString();
    }

    public List<String> getTimeline() {
        return timeline;
    }
}