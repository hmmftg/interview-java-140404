# Java Interview 1404-04

## Transaction Processor Program
to test project:

* install java 21 and gradle 8
* run `gradle jmh`

### Sample benchamrk test results:
|Benchmark                         |Mode  |Cnt    |Score |   Error  |Units|
|--|--|--|--|--|--|
|TransactionProcessorBenchmark.v1  |avgt    |8  |176.707 |± 6.079  |us/op|
|TransactionProcessorBenchmark.v2  |avgt    |8  |218.755 |± 7.537  |us/op|
|TransactionProcessorBenchmark.v3  |avgt    |8  |127.541 |± 2.141  |us/op|