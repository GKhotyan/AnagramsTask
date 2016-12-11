#### Task
Given an input file which contains one word per line, as an output construct a list of all anagrams from that input file. Print those words to the console, where all words that are an anagram should each other should be on the same line.

####Design decisions
We have two classes:
1 - MainJava8 uses the features of JDK 8 - CompletableFuture class:
It uses for the asynchronous calculation. It based on ForkJoinFramework and allow for each task to makes parallel reading words from the File, makes with them executions and put to the concurrentHashMap. 
Numbers of tasks(threads) defines in THREADS_SIZE variable. It could be changed depending on input file size.

2 - Main uses features of the older JDK versions:
One thread reads file line by line and puts them to the blocking queue.
Blocking queue has maximum size defined in variable QUEUE_SIZE in order to be able to stop the reading process and wait for executing tasks.
Each of this tasks use thread for parallel reading words from the queue, makes with them executions and put to the concurrentHashMap. Numbers of tasks(threads) defines in THREADS_SIZE variable.
QUEUE_SIZE and THREADS_SIZE could be changed depending on input file size.

#### How to start
First way:
1. To make jar file find file build.gradle at [..\Anagrams] and execute command in that directory:
*build clean jar*
2. Find file Anagrams-1.0.jar at [..\Anagrams\build\libs] and execute command in that directory:
*java -jar Anagrams-1.0.jar [path_to_file]*
Example: *java -jar Anagrams-1.0.jar  c:/sample.txt*

Second way:
1. Execute next command at the directory with Main.java file:
*javac Main.java*
2. Execute next command at the directory with Main.class file (out/Anagrams/com/gkhotyan):
*java -cp . Main [path_to_file]* 
Example: *java -cp . Main c:/sample.txt*

####Larger datasets
Program execution time will increase. In order to reduce memory consumption it make sense to include one more HashMap object and store there full anagrams sets.
For example, if we already have "aac", "aca", "caa", so we can put this set to the new Map, remove it from previous and ignore such words from the file. 