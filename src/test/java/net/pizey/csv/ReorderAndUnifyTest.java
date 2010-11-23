package net.pizey.csv;

import junit.framework.TestCase;

public class ReorderAndUnifyTest extends TestCase {

  public ReorderAndUnifyTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testReorderAndUnify() throws Exception {
    CsvTable sheet1 = new CsvTable("src/test/resources/eg_sheet1.csv", "ID",
        UnificationOptions.DEFAULT);
    CsvTable sheet2 = new CsvTable("src/test/resources/eg_sheet2.csv", "ID",
        UnificationOptions.DEFAULT);
    CsvTable sheet3 = new CsvTable("src/test/resources/eg_sheet3.csv", "ID",
        UnificationOptions.DEFAULT);

    sheet1.makeFirstAndPrimary("ID");
    System.out.println(sheet1.toString());
    sheet2.makeFirstAndPrimary("ID");
    sheet3.makeFirstAndPrimary("ID");

    CsvTable unified = sheet1.unify(sheet2, true).unify(sheet3, true);
    String out = unified.toString();

    String expectedHeader = "ID,PCRID,Village,C50/51,C50/51 MINOR,C59,C59 MINOR,C108,C108 MINOR,C164,C164 MINOR,DHFR-H-TYPE,436.437,436.437 Minor,436-437-MINOR-MINOR,540,540_minor,581,581_minor,613,613_minor,DHPS_haplotype,C72-76,C72-76-MINOR,mixed1,mixed2,\n";
    String expectedLine = "164,2,Southstoke,CI,,R,,N,,I,,CIRNI,SG,,,E,,G,a,A,,SGEGA,CVIET,,,";
    assertTrue(out.startsWith(expectedHeader));
    assertEquals(expectedLine, unified.get("164").toString());

  }
}
