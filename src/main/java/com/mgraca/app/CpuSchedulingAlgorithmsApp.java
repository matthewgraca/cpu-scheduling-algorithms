package com.mgraca.app;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Comparator;

public class CpuSchedulingAlgorithmsApp{
  final static int pid = 0;
  final static int arrive = 1;
  final static int burst = 2;
  final static int priority = 3;

  public static void main(String[] args)
  {
    Scanner inputReader = new Scanner(System.in);

    // ask for input.txt file and store the process info
    ArrayList<int[]> processList = initializeProcessList(inputReader); 
    printList(processList);

    // ask for choice in scheduling algo
    promptAlgorithmChoice(inputReader, processList);
  }

  /**
   * Prints the contents of an arraylist of int arrays
   * @param arr the arraylist of int arrays
   */
  private static void printList(ArrayList<int[]> arr){
    for (int i = 0; i < arr.size(); i++){
      System.out.println(Arrays.toString(arr.get(i)));
    }
  }

  /**
   * Menu that asks for the user to input a file to read
   * @param inputReader the object that reads input from the user
   * @return the arraylist of int arrays that contains the info about the processes
   */
  private static ArrayList<int[]> initializeProcessList(Scanner inputReader){
    System.out.print("Enter the name of the file containing the processes: ");
    String fileName = inputReader.next();

    ArrayList<int[]> processList = new ArrayList<>();
    printList(processList);
    int processFields = 4;
    try{
      File file = new File(fileName);
      Scanner fileReader = new Scanner(file);
      while (fileReader.hasNextInt()){
        int[] processInfo = new int[processFields];
        for (int i = 0; i < processFields; i++){
          processInfo[i] = fileReader.nextInt();
        }
        processList.add(processInfo);
      }
      fileReader.close();
    }
    catch (FileNotFoundException e){
      e.printStackTrace();
    }
    return processList;
  }

  /**
   * Menu that asks for user's choice in scheduling algorithm
   * @param inputReader the object that reads input from the user
   * @return the time quantum if RR was chosen
   */
  private static void promptAlgorithmChoice(Scanner inputReader, ArrayList<int[]> processList){
    System.out.println("Please your choice of scheduling algorithm:");
    System.out.println("1) First Come First Serve (FCFS)");
    System.out.println("2) Shortest Job First (SJF)");
    System.out.println("3) Preemptive Priority Scheduling");
    System.out.println("4) Round Robin (RR)");

    int algoChoice = inputReader.nextInt();
    switch (algoChoice){
      case 1:
        System.out.println("You have chosen FCFS");
        firstComeFirstServe(processList);
        break;
      case 2:
        System.out.println("You have chosen SJF");
        shortestJobFirst(processList);
        break;
      case 3:
        System.out.println("You have chosen PPS");
        preemptivePriorityScheduling(processList);
        break;
      case 4: // ask for time quantum if RR is chosen
        System.out.println("You have chosen RR");
        System.out.print("Please input a time quantum: ");
        roundRobin(processList, inputReader.nextInt());
        break;
      default:
        System.out.println("Invalid choice, exiting program.");
        System.exit(0);
    }
  }

  /**
   * Runs the processes on a list as they are; currently only fit for fcfs and sjf
   * @param processList the list of processes
   */
  private static void runProcesses(ArrayList<int[]> processList){
    int n = processList.size();

    for (int i = 0; i < n; i++){
      int total = processList.get(i)[burst];
      int progress = 0;
      // display progress
      while (progress <= total){
        try{
          System.out.print("\rProcess " + processList.get(i)[pid] + " progress: " + progress + "/" + total);
          progress++;
          Thread.sleep(100); // delay to see progress
        }
        catch (InterruptedException e){
          e.printStackTrace();
        }
      }
      System.out.println("\nProcess id " + processList.get(i)[pid] + " is finished.");
    }

    // timings for processes that are run contiguously
    int totalTime = 0;
    int totalWaitTime = 0;
    int totalTurnaroundTime = 0;
    int sumOfPrevTimes = 0;
    for (int i = 0; i < n; i++){
      totalTurnaroundTime += sumOfPrevTimes - processList.get(i)[arrive] + processList.get(i)[burst];
      totalWaitTime += sumOfPrevTimes - processList.get(i)[arrive];
      sumOfPrevTimes += processList.get(i)[burst];
    }

    // calc averages
    double avgWaitTime = (double)totalWaitTime / n; 
    double avgTurnaroundTime = (double)totalTurnaroundTime / n;

    System.out.println("avg wait time: " +  avgWaitTime + ", avg turnaround time: " + avgTurnaroundTime);
  }

  /**
   * Displays the progression of the pps algorithm
   * @param processList a list of the processes and their attributes
   */
  private static void printPPSProgress(ArrayList<int[]> processList){
    int n = processList.size();
    int[] burstTimes = new int[n];
    for (int i = 0; i < n; i++){
      burstTimes[i] = processList.get(i)[burst];
    }

    int count = 0;
    int timer = 0;
    int currentPriority = Integer.MAX_VALUE;
    int currentProcess = 0;
    int prevProcess = -1;
    while (count != n){
      // display current progress
      if (prevProcess != currentProcess){
        System.out.println();
      }
      int burstTimeFormatted = processList.get(currentProcess)[burst] - burstTimes[currentProcess];
      System.out.print("\rProcess " + processList.get(currentProcess)[pid] + " progress: " + burstTimeFormatted + "/" + processList.get(currentProcess)[burst]);
      prevProcess = currentProcess;

      // check for newer processes 
      for (int i = 0; i < n; i++){
        // if a new process has arrived
        if (timer == processList.get(i)[arrive]){
          // and the process has a higher priority
          if (processList.get(i)[priority] < currentPriority){
            // then pause the current process and run the higher priority one
            currentProcess = i;
            currentPriority = processList.get(i)[priority];
          }
        }
      }

      // if current process is done, run the highest priority process that has arrived
      if (burstTimes[currentProcess] == 0){
        System.out.println("\nProcess " + processList.get(currentProcess)[pid] + " has finished.");
        count++;  // denote that a process is finished
        currentPriority = Integer.MAX_VALUE;  // reset lowest priority
        // search through the processes
        for (int i = 0; i < n; i++){
          // if process has arrived and has the best priority and is not finished,  set it as the current process
          if (processList.get(i)[arrive] < timer && processList.get(i)[priority] < currentPriority && burstTimes[i] != 0){
            currentProcess = i;
            currentPriority = processList.get(i)[priority];
          }
        }
        // if no process fits, just increment timer
      }

      // run the process
      try{
        // increment timer and decrement burst timer
        burstTimes[currentProcess]--;
        timer++;
        Thread.sleep(500);  // visually see progress
      }
      catch (InterruptedException e){
        e.printStackTrace();
      }
    }
  }

  /**
   * Implementation of the fcfs scheduling algorithm
   * @param processList the list of processes and their attributes
   */
  public static void firstComeFirstServe(ArrayList<int[]> processList){
    // sort by arrival time
    Collections.sort(processList, new Comparator<int[]>(){
      public int compare(int[] a, int[] b){
        return Integer.compare(a[1], b[1]); // int[1] is the arrival time
      }
    });

    // print order
    System.out.println("Process order: ");
    printList(processList);

    // run the processes
    runProcesses(processList);
  }

  /**
   * Implementation of the sjf scheduling algorithm
   * @param processList the list of processes and their attributes
   */
  public static void shortestJobFirst(ArrayList<int[]> processList){
    // establish process order
    Collections.sort(processList, new Comparator<int[]>(){
      public int compare(int[] a, int[] b){
        return Integer.compare(a[2], b[2]); // int[2] is the job length
      }
    });

    // print order
    System.out.println("Process order: ");
    printList(processList);

    // run processes
    runProcesses(processList);
  }

  /**
   * Implementation of the preemptive priority scheduling algorithm
   * @param processList the list of processes and their attributes
   */
  public static void preemptivePriorityScheduling(ArrayList<int[]> processList){
    // sort by arrival time
    Collections.sort(processList, new Comparator<int[]>(){
      public int compare(int[] a, int[] b){
        return Integer.compare(a[1], b[1]); // int[1] is the arrival time
      }
    });

    // print progress
    printPPSProgress(processList);
  }

  /**
   * Implementation of the round robin scheduling algorithm
   * @param processList the list of processes and their attributes
   * @param quantum     the time quantum for each job
   */
  public static void roundRobin(ArrayList<int[]> processList, int quantum){

  }
}
