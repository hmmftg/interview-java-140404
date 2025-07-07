package com.hmmftg.bench;

import org.openjdk.jmh.annotations.*;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class TransactionProcessorBenchmark {

  /**
   * Holds the input file content so each invocation reads from the same in-memory copy.
   */
  private String sampleData;

  @Setup(Level.Trial)
  public void loadData() throws Exception {
    try (InputStream in = getClass()
        .getResourceAsStream("/sample.txt")) {
      if (in == null) {
        throw new IllegalStateException("sample.txt not on classpath");
      }
      sampleData = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  /**
   * Common runner that feeds the same data to each version.
   */
  private String runVersion(String className, String sampleData) throws Exception {
    // Redirect System.in to our sampleData
    InputStream originalIn = System.in;
    try {
      System.setIn(new java.io.ByteArrayInputStream(
                       sampleData.getBytes(StandardCharsets.UTF_8)));
      Class<?> cls = Class.forName(className);
      Method m = cls.getMethod("processTransactions", java.io.Reader.class);
      return (String) m.invoke(null, new StringReader(sampleData));
    } finally {
      System.setIn(originalIn);
    }
  }

  @Benchmark
  public String v1() throws Exception {
    return runVersion("com.hmmftg.transactionProcessor.v1.TransactionProcessor", sampleData);
  }

  @Benchmark
  public String v2() throws Exception {
    return runVersion("com.hmmftg.transactionProcessor.v2.TransactionProcessor", sampleData);
  }

  @Benchmark
  public String v3() throws Exception {
    return runVersion("com.hmmftg.transactionProcessor.v3.TransactionProcessor", sampleData);
  }
}
