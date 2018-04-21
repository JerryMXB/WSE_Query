# WSE_Query

## What is this?
This project is a query processor based on [WSE_Indexing](https://github.com/JerryMXB/WSE_Indexing)

A web page (jquery + html) will send the http request contains the query to the query processor hosted on EC2. The query processor will first analysis the query and rewrite the query. Then, fetch the compressed the inverted index from disk which is optimized by reading block metadata and using nextGEQ() to jump chunks to minimize unnecessary I/O. Finally, the inverted index is decompressed in main memory and BM25 is used to rank the results to send response to the user client. Details will be discussed in the following paragraphs.

## How to run it?
1 First modify the file path in FilePath.java

2 Then run following command to build a maven fat jar
```
mvn clean package
```
3 Start the server using
```
java -jar hw3-jar-with-dependencies
```
