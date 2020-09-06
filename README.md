# GC Free NIO
The project is a simple client-server app throw NIO for java 8, where any objects are not allocating. All allocations 
on Server part appear before starting a message exchange. On client part it appears after sending a first message.Then all work of client-server message exchange going without any allocations.

  
Experiments show that thread affinity ineffectual in a common case, but if you isolate CPU for server part it will.
 
 ## Code navigation
 
 - [standard](src/main/java/com/pingpong/standard) package: is a package with a standard client-server nio app
 - [gc_free](src/main/java/com/pingpong/standard) package: is a GC-free client-server nio app
 
 ## Instrumentation
  It uses: 
   - [trove4j](https://bitbucket.org/trove4j/trove/src/master/)
   - [Java-Thread-Affinity](https://github.com/OpenHFT/Java-Thread-Affinity)
   - [Google allocation-instrumenter](https://github.com/google/allocation-instrumenter)
    
