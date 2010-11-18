/**
 * 
 */
package net.pizey.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map.Entry;

import junit.framework.TestCase;

/**
 * @author timp
 * 
 */
public class CsvTableTest extends TestCase {

  public void testRemoveExtension() {
    assertEquals("sheet1", CsvTable.removeExtension("sheet1.csv"));
    assertEquals("sheet1", CsvTable.removeExtension("sheet1"));
  }

  public void testConstruct() throws Exception {
    String sheet1Name = "src/test/resources/sheet1.csv";

    CsvTable sheet1 = new CsvTable(sheet1Name);

    String input = "Id,field1,\n1,f1,\n2,2f1,\n";
    assertEquals(input, sheet1.toString());
    assertEquals("sheet1", sheet1.getName());
    String outputFileName = "target/sheet1out.csv";
    sheet1.outputToFile(outputFileName);
    BufferedReader reader = new BufferedReader(new FileReader(outputFileName));
    StringBuffer outputBuffer = new StringBuffer();
    String line = null;
    while ((line = reader.readLine()) != null) {
      outputBuffer.append(line);
      if (!line.equals(""))
        outputBuffer.append("\n");
    }
    assertEquals(input, outputBuffer.toString());
    reader.close();

  }

  public void testMakeFirst() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv");

    String input = "Id,field1,field2,\n1,f1,f2,\n2,2f1,2f2,\n";
    assertEquals(input, sheet.toString());
    assertEquals("Id", sheet.getColumn("Id").getName());
    assertEquals("Id(PK)", sheet.getColumn("Id").toString());

    sheet.makeFirst("field1");
    assertEquals("field1,Id,field2,\nf1,1,f2,\n2f1,2,2f2,\n", sheet.toString());

  }

  public void testFailToUnifyMutated() {
    CsvTable sheet1 = new CsvTable("src/test/resources/sheet2.csv",
        UnificationOptions.LOG);
    CsvTable sheet2 = new CsvTable(
        "src/test/resources/mutatedCopyOfSheet2.csv", UnificationOptions.LOG);
    try {
      sheet1.unify(sheet2).toString();
    } catch (CsvException e) {
      assertEquals(
          "Line 3 value found for field1 but not equal: '\"field1\": \"2f1\"' != '\"field1\": \"2f1-mutated\"'",
          e.getMessage());
    }
  }

  public void testUnifyTHROW() {
    String sheet1Name = "src/test/resources/sheet1.csv";
    String sheet2Name = "src/test/resources/sheet2.csv";
    String sheet3Name = "src/test/resources/sheet3.csv";

    CsvTable sheet1 = new CsvTable(sheet1Name, UnificationOptions.THROW);
    CsvTable sheet2 = new CsvTable(sheet2Name, UnificationOptions.THROW);
    CsvTable sheet3 = new CsvTable(sheet3Name, UnificationOptions.THROW);
    try {
      sheet1.unify(sheet2).unify(sheet3).toString();
      fail("Should have bombed");
    } catch (CsvRecordNotFoundException e) {
      e = null;
    }
  }

  public void testUnifyLOG() {
    String sheet1Name = "src/test/resources/sheet1.csv";
    String sheet2Name = "src/test/resources/sheet2.csv";
    String sheet3Name = "src/test/resources/sheet3.csv";

    CsvTable sheet1 = new CsvTable(sheet1Name, UnificationOptions.LOG);
    CsvTable sheet2 = new CsvTable(sheet2Name, UnificationOptions.LOG);
    CsvTable sheet3 = new CsvTable(sheet3Name, UnificationOptions.LOG);
    String expected = "Id,field1,field2,field3,field4,\n" + "1,f1,f2,f3,f4,\n"
        + "2,2f1,2f2,2f3,2f4,\n";
    String out = sheet1.unify(sheet2).unify(sheet3).toString();
    assertEquals(expected, out);

  }

  public void testUnifyDEFAULT() {
    String sheet1Name = "src/test/resources/sheet1.csv";
    String sheet2Name = "src/test/resources/sheet2.csv";
    String sheet3Name = "src/test/resources/sheet3.csv";

    CsvTable sheet1 = new CsvTable(sheet1Name, UnificationOptions.DEFAULT);
    CsvTable sheet2 = new CsvTable(sheet2Name, UnificationOptions.DEFAULT);
    CsvTable sheet3 = new CsvTable(sheet3Name, UnificationOptions.DEFAULT);
    String expected = "Id,field1,field2,field3,field4,\n" + "1,f1,f2,f3,f4,\n"
        + "2,2f1,2f2,2f3,2f4,\n" + "3,3f1,3f2,3f3,3f4,\n";
    String out = sheet1.unify(sheet2).unify(sheet3).toString();
    assertEquals(expected, out);

  }

  public void testmultiLine() {
    CsvTable sheet1 = new CsvTable("src/test/resources/multilineField.csv",
        UnificationOptions.LOG);
    assertEquals("line1\nline2", sheet1.get("1").get("field1").value);
  }

  public void testColumn() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertEquals("Id(PK)", sheet.getColumn("Id").toString());
    assertEquals("Id", sheet.getColumn("Id").getName());
    assertEquals("field1", sheet.getColumn("field1").getName());
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#clear()}.
   */
  public void testClear() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertEquals(2, sheet.size());
    sheet.clear();
    assertEquals(0, sheet.size());
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#get(java.lang.Object)}.
   */
  public void testGet() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertEquals("f1", sheet.get("1").get("field1").value);
  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvTable#containsValue(java.lang.Object)}.
   */
  public void testContainsValue() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertTrue(sheet.containsValue(sheet.get("1")));
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#entrySet()}.
   */
  public void testEntrySet() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    String keys = "";
    String values = "";
    for (Entry<String, CsvRecord> e : sheet.entrySet()) {
      keys = keys + (keys != "" ? "," : "") + e.getKey();
      values = values + (values != "" ? "," : "") + e.getValue();
    }
    assertEquals("2,1", keys);
    // Not the order that you might expect
    assertEquals(
        "{\"Id\": \"2\",\"field1\": \"2f1\",\"field2\": \"2f2\"},{\"Id\": \"1\",\"field1\": \"f1\",\"field2\": \"f2\"}",
        values);
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#isEmpty()}.
   */
  public void testIsEmpty() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertFalse(sheet.isEmpty());
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#keySet()}.
   */
  public void testKeySet() {
  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvTable#put(java.lang.String, net.pizey.csv.CsvRecord)}
   * .
   */
  public void testPut() {
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#putAll(java.util.Map)}.
   */
  public void testPutAll() {
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#remove(java.lang.Object)}.
   */
  public void testRemove() {
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#size()}.
   */
  public void testSize() {
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#values()}.
   */
  public void testValues() {
  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvTable#containsKey(java.lang.Object)}.
   */
  public void testContainsKey() {
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#iterator()}.
   */
  public void testIterator() {
  }

}
