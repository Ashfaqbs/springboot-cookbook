In Spring Boot, the default heap size is determined by the JVM. The total heap size can be set using the -Xmx JVM option, and the maximum heap size can be set using the -Xms JVM option. For example, to set the total heap size to 2GB and the maximum heap size to 1GB, you would use the following command when starting your Spring Boot application:

```
java -Xmx2g -Xms1g -jar myapp.jar
```

You can also set the heap size in your application.properties file.
```
spring.datasource.username=root
spring.datasource.password=password

# Heap Size
spring.datasource.tomcat.max-active=50
spring.datasource.tomcat.max-idle=20
spring.datasource.tomcat.min-idle=15
spring.datasource.tomcat.initial-size=10
```

Please note that you should always monitor the heap usage of your application and adjust the heap size accordingly to prevent out of memory errors.

















To clear heap dumps, you can use the following command:
```
jmap -dump:format=b,file=heapdump.hprof <pid>
```
This command will create a heap dump file called "heapdump.hprof" in the current directory.

Alternatively, you can use the following command:
```
jcmd <pid> GC.heap_dump filename=heapdump.hprof
```

Once you have captured the heap dump, you can use a tool like Eclipse Memory Analyzer (MAT) to analyze the heap dump and identify any memory leaks. Once you have identified and fixed the leaks, you can delete the heap dump file to clear it from your system.

Please note that, heap dump are large files and it is important to keep your system's storage space in mind when capturing heap dump and make sure to delete them once you are done.
