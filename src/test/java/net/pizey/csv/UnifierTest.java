package net.pizey.csv;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class UnifierTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public UnifierTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( UnifierTest.class );
    }

    public void testApp()
    {
      String sheet1Name = "src/test/resources/sheet1.csv";
      String sheet2Name = "src/test/resources/sheet2.csv";
      String sheet3Name = "src/test/resources/sheet3.csv";

      CsvTable sheet1 = new CsvTable(sheet1Name, UnificationOptions.LOG);
      CsvTable sheet2 = new CsvTable(sheet2Name, UnificationOptions.LOG);
      CsvTable sheet3 = new CsvTable(sheet3Name, UnificationOptions.LOG);
      String expected = "Id,field1,field2,field3,field4,\n" +
                        "1,f1,f2,f3,f4,\n" + 
                        "2,2f1,2f2,2f3,2f4,\n";
      String out = sheet1.unify(sheet2).unify(sheet3).toString();
      assertEquals(expected, out);
    }
}
