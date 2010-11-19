package net.pizey.csv;

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
    bad.addField(key1);
    try {
      bad.addField(key1);
      fail("Should have bombed");
    } catch (CsvPrimaryKeyAlreadySetException e) {
      e = null;
    }
  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvRecord#CsvRecord(net.pizey.csv.CsvTable)}.
   */
  public void testCsvRecord() {

  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvRecord#replaceField(net.pizey.csv.CsvField, net.pizey.csv.CsvField)}
   * .
   */
  public void testReplaceField() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#iterator()}.
   */
  public void testIterator() {

  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvRecord#unify(net.pizey.csv.CsvRecord, boolean)}.
   */
  public void testUnify() {

  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvRecord#addField(net.pizey.csv.CsvField)}.
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
   * Test method for {@link net.pizey.csv.CsvRecord#getPrimaryKeyField()}.
   */
  public void testGetPrimaryKeyField() {

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

  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvRecord#containsKey(java.lang.Object)}.
   */
  public void testContainsKey() {

  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvRecord#containsValue(java.lang.Object)}.
   */
  public void testContainsValue() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#entrySet()}.
   */
  public void testEntrySet() {

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

  }

  /**
   * Test method for
   * {@link net.pizey.csv.CsvRecord#put(java.lang.String, net.pizey.csv.CsvField)}
   * .
   */
  public void testPut() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#putAll(java.util.Map)}.
   */
  public void testPutAll() {

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

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#values()}.
   */
  public void testValues() {

  }

  /**
   * Test method for {@link net.pizey.csv.CsvRecord#toString()}.
   */
  public void testToString() {

  }

}
