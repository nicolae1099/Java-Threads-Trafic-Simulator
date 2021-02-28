package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.*;

import javax.management.monitor.Monitor;
import java.util.concurrent.BrokenBarrierException;

import static java.lang.Thread.sleep;

/**
 * Clasa Factory ce returneaza implementari ale InterfaceHandler sub forma unor
 * clase anonime.
 */
public class IntersectionHandlerFactory {
    public String currentState = "";
    public static IntersectionHandler getHandler(String handlerType) {
        // simple semaphore intersection
        // max random N cars roundabout (s time to exit each of them)
        // roundabout with exactly one car from each lane simultaneously
        // roundabout with exactly X cars from each lane simultaneously
        // roundabout with at most X cars from each lane simultaneously
        // entering a road without any priority
        // crosswalk activated on at least a number of people (s time to finish all of
        // them)
        // road in maintenance - 2 ways 1 lane each, X cars at a time
        // road in maintenance - 1 way, M out of N lanes are blocked, X cars at a time
        // railroad blockage for s seconds for all the cars
        // unmarked intersection
        // cars racing
        return switch (handlerType) {
            case "simple_semaphore" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    try {
                        Main.semaphore.acquire();
                        System.out.println("Car " + car.getId() + " has reached the semaphore, now waiting...");
                        Thread.sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Car " + car.getId() + " has waited enough, now driving...");
                    Main.semaphore.release();
                }
            };
            case "simple_n_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    System.out.println("Car " + car.getId() + " has reached the roundabout, now waiting...");

                    try {
                        Main.semaphore.acquire();
                        System.out.println("Car " + car.getId() + " has entered the roundabout");
                        Thread.sleep(Main.maxWaiting);
                        System.out.println("Car " + car.getId() + " has exited the roundabout after " +
                                Main.maxWaiting / 1000 + " seconds");
                        Main.semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            case "simple_strict_1_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    System.out.println("Car " + car.getId() + " has reached the roundabout");

                    try {
                        Main.semaphoreList.get(car.getStartDirection()).acquire();
                        System.out.println("Car " + car.getId() + " has entered the roundabout from lane " +
                                car.getStartDirection());

                        Main.customBarrier.await();
                        Thread.sleep(Main.maxWaiting);
                        System.out.println("Car " + car.getId() + " has exited the roundabout after " +
                                Main.maxWaiting / 1000 + " seconds");
                        Main.semaphoreList.get(car.getStartDirection()).release();

                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            };
            case "simple_strict_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {

                    System.out.println("Car " + car.getId() + " has reached the roundabout, now waiting...");
                    try {
                        Main.barrierAllCars.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    try {
                        Main.semaphoreList.get(car.getStartDirection()).acquire();

                        System.out.println("Car " + car.getId() + " was selected to enter the roundabout from lane " +
                                car.getStartDirection());
                        Main.customBarrier.await();
                        System.out.println("Car " + car.getId() + " has entered the roundabout from lane " +
                                car.getStartDirection());
                        Thread.sleep(Main.maxWaiting);
                        Main.customBarrier.await();
                        System.out.println("Car " + car.getId() + " has exited the roundabout after " +
                                Main.maxWaiting/ 1000 + " seconds");
                        Main.customBarrier.await();

                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    Main.semaphoreList.get(car.getStartDirection()).release();
                }
            };
            case "simple_max_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Get your Intersection instance

                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // NU MODIFICATI

                    // Continuati de aici

                   System.out.println("Car " + car.getId() + " has reached the roundabout from lane " +
                           car.getStartDirection());

                    try {
                        Main.semaphoreList.get(car.getStartDirection()).acquire();
                        System.out.println("Car " + car.getId() + " has entered the roundabout from lane " +
                                car.getStartDirection());

                        Thread.sleep(Main.maxWaiting);

                        System.out.println("Car " + car.getId() + " has exited the roundabout after " +
                                Main.maxWaiting/ 1000 + " seconds");
                        Main.semaphoreList.get(car.getStartDirection()).release();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };
            case "priority_intersection" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Get your Intersection instance

                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // NU MODIFICATI

                    // Continuati de aici


                    if (car.getPriority() == 1) {
                        System.out.println("Car " + car.getId() + " with low priority is trying to enter the intersection...");
                        Main.lowPriorityList.add(car.getId());
                    }
                    else  {
                        try {
                            System.out.println("Car " + car.getId() + " with high priority has entered the intersection");
                            Main.highPriorityList.add(car.getId());
                            Thread.sleep(2000);
                            System.out.println("Car " + car.getId() + " with high priority has exited the intersection");
                            Main.highPriorityList.remove(Main.highPriorityList.size() - 1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (Main.highPriorityList.size() == 0 && Main.lowPriorityList.size() > 0) {
                        for (int i = 0; i < Main.lowPriorityList.size(); i++) {
                            System.out.println("Car " + Main.lowPriorityList.get(i) + " with low priority has entered the intersection");
                        }
                        Main.lowPriorityList.clear();
                    }
                }
            };
            case "crosswalk" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    while(!Main.pedestrians.isFinished()) {
                        while (Main.pedestrians.isPass() == true && Main.pedestrians.isFinished() == false) {
                            if (Main.markedCarlist.get(car.getId()) == true) {
                                System.out.println("Car " + car.getId() + " has now red light");
                                Main.isRed = true;
                                Main.markedCarlist.set(car.getId(), false);
                                synchronized (Monitor.class) {
                                    try {
                                        Monitor.class.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                        while (Main.pedestrians.isPass() == false && Main.pedestrians.isFinished() == false) {
                            if (Main.markedCarlist.get(car.getId()) == false) {
                                System.out.println("Car " + car.getId() + " has now green light");
                                Main.markedCarlist.set(car.getId(), true);
                            }
                            Main.isRed = false;
                        }
                    }
                    if (Main.isRed == true) {
                        System.out.println("Car " + car.getId() + " has now green light");
                    }
                }

            };
            case "simple_maintenance" -> new IntersectionHandler() {
                @Override
                public synchronized void handle(Car car) {

                    System.out.println("Car " + car.getId() + " from side number " +
                            car.getStartDirection() + " has reached the bottleneck");
                    try {
                        Main.barrierAllCars.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    if (car.getStartDirection() == 1) {

                        if (Main.direction.get() == 0) {
                            synchronized (Monitor.class) {
                                try {
                                    Monitor.class.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                //System.out.println("ssds");
                                Main.semaphoreList.get(1).acquire();
                                System.out.println("Car " + car.getId() + " from side number 1 has passed the bottleneck");
                                Main.semaphoreList.get(0).release();
                                Main.customBarrier.await();
                                synchronized (Monitor.class) {
                                    Monitor.class.notify();
                                }
                                Main.direction.set(0);
                            } catch (InterruptedException | BrokenBarrierException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (car.getStartDirection() == 0) {
                        if (Main.direction.get() == 1) {
                            synchronized (Monitor.class) {
                                try {
                                    Monitor.class.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                Main.semaphoreList.get(0).acquire();
                                System.out.println("Car " + car.getId() + " from side number 0 has passed the bottleneck");
                                Main.semaphoreList.get(1).release();
                                Main.customBarrier.await();
                                synchronized (Monitor.class) {
                                    Monitor.class.notify();
                                }
                                Main.direction.set(1);
                            } catch (InterruptedException | BrokenBarrierException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            case "complex_maintenance" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    
                }
            };
            case "railroad" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    System.out.println("Car " + car.getId() + " from side number " +
                            car.getStartDirection() + " has stopped by the railroad");
                    Main.lowPriorityQueue.add(car.getId());
                    try {
                        Main.barrierAllCars.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    if (car.getId() == 0) {
                        System.out.println("The train has passed, cars can now proceed");
                    }
                    synchronized (Monitor.class) {
                        while (Main.lowPriorityQueue.peek() != car.getId()) {
                            try {
                                Monitor.class.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Main.lowPriorityQueue.remove();
                        Monitor.class.notifyAll();
                        System.out.println("Car " + car.getId()+ " from side number " +
                                + car.getStartDirection() + " has started driving");
                    }

                }
            };
            default -> null;
        };
    }
}
