package com.gkhotyan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainJava8 {
  private static ConcurrentHashMap<String, HashSet<String>> wordsHashMap = new ConcurrentHashMap<>();
  private static final String FILE_PATH = "c:/sample.txt";

  public static void main(String[] args)
  {
    try {
      Stream<String> stream = Files.lines(Paths.get(FILE_PATH));
      int cores = Runtime.getRuntime().availableProcessors();

      ExecutorService executor = Executors.newFixedThreadPool(cores);
      List<CompletableFuture<Void>> result = stream.map(s1 -> CompletableFuture.runAsync(()-> calculate(s1), executor))
                                                   .collect(Collectors.toList());

      CompletableFuture<Void> allDoneFuture =
        CompletableFuture.allOf(result.toArray(new CompletableFuture[result.size()]));

      allDoneFuture.get();
      executor.shutdown();

      for (Map.Entry<String, HashSet<String>> entry : wordsHashMap.entrySet()) {
        HashSet<String> set = entry.getValue();
//        set.stream().reduce((s, s2) -> s.concat(" ").concat(s2)).ifPresent(System.out::println);
        System.out.println(set.stream().collect(Collectors.joining(" ")));
      }
    }
    catch (IOException | InterruptedException | ExecutionException e) {
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
