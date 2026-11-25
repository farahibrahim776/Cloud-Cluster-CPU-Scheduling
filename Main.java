
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("--- CPU Scheduling Simulator ---");
        System.out.println("1. Preemptive SJF");
        System.out.println("2. ACS ");
        System.out.print("Choose algorithm: ");

        int choice = sc.nextInt();

        while (choice != 1 && choice != 2) {
            System.out.print("Invalid choice! Choose 1 or 2: ");
            choice = sc.nextInt();
        }
        System.out.print("Number of processes: ");
        int n = sc.nextInt();

        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String id = "P" + (i + 1);

            System.out.print(id + " -> arrival: ");
            int arrival = sc.nextInt();

            System.out.print(id + " -> burst: ");
            int burst = sc.nextInt();

            System.out.print(id + " -> priority: ");
            int priority = sc.nextInt();

            processes.add(new Process(id, arrival, burst, priority));
        }

        System.out.print("Context-switch: ");
        int cs = sc.nextInt();

        if (choice == 1) {
            System.out.println("\n===== Preemptive SJF =====");

            SJFPreemptive sjf = new SJFPreemptive();

            List<String> chart = sjf.execute(processes, cs);

            System.out.println("\nGantt Chart:");

            printGanttChart(chart);

            printResults(processes);

        } else {
            System.out.print("Quantum: ");
            int quantum = sc.nextInt();

            System.out.print("Aging-interval: ");
            int agingInterval = sc.nextInt();

            while (agingInterval <= 0) {
                System.out.println("Aging-interval should be greater than zero!");

                System.out.print("Aging-interval: ");
                agingInterval = sc.nextInt();
            }
            System.out.println("\n===== ACS (Q=" + quantum + ", CS=" + cs + ", aging=" + agingInterval + ") =====");
            ACSSimulator simulator = new ACSSimulator(processes, quantum, cs, agingInterval);

            simulator.runSimulation();
            simulator.printResults();
        }

        sc.close();
    }
    public static void printGanttChart(List<String> chart) {
        List<String> processes = new ArrayList<>();
        List<Integer> durations = new ArrayList<>();

        for (int i = 0; i < chart.size(); i++) {
            String current = chart.get(i);
            if (!processes.isEmpty() && processes.get(processes.size() - 1).equals(current)) {
                int last = durations.size() - 1;
                durations.set(last, durations.get(last) + 1);
            } else {
                processes.add(current);
                durations.add(1);
            }
        }

        for (String p : processes) {
            int width = Math.max(5, p.length() + 2);
            System.out.print("+" + "-".repeat(width) + "+");
        }

        System.out.println();
        for (String p : processes) {
            int width = Math.max(5, p.length() + 2);
            int spaces = width - p.length();
            int left = spaces / 2;
            int right = spaces - left;

            System.out.print("|");
            System.out.print(" ".repeat(left));
            System.out.print(p);
            System.out.print(" ".repeat(right));
        }

        System.out.println("|");

        System.out.print("+");
        for (String p : processes) {
            int width = Math.max(5, p.length() + 2);
            System.out.print("-".repeat(width) + "+");
        }
        System.out.println("\n");

        int time = 0;

        System.out.print(time);
        for (int i = 0; i < processes.size(); i++) {
            int width = Math.max(5, processes.get(i).length() + 2);
            int previousTime = time;

            time += durations.get(i);
            int spaces = width + 1 - String.valueOf(previousTime).length();
            System.out.print(" ".repeat(Math.max(1, spaces)));
            System.out.print(time);
        }
        System.out.println();
    }
    public static void printResults(List<Process> processes) {

        System.out.printf("%n%-9s%-9s%-7s%-8s%-12s%-9s%-9s%n", "Process", "Arrival", "Finish", "Turnaround", "Waiting");

        double totalWT = 0;
        double totalTAT = 0;

        for (Process p : processes) {
            System.out.printf("%-9s%-9d%-7d%-8d%-12d%-9d%-9d%n", p.id, p.arrivalTime, p.completionTime, p.turnarroundTime, p.waitingTime);
            totalWT += p.waitingTime;
            totalTAT += p.turnarroundTime;
        }
        System.out.printf("Average Turnaround Time: %.2f%n", totalTAT / processes.size());
        System.out.printf("Average Waiting Time: %.2f%n", totalWT / processes.size());
    }
}
