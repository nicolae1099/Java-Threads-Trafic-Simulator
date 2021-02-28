package com.apd.tema2;

import com.apd.tema2.entities.Intersection;
import com.apd.tema2.entities.Pedestrians;
import com.apd.tema2.io.Reader;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static Pedestrians pedestrians = null;
    public static Intersection intersection;
    public static int carsNo;
    public static Semaphore semaphore;
    public static int maxCars = 0;
    public static int maxWaiting;
    public static CyclicBarrier barrierAllCars;
    public static CyclicBarrier customBarrier;
    public static List<Semaphore> semaphoreList = Collections.synchronizedList(new ArrayList<>());
    public static int maxPermits;
    public static List<Integer> lowPriorityList = Collections.synchronizedList(new ArrayList<>());
    public static List<Integer> highPriorityList = Collections.synchronizedList(new ArrayList<>());
    public static volatile BlockingQueue<Integer> highPriorityQueue;
    public static volatile BlockingQueue<Integer> lowPriorityQueue;
    public static volatile List<Boolean> markedCarlist;
    public static volatile boolean isRed = false;
    public static volatile AtomicInteger direction = new AtomicInteger(0);

    public static void main(String[] args) {
        Reader fileReader = Reader.getInstance(args[0]);
        Set<Thread> cars = fileReader.getCarsFromInput();

        for(Thread car : cars) {
            car.start();
        }

        if(pedestrians != null) {
            try {
                Thread p = new Thread(pedestrians);
                p.start();
                p.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(Thread car : cars) {
            try {
                car.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
