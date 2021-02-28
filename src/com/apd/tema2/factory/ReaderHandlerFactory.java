package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Pedestrians;
import com.apd.tema2.entities.ReaderHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Returneaza sub forma unor clase anonime implementari pentru metoda de citire din fisier.
 */
public class ReaderHandlerFactory {

    public static ReaderHandler getHandler(String handlerType) {
        // simple semaphore intersection
        // max random N cars roundabout (s time to exit each of them)
        // roundabout with exactly one car from each lane simultaneously
        // roundabout with exactly X cars from each lane simultaneously
        // roundabout with at most X cars from each lane simultaneously
        // entering a road without any priority
        // crosswalk activated on at least a number of people (s time to finish all of them)
        // road in maintenance - 1 lane 2 ways, X cars at a time
        // road in maintenance - N lanes 2 ways, X cars at a time
        // railroad blockage for T seconds for all the cars
        // unmarked intersection
        // cars racing
        return switch (handlerType) {
            case "simple_semaphore" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) {
                    // Exemplu de utilizare:
                    // Main.intersection = IntersectionFactory.getIntersection("simpleIntersection");
                    Main.semaphore = new Semaphore(Main.carsNo);
                }
            };
            case "simple_n_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    // To parse input line use:
                    String[] line = br.readLine().split(" ");
                    Main.maxCars = Integer.parseInt(line[0]);
                    Main.maxWaiting = Integer.parseInt(line[1]);
                    Main.semaphore = new Semaphore(Main.maxCars);
                }
            };
            case "simple_strict_1_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    Main.maxCars = Integer.parseInt(line[0]);
                    Main.maxWaiting = Integer.parseInt(line[1]);
                    Main.customBarrier = new CyclicBarrier(Main.maxCars);

                    for (int i  = 0; i < Main.maxCars; i++) {
                        Main.semaphoreList.add(new Semaphore(1));
                    }
                }
            };
            case "simple_strict_x_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    Main.maxCars = Integer.parseInt(line[0]);
                    Main.maxWaiting = Integer.parseInt(line[1]);
                    Main.maxPermits = Integer.parseInt(line[2]);

                    Main.customBarrier = new CyclicBarrier(Main.maxCars * Main.maxPermits);

                    for (int i  = 0; i < Main.maxCars; i++) {
                        Main.semaphoreList.add(new Semaphore(Main.maxPermits));
                    }
                }
            };
            case "simple_max_x_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    int maxCars = Integer.parseInt(line[0]);
                    int maxPermits = Integer.parseInt(line[2]);
                    Main.maxCars = maxCars;
                    Main.customBarrier = new CyclicBarrier(maxCars * maxPermits);
                    Main.maxPermits = maxPermits;

                    for (int i  = 0; i < maxCars; i++) {
                        Main.semaphoreList.add(new Semaphore(maxPermits));
                    }
                }
            };
            case "priority_intersection" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                }
            };
            case "crosswalk" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    int executeTime = Integer.parseInt(line[0]);
                    int maxNoPedestrians = Integer.parseInt(line[1]);
                    Main.pedestrians = new Pedestrians(executeTime, maxNoPedestrians);
                    Main.markedCarlist = new ArrayList<>(Main.carsNo);
                    for (int i = 0; i < Main.carsNo; i++) {
                        Main.markedCarlist.add(false);
                    }
                }
            };
            case "simple_maintenance" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    int maxPermits = Integer.parseInt(line[0]);
                    Main.maxPermits = maxPermits;
                    Main.customBarrier = new CyclicBarrier(maxPermits);
                    for (int i  = 0; i < 2; i++) {
                        Main.semaphoreList.add(new Semaphore(maxPermits));
                    }
                    Main.lowPriorityQueue = new LinkedBlockingQueue<>();
                    Main.highPriorityQueue = new LinkedBlockingQueue<>();

                }
            };
            case "complex_maintenance" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    
                }
            };
            case "railroad" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    Main.lowPriorityQueue = new LinkedBlockingQueue<>();
                    Main.highPriorityQueue = new LinkedBlockingQueue<>();
                    for (int i = 0; i < 2; i++) {
                        Main.semaphoreList.add(new Semaphore(1));
                    }
                }
            };
            default -> null;
        };
    }

}
