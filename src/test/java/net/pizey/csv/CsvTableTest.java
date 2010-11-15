/**
 * 
 */
package net.pizey.csv;

import junit.framework.TestCase;

/**
 * @author timp
 * 
 */
public class CsvTableTest extends TestCase {

  public void testRemoveExtension() {
    assertEquals("sheet1", CsvTable.removeExtension("sheet1.csv"));
  }

  public void testConstruct() {
    String sheet1Name = "src/test/resources/sheet1.csv";

    CsvTable sheet1 = new CsvTable(sheet1Name);

    sheet1.load();

    System.out.println(sheet1.toString());
  }

}
