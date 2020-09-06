The statistic info in this package not quite correct, cause [benchmarks](../../../src/test/java/com/pingpong/GetStatTest.java) are not correct:
- measured in a one test
- measured with a ``System.nanoTime()``
- runned from one test, despite on a different threads
- and so on...

But. 
Cause it measured for 