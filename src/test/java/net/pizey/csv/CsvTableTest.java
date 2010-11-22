package net.pizey.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;
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
      sheet1.unify(sheet2, false).toString();
    } catch (CsvException e) {
      assertEquals(
          "Line 3 value found for field1 but not equal to current value : '2f1' != '2f1-mutated'",
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
      sheet1.unify(sheet2, false).unify(sheet3, false).toString();
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
    String out = sheet1.unify(sheet2, false).unify(sheet3, false).toString();
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
    String out = sheet1.unify(sheet2, false).unify(sheet3, false).toString();
    assertEquals(expected, out);

  }

  public void testUnifyDEFAULTUnifyWithEmpty() {
    CsvTable holey = new CsvTable("src/test/resources/sheet2WithBlanks.csv",
        UnificationOptions.DEFAULT);
    CsvTable sheet2 = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.DEFAULT);
    String expected = "Id,field1,field2,\n" + "1,f1,f2,\n"
        + "2,2f1,2f2,\n";
    String out = holey.unify(sheet2, true).toString();
    assertEquals(expected, out);

  }

  public void testUnifyDEFAULTNotUnifyWithEmpty() {
    CsvTable holey = new CsvTable("src/test/resources/sheet2WithBlanks.csv",
        UnificationOptions.DEFAULT);
    CsvTable sheet2 = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.DEFAULT);
    try {
      holey.unify(sheet2, false).toString();
      fail("Should have bombed");
    } catch (CsvRecordUnificationException e) {
      e = null;
    }

  }

  public void testmultiLine() {
    CsvTable sheet1 = new CsvTable("src/test/resources/multilineField.csv",
        UnificationOptions.LOG);
    assertEquals("line1\nline2", sheet1.get("1").get("field1").getValue());
  }

  public void testColumn() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertEquals("Id(PK)", sheet.getColumn("Id").toString());
    assertEquals("Id", sheet.getColumn("Id").getName());
    assertEquals("field1", sheet.getColumn("field1").getName());
    assertEquals("Id", sheet.getPrimaryKeyColumn().getName());
  }

  public void testMalformedRecordCannotBeAdded() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord bad = new CsvRecord(sheet);
    try {
      sheet.add(bad);
      fail("Should have bombed");
    } catch (CsvMissingPrimaryKeyException e) {
      e = null;
    }
  }

  public void testMalformedRecordCannotBeDefaulted() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord bad = new CsvRecord(sheet);
    try {
      sheet.addMissingFields(bad);
      fail("Should have bombed");
    } catch (CsvMissingPrimaryKeyException e) {
      e = null;
    }
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#clear()}.
   */
  public void testClear() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertEquals(2, sheet.size());
    CsvField f = sheet.get("2").get("field2");
    assertEquals("2f2", f.getValue());
    sheet.clear();
    assertEquals("2f2", f.getValue());
    assertEquals(0, sheet.size());
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#get(java.lang.Object)}.
   */
  public void testGet() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertEquals("f1", sheet.get("1").get("field1").getValue());
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
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    Set<String> s = sheet.keySet();
    assertEquals("[2, 1]", s.toString());
  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvTable#put(java.lang.String, net.pizey.csv.CsvRecord)}
   * .
   */
  public void testPut() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord r = new CsvRecord(sheet);
    r.setLineNo(100);
    r.setRecordNo(99);
    CsvField pk = new CsvField(sheet.getPrimaryKeyColumn(), "3");
    r.addField(pk);
    r.addField(new CsvField(sheet.getColumn("field1"), "new"));
    sheet.put("3", r);
    assertEquals("Id,field1,field2,\n1,f1,f2,\n2,2f1,2f2,\n3,new,,\n", sheet.toString());
    try {
      sheet.put("3", r);
      fail("Should have bombed");
    } catch (CsvDuplicateKeyException e) {
      e = null;
    }
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#putAll(java.util.Map)}.
   */
  public void testPutAll() {
    CsvTable sheet1 = new CsvTable("src/test/resources/sheet1.csv", UnificationOptions.LOG);
    CsvTable sheet2 = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvTable sheet2a = new CsvTable("src/test/resources/sheet2a.csv", UnificationOptions.LOG);
    try { 
      sheet1.putAll(sheet2);
      fail("Should have bombed");
    } catch (CsvDuplicateKeyException e) { 
      e = null;
    }
    sheet2.putAll(sheet2a);
    assertEquals("Id,field1,field2,\n1,f1,f2,\n2,2f1,2f2,\n3,3f1,3f2,\n4,4f1,4f2,\n", sheet2
        .toString());
    sheet1.putAll(sheet2a);
    assertEquals("Id,field1,\n1,f1,\n2,2f1,\n3,3f1,\n4,4f1,\n", sheet1.toString());
    
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#remove(java.lang.Object)}.
   */
  public void testRemove() {
    CsvTable sheet2 = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvTable sheet2a = new CsvTable("src/test/resources/sheet2a.csv", UnificationOptions.LOG);
    sheet2.putAll(sheet2a);
    assertEquals("Id,field1,field2,\n1,f1,f2,\n2,2f1,2f2,\n3,3f1,3f2,\n4,4f1,4f2,\n", sheet2
        .toString());
    sheet2.remove("4");
    assertEquals("Id,field1,field2,\n1,f1,f2,\n2,2f1,2f2,\n3,3f1,3f2,\n", sheet2.toString());
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
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertTrue(sheet.containsKey("2"));
  }

  /**
   * Test method for {@link net.pizey.csv.CsvTable#iterator()}.
   */
  public void testIterator() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    int i = 0;
    for (CsvRecord record : sheet) {
      i++;
      assertEquals(new Integer(i).toString(), record.getPrimaryKeyField().getValue());
      assertEquals(i - 1, record.getRecordNo());
      assertEquals(i + 1, record.getLineNo());
    }
  }

}
