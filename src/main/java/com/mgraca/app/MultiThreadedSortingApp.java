package com.mgraca.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class MultiThreadedSortingApp{
  static ArrayList<Integer> list = new ArrayList<Integer>(Arrays.asList(7, 12, 19, 3, 18, 4, 2, 6, 15, 8));
  static int n = list.size();
  static ArrayList<Integer> l1 = new ArrayList<Integer>(list.subList(0, n/2));
  static ArrayList<Integer> l2 = new ArrayList<Integer>(list.subList(n/2 + 1, n));
  static ArrayList<Integer> sortedList = new ArrayList<Integer>();
  
  public static void main(String[] args){
    // define sort thread for first half of array
    Thread t1 = new Thread(new Runnable() {
      public void run(){
        Collections.sort(l1);
        System.out.println("Sorted first half: " + l1.toString());
      }
    });

    // define sort thread for second half of array
    Thread t2 = new Thread(new Runnable() {
      public void run(){
        Collections.sort(l2);
        System.out.println("Sorted second half: " + l2.toString());
      }
    });

    // define merge thread that combines two sorted arrays
    Thread t3 = new Thread(new Runnable() {
      public void run(){
        int j = 0;
        int k = 0;
        for (int i = 0; i < n - 1; i++){
          // take from l1 if l2 has been fully scanned
          if (k >= l2.size()){
            sortedList.add(l1.get(j++));
          }
          // take from l2 if l1 has been fully scanned
          else if (j >= l1.size()){
            sortedList.add(l2.get(k++));
          }
          // determine who to take from if both not fully scanned
          else{
            // take from the list with smallest value
            if (l1.get(j) < l2.get(k)){
              sortedList.add(l1.get(j++));
            }
            else{
              sortedList.add(l2.get(k++));
            }
          }
        }
        // display solution
        System.out.println("Merged sorted array: " + sortedList.toString());
      }
    });

    // run threads
    try{
      t1.start();
      t2.start();
      Thread.sleep(10);  // simple solution to avoid sync issues with merge
      t3.start();
    }
    catch(Exception e){
      System.out.println(e.toString());
    }
  }
}
