package com.gkhotyan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

/**
 * This task reads lines from file and puts them to the queue.
 */
public class LineReaderTask implements Runnable
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
            e.printStackTrace();
        }
    }

    private void readLines(String filePath) throws InterruptedException {

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
