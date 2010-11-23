package net.pizey.csv;

import junit.framework.TestCase;

/**
 * @author timp
 * @since 21 Nov 2010 21:27:05
 *
 */
public class CsvFieldTest extends TestCase {

  public CsvFieldTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test method for {@link net.pizey.csv.CsvField#hashCode()}.
   */
  public void testHashCode() {
    
  }

  /**
   * Test method for {@link net.pizey.csv.CsvField#CsvField(net.pizey.csv.CsvColumn, java.lang.String)}.
   */
  public void testCsvField() {
    try { 
      new CsvField(null, "");
      fail("Should have bombed");
    } catch (NullPointerException e) { 
      e = null;
    }
    try { 
      new CsvField(new CsvColumn("Id", true), null);
      fail("Should have bombed");
    } catch (NullPointerException e) { 
      e = null;
    }
  }

  /**
   * Test method for {@link net.pizey.csv.CsvField#toString()}.
   */
  public void testToString() {
    
  }

  /**
   * Test method for {@link net.pizey.csv.CsvField#getColumn()}.
   */
  public void testGetColumn() {
    
  }

  /**
   * Test method for {@link net.pizey.csv.CsvField#getValue()}.
   */
  public void testGetValue() {
    
  }

  /**
   * Test method for {@link net.pizey.csv.CsvField#equals(java.lang.Object)}.
   */
  public void testEqualsObject() {
    CsvField f1 = new CsvField(new CsvColumn("Id", true), "2");
    assertEquals(1287046,f1.hashCode());
    CsvField f2 = new CsvField(new CsvColumn("Id", true), "2");
    assertEquals(1287046,f2.hashCode());
    CsvField f3 = new CsvField(new CsvColumn("Id", false), "2");
    CsvField f4 = new CsvField(new CsvColumn("Id", false), "4");
    assertEquals(1292812,f3.hashCode());
    assertTrue(f1.equals(f1));
    assertTrue(f1.equals(f2));
    assertFalse(f1.equals(f3));
    assertFalse(f1.equals(null));
    assertFalse(f1.equals(new Object()));
    assertFalse(f3.equals(f4));
    
  }
  
  /**
   * Test method for {@link net.pizey.csv.CsvField#clone()}.
   */
  public void testClone() {
    CsvField f1 = new CsvField(new CsvColumn("Id", true), "2");
    assertEquals(f1,f1.clone());
    CsvField f2 = new CsvField(new CsvColumn("Id", true), "2");
    assertEquals(f2, f2.clone());
    CsvField f3 = new CsvField(new CsvColumn("Id", false), "2");
    assertEquals(f3, f3.clone());
  }

}
