package net.pizey.csv;

import java.io.FileNotFoundException;

import junit.framework.TestCase;

public class DefaultingUnifierTest extends TestCase {

  public DefaultingUnifierTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testMain() throws Exception {
    // it just logs
    DefaultingUnifier.main(new String[] { "src/test/resources/sheet1.csv" });

    try {
      DefaultingUnifier.main(new String[] { "", "" });
      fail("Should have bombed");
    } catch (CsvException e) {
      assertTrue(e.getCause() instanceof FileNotFoundException);
    }
    DefaultingUnifier.main(new String[] { "src/test/resources/sheet1.csv",
        "src/test/resources/sheet2.csv", "src/test/resources/sheet3.csv" });
  }
}
