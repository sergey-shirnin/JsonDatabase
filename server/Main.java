package server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    private final static int PORT = 1_666;

    final static String dbPath = System.getProperty("user.dir") + "/JSON Database/task/src/server/data/db.json";
    final static String dbPathTest = System.getProperty("user.dir") + "/src/server/data/db.json";

    final static Database db = new Database();

    final static int awaitTime = 800;

    static ReadWriteLock lock = new ReentrantReadWriteLock();
    static Lock readLock = lock.readLock();
    static Lock writeLock = lock.writeLock();

    static int threads = Runtime.getRuntime().availableProcessors();
    static ExecutorService executor = Executors.newFixedThreadPool(threads);
    static List<Runnable> unfinishedTasks;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.printf("Server started @ %d threads!%n", threads);
            while (!executor.isShutdown()) {
                Socket socket = server.accept();
                System.out.println("New client session started!");

                if (executor.submit(new Session(socket)).get()) {
                    executor.shutdown();

                    if (!executor.awaitTermination(awaitTime, TimeUnit.MILLISECONDS)) {
                        unfinishedTasks = executor.shutdownNow();
                        System.out.println("Some requests not processed before termination:" +
                                "\n-> " + unfinishedTasks);
                    } else {
                        System.out.println("All requests processed before termination");
                    }
                }
            }
        } catch (IOException e) {
            System.out.printf("Server set up failed!\nMsg:%s%n",
                    e.getMessage());
        } catch (ExecutionException e) {
            System.out.printf("Client session failed at execution!\nMsg:%s%n",
                    e.getMessage());
        } catch (InterruptedException e) {
            System.out.printf("Client session interrupted at execution!\nMsg:%s%n",
                    e.getMessage());
        }
    }
}
