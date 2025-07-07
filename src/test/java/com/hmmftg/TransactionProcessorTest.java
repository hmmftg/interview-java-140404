package com.hmmftg;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TransactionProcessorTest {
  // expected output constants
  private static final long EXPECTED_COUNT = 516L;
  private static final long EXPECTED_SUM = 71199785618937L;
  private static final List<String> EXPECTED_DUPLICATES =
      Arrays.asList(
          "Source Account: 12345467890123, Amount: 1000010000000, Count: 28",
          "Source Account: 12345467890123, Amount: 313926263, Count: 146",
          "Source Account: 12345467890123, Amount: 1000000000000, Count: 43");
  private String sampleData;

  @BeforeAll
  public void loadData() throws Exception {
    try (InputStream in = getClass().getResourceAsStream("/sample.txt")) {
      if (in == null) {
        throw new IllegalStateException("sample.txt not on classpath");
      }
      sampleData = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  @Test
  public void testV1() {
    testVersion(com.hmmftg.transactionProcessor.v1.TransactionProcessor.class);
  }

  @Test
  public void testV2() {
    testVersion(com.hmmftg.transactionProcessor.v2.TransactionProcessor.class);
  }

  @Test
  public void testV3() {
    testVersion(com.hmmftg.transactionProcessor.v3.TransactionProcessor.class);
  }

  private void testVersion(Class<?> clazz) {
    String sampleData =
        assertDoesNotThrow(
            () -> {
              try (InputStream in = getClass().getResourceAsStream("/sample.txt")) {
                if (in == null) {
                  throw new IllegalStateException("sample.txt not on classpath");
                }
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
              }
            });

    String output =
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
            () -> {
              return (String)
                  clazz
                      .getMethod("processTransactions", java.io.Reader.class)
                      .invoke(null, new StringReader(sampleData));
            });

    String[] lines = output.split("\n");
    assertTrue(lines.length >= 4);

    // check count line
    assertTrue(lines[0].equals("Count: " + EXPECTED_COUNT));
    // check sum line
    assertTrue(lines[1].equals("Sum: " + EXPECTED_SUM));
    // check header
    assertTrue(lines[2].equals("Duplicate Transactions:"));

    // gather duplicates
    List<String> found = Arrays.asList(Arrays.copyOfRange(lines, 3, lines.length));
    assertTrue(found.size() == EXPECTED_DUPLICATES.size());

    for (String exp : EXPECTED_DUPLICATES) {
      assertTrue(found.contains(exp));
    }
  }
}
