package net.pizey.csv;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestCase;

/**
 * @author timp
 * @since 19 Nov 2010 12:02:31
 * 
 */
public class CsvRecordTest extends TestCase {

  /**
   * @param name
   */
  public CsvRecordTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testPrimaryKeyCannotBeSetTwice() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord bad = new CsvRecord(sheet);
    CsvField key1 = new CsvField(sheet.getPrimaryKeyColumn(), "1");
    CsvField key2 = new CsvField(sheet.getPrimaryKeyColumn(), "2");
    bad.addField(key1);
    bad.addField(key1);
    bad.addField(key2);
  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#CsvRecord(net.pizey.csv.CsvTable)}.
   */
  public void testCsvRecord() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#replaceField(net.pizey.csv.CsvField, net.pizey.csv.CsvField)} .
   */
  public void testReplaceField() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#iterator()}.
   */
  public void testIterator() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#unify(net.pizey.csv.CsvRecord, boolean)}.
   */
  public void testUnify() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#addField(net.pizey.csv.CsvField)}.
   */
  public void testAddField() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#setRecordNo(int)}.
   */
  public void testSetRecordNo() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#getRecordNo()}.
   */
  public void testGetRecordNo() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#setLineNo(int)}.
   */
  public void testSetLineNo() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#getLineNo()}.
   */
  public void testGetLineNo() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#getPrimaryKey()}.
   */
  public void testGetPrimaryKey() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord r = sheet.get("2");
    assertEquals("2", r.getPrimaryKey());
    CsvRecord unset = new CsvRecord(sheet);
    unset.addField(new CsvField(new CsvColumn("field1", false), "3"));
    try {
      unset.getPrimaryKey();
    } catch (CsvMissingPrimaryKeyException e) {
      e = null;
    }
  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#get(java.lang.Object)}.
   */
  public void testGet() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#clear()}.
   */
  public void testClear() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertEquals(2, sheet.size());
    CsvRecord r = sheet.get("2");
    CsvField f = r.get("field2");
    assertEquals("2f2", f.getValue());
    try {
      r.clear();
      fail("Should have bombed");
    } catch (UnsupportedOperationException e) {
      e = null;
    }
    assertFalse(r.isEmpty());
    assertEquals("2f2", f.getValue());
    assertEquals(2, sheet.size());
  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#containsKey(java.lang.Object)}.
   */
  public void testContainsKey() {

    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertEquals(2, sheet.size());
    CsvRecord r = sheet.get("2");
    CsvField f = r.get("field2");
    assertEquals("2f2", f.getValue());
    assertTrue(r.containsKey("field2"));
    r.remove("field2");
    assertFalse(r.containsKey("field2"));
  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#containsValue(java.lang.Object)}.
   */
  public void testContainsValue() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertEquals(2, sheet.size());
    CsvRecord r = sheet.get("2");
    CsvField f = r.get("field2");
    assertTrue(r.containsValue(f));
  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#entrySet()}.
   */
  public void testEntrySet() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord r = sheet.get("2");
    Set<Entry<String, CsvField>> s = r.entrySet();
    assertEquals("[field2=\"field2\": \"2f2\", Id=\"Id\": \"2\", field1=\"field1\": \"2f1\"]", s
        .toString());
  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#isEmpty()}.
   */
  public void testIsEmpty() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#keySet()}.
   */
  public void testKeySet() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord r = sheet.get("2");
    Set<String> s = r.keySet();
    assertEquals("[field2, Id, field1]", s.toString());
  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#put(java.lang.String, net.pizey.csv.CsvField)} .
   */
  public void testPut() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord r = new CsvRecord(sheet);
    r.setLineNo(100);
    CsvField pk = new CsvField(sheet.getPrimaryKeyColumn(), "3");
    r.put("Id", pk);
    r.put("field1", new CsvField(sheet.getColumn("field1"), "new"));
    assertEquals("Id,field1,field2,\n1,f1,f2,\n2,2f1,2f2,\n", sheet.toString());
    try {
      r.put("3", pk);
      fail("Should have bombed");
    } catch (CsvInvalidKeyException e) {
      e = null;
    }
  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#putAll(java.util.Map)}.
   */
  public void testPutAll() {
    CsvTable sheet1 = new CsvTable("src/test/resources/sheet1.csv", UnificationOptions.LOG);
    CsvTable sheet2 = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord r1 = sheet1.get("2");
    CsvRecord r2 = sheet2.get("2");

    r1.putAll(r2);

    assertEquals("{\"Id\": \"2\",\"field1\": \"2f1\"}", r1.toJSON());
    assertEquals("2,2f1", r1.toString());
  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#remove(java.lang.Object)}.
   */
  public void testRemove() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#size()}.
   */
  public void testSize() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertEquals(2, sheet.size());
    CsvRecord r = sheet.get("2");
    assertEquals(3, r.size());
  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#values()}.
   */
  public void testValues() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    assertEquals(2, sheet.size());
    CsvRecord r = sheet.get("2");
    assertEquals("[\"field2\": \"2f2\", \"Id\": \"2\", \"field1\": \"2f1\"]", r.values().toString());

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#toString()}.
   */
  public void testToString() {

  }

  public void testHashCode() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord r = sheet.get("2");
    assertEquals(-1559758908, r.hashCode());

  }

  public void testEquals() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord r = sheet.get("2");
    assertEquals(r, r.clone(r.getTable()));
    try {
      r.getTable().add((CsvRecord) r.clone());
    } catch (CsvDuplicateKeyException e) {
      e = null;
    }
    assertFalse(r.equals(null));
    assertFalse(r.equals(new Object()));
  }

  public void testEqualsFailsForDifferentFiles() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet1.csv", UnificationOptions.LOG);
    CsvTable copy = new CsvTable("src/test/resources/CopyOfSheet1.csv", UnificationOptions.LOG);
    CsvRecord r1 = sheet.get("2");
    CsvRecord r2 = copy.get("2");
    assertFalse(r1.equals(r2));
  }

  public void testEqualsFailsForDifferentOrderings() {
    CsvTable sheet = new CsvTable("src/test/resources/sheet1.csv", UnificationOptions.LOG);
    CsvTable copy = new CsvTable(sheet);
    sheet.addColumn(new CsvColumn("a1", false));
    sheet.addColumn(new CsvColumn("a2", false));
    copy.addColumn(new CsvColumn("a2", false));
    copy.addColumn(new CsvColumn("a1", false));

    CsvRecord r1 = sheet.get("2");
    CsvRecord r2 = copy.get("2");
    assertFalse(r1.equals(r2));
  }

  public void testGeneratedBridgeMethods() throws Exception {

    for (Method m : CsvRecord.class.getMethods()) {
      System.out.println(m.toGenericString());
    }

    CsvTable t = new CsvTable("src/test/resources/sheet2.csv", UnificationOptions.LOG);
    CsvRecord r = t.get("1");
    CsvField f = r.get("Id");
    Object o = r.get((Object) "Id");
    assertTrue(o.equals(f));

    // Not accessible
    // Object o2 = r.put("jj", o);
    Method method = r.getClass().getMethod("put", new Class[] { Object.class, Object.class });
    method.invoke(r, "Id", o);

    r.remove("Id");
    // Careful record and table now invalid
  }
}
