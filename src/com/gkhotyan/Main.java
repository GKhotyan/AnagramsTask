package com.gkhotyan;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Main class. Responsible for threads starting
 */
public class Main
{
    public static void main(String[] args)
    {
        if(args.length<1){
            throw new IllegalArgumentException("There is no file path");
        }
        String filePath = args[0];
        Path path = Paths.get(filePath);
        if(!Files.exists(path)){
            throw new IllegalArgumentException("There is no file "+filePath);
        }

        final int QUEUE_SIZE = 10;
        final int THREADS_SIZE = 5;

        BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        ConcurrentHashMap<String, HashSet<String>> wordsHashMap = new ConcurrentHashMap<>();

        LineReaderTask enumerator = new LineReaderTask(queue, filePath);
        new Thread(enumerator).start();

        ExecutorService executor = Executors.newFixedThreadPool(THREADS_SIZE);
        boolean done = false;
        try{
            while (!done)
            {
                String str = queue.take();
                if (str.equals(LineReaderTask.END)) {
                    queue.put(LineReaderTask.END);
                    done = true;
                } else {
                    Runnable worker = new WorkerThread(str, wordsHashMap);
                    executor.execute(worker);
                }
            }

            executor.shutdown();
            executor.awaitTermination(1000, TimeUnit.SECONDS);

            for (Map.Entry<String, HashSet<String>> entry : wordsHashMap.entrySet()) {
                HashSet<String> set = entry.getValue();
                if(set.size()>1){
                    set.stream().forEach(s-> System.out.print(s+" "));
                    System.out.println("");
                }
            }
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}





