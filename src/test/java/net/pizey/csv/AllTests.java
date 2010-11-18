package net.pizey.csv;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {

    TestSuite ts = new TestSuite();
    ts.addTestSuite(CsvTableTest.class);
    ts.addTestSuite(CsvFileParserTest.class);
    ts.addTestSuite(DefaultingUnifierTest.class);

    return ts;

  }

}
