/**
 * 
 */
package net.pizey.csv;

import junit.framework.TestCase;

/**
 * @author timp
 * @since 21 Nov 2010 20:59:48
 *
 */
public class CsvColumnTest extends TestCase {

  public CsvColumnTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test method for {@link net.pizey.csv.CsvColumn#hashCode()}.
   */
  public void testHashCode() {
    CsvColumn c1 = new CsvColumn("Id", true);
    assertEquals(41485, c1.hashCode());
    CsvColumn c2 = new CsvColumn("val", false);
    assertEquals(155821, c2.hashCode());
    CsvColumn c3 = new CsvColumn("ID", true);
    assertEquals(41453, c3.hashCode());
    CsvColumn c4 = new CsvColumn("Id", true);
    assertEquals(41485, c1.hashCode());
    CsvColumn c5 = new CsvColumn("val", false);
    
    assertTrue(c1.equals(c1));
    assertTrue(c1.equals(c4));
    assertFalse(c3.equals(c4));
    assertFalse(c1.equals(c2));
    assertTrue(c2.equals(c5));
    assertFalse(c1.equals(null));
    assertFalse(c1.equals(new Object()));
    
  }

  /**
   * Test method for {@link net.pizey.csv.CsvColumn#CsvColumn(java.lang.String, boolean)}.
   */
  public void testCsvColumn() {
    try { 
      new CsvColumn(null,false);
      fail("Should have bombed");
    } catch (NullPointerException e) { 
      e = null;
    }
    
  }

  /**
   * Test method for {@link net.pizey.csv.CsvColumn#getName()}.
   */
  public void testGetName() {
    
  }

  /**
   * Test method for {@link net.pizey.csv.CsvColumn#isPrimaryKey()}.
   */
  public void testIsPrimaryKey() {
    
  }

  /**
   * Test method for {@link net.pizey.csv.CsvColumn#toString()}.
   */
  public void testToString() {
    CsvColumn c1 = new CsvColumn("Id", true);
    assertEquals("Id(PK)", c1.toString());
    CsvColumn c2 = new CsvColumn("val", false);
    assertEquals("val", c2.toString());
    
  }

  /**
   * Test method for {@link net.pizey.csv.CsvColumn#equals(java.lang.Object)}.
   */
  public void testEqualsObject() {
    
  }

}
