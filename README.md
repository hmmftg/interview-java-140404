# Java Interview 1404-04

## Transaction Processor Program
to test project:

* install java 21
* run `mvn clean test`
* run `mvn clean package jmh:benchamrk`

### Sample benchamrk test results:
|Benchmark                         |Mode  |Cnt    |Score |   Error  |Units|
|--|--|--|--|--|--|
|TransactionProcessorBenchmark.v1  |avgt    |8  |207.074 |± 25.435  |us/op|
|TransactionProcessorBenchmark.v2  |avgt    |8  |237.090 |± 10.656  |us/op|
|TransactionProcessorBenchmark.v3  |avgt    |8  |198.826 |±  8.416  |us/op|

### Prompt used to enhance versions

the requirement is to analyze constant input file and report count and sum and list of duplicates, duplicates are found with source account and amount identical, i have a class to process input file and generate report, analyze performance and give new version which has less execution time with no change to results, the input file is constant, sample input:  `source_account	amount	ssn
12345467890123	1000010000000	1718899553
12345467890123	1000010000000	1718899553
12345467890123	1000010000000	1718899553
12345467890123	303970681	1718899553
12345467890123	329677037	1718899553
12345467890123	291573592	1718899553
12345467890123	324752696	1718899553
12345467890123	240131514	1718899553
12345467890123	286143769	1718899553
12345467890123	584968067	1718899553`