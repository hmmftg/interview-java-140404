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