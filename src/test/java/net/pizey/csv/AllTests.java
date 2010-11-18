package net.pizey.csv;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AllTests 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AllTests( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        TestSuite ts = new TestSuite( );
        ts.addTestSuite(CsvTableTest.class );
        ts.addTestSuite(CsvFileParserTest.class );
        return ts;
        
    }

}
