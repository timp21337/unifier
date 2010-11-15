/**
 * 
 */
package net.pizey.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;

/**
 * @author timp
 * 
 */
public class CsvFileParserTest extends TestCase {

  /**
   * @param name
   */
  public CsvFileParserTest(String name) {
    super(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
  }

  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvFileParser#CsvFileParser(java.io.BufferedReader)}.
   */
  public void testCsvFileParser() throws Exception {
    String fileName = "src/test/resources/sheet1.csv";
    System.out.println("***** Reading file " + fileName);

    BufferedReader reader = new BufferedReader(new FileReader(
        new File(fileName)));
    CsvFileParser toks = new CsvFileParser(reader);

    int recordCount = 0;
    while (toks.hasNextRecord()) {
      System.out.println("*** Line " + ++recordCount);
      int i = 0;
      while (toks.recordHasMoreFields()) {
        System.out.println("Field " + ++i + ":" + toks.nextField());
      }
    }

  }

  /**
   * Test method for {@link net.pizey.csv.CsvFileParser#hasNextRecord()}.
   */
  public void testNextRecord() {
  }

  /**
   * Test method for {@link net.pizey.csv.CsvFileParser#getLineNo()}.
   */
  public void testGetLineNo() {
  }

  /**
   * Test method for {@link net.pizey.csv.CsvFileParser#recordHasMoreFields()}.
   */
  public void testRecordHasMoreFields() {
  }

  /**
   * Test method for {@link net.pizey.csv.CsvFileParser#nextField()}.
   */
  public void testNextField() {
  }

}
