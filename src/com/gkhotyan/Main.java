package com.gkhotyan;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class Main
{
    public static void main(String[] args)
    {
        String filePath = "c:/sample.txt";

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
            System.out.println("Map size:" + wordsHashMap.size());

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

/**
 * This task works with the concrete word: searches for its anagrams and puts into the map.
 */
class WorkerThread implements Runnable {

    private String word;
    ConcurrentHashMap<String, HashSet<String>> wordsHashMap;
    public WorkerThread(String word, ConcurrentHashMap<String, HashSet<String>> wordsHashMap)
    {
        this.wordsHashMap = wordsHashMap;
        this.word = word;
    }

    @Override
    public void run() {
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        String newWord = new String(chars);

        if (!wordsHashMap.containsKey(newWord)) {
            HashSet<String> copyOnWriteArraySet = new HashSet<>();
            copyOnWriteArraySet.add(word);
            wordsHashMap.putIfAbsent(newWord, copyOnWriteArraySet);
        }
        wordsHashMap.get(newWord).add(word);

    }

}

/**
 * This task reads lines from file and puts them to the queue.
 */
class LineReaderTask implements Runnable
{
    public static String END = "";

    private BlockingQueue<String> queue;
    private String filePath;

    public LineReaderTask(BlockingQueue<String> queue, String filePath)
    {
        this.queue = queue;
        this.filePath = filePath;
    }

    public void run()
    {
        try
        {
            readLines(filePath);
            queue.put(END);
        }
        catch (InterruptedException e)
        {
        }
    }

    public void readLines(String filePath) throws InterruptedException {

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {

            stream.forEach(s -> putToQueueWrapper(s));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void putToQueueWrapper(String s){
        try {
            queue.put(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

