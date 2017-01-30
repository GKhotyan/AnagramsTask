package com.gkhotyan;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainJava8 {
  private static ConcurrentHashMap<String, HashSet<String>> wordsHashMap = new ConcurrentHashMap<>();


  public static void main(String[] args)
  {
    if (args.length < 1) {
      throw new IllegalArgumentException("There is no file path");
    }
    String filePath = args[0];

    Path path = Paths.get(filePath);
    if (!Files.exists(path)) {
      throw new IllegalArgumentException("There is no file " + filePath);
    }

    try {
      Stream<String> stream = Files.lines(Paths.get(filePath));

      int cores = Runtime.getRuntime().availableProcessors();

      ExecutorService executor = Executors.newFixedThreadPool(cores);
      List<CompletableFuture<Void>> result = stream.map(s1 -> CompletableFuture.runAsync(()-> calculate(s1), executor))
                                                   .collect(Collectors.toList());

      CompletableFuture<Void> allDoneFuture =
        CompletableFuture.allOf(result.toArray(new CompletableFuture[result.size()]));

      try {
        allDoneFuture.get();
        executor.shutdown();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }

      for (Map.Entry<String, HashSet<String>> entry : wordsHashMap.entrySet()) {
        HashSet<String> set = entry.getValue();
        if(set.size()>1){
          set.stream().forEach(s-> System.out.print(s+" "));
          System.out.println("");
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void calculate(String word)  {

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
