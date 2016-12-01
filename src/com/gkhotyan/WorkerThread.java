package com.gkhotyan;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This task works with the concrete word: searches for its anagrams and puts into the map.
 */
public class WorkerThread implements Runnable {

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
            HashSet<String> wordsSet = new HashSet<>();
            wordsSet.add(word);
            wordsHashMap.putIfAbsent(newWord, wordsSet);
        }
        wordsHashMap.get(newWord).add(word);

    }

}
