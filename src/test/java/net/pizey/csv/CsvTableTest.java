/**
 * 
 */
package net.pizey.csv;

import java.io.BufferedReader;
import java.io.FileReader;

import junit.framework.TestCase;

/**
 * @author timp
 * 
 */
public class CsvTableTest extends TestCase {

  public void testRemoveExtension() {
    assertEquals("sheet1", CsvTable.removeExtension("sheet1.csv"));
  }

  public void testConstruct() throws Exception {
    String sheet1Name = "src/test/resources/sheet1.csv";

    CsvTable sheet1 = new CsvTable(sheet1Name);

    String input = "Id,field1,\n1,f1,\n2,2f1,\n";
    assertEquals(input,sheet1.toString());
    assertEquals("sheet1",sheet1.getName());
    String outputFileName = "target/sheet1out.csv";
    sheet1.outputToFile(outputFileName);
    BufferedReader reader = new BufferedReader(new FileReader(outputFileName));
    StringBuffer outputBuffer = new StringBuffer();
    String line = null;
    while((line = reader.readLine()) != null){
      outputBuffer.append(line);
      if (!line.equals(""))
        outputBuffer.append("\n");
    }
    assertEquals(input, outputBuffer.toString());
    reader.close();
    
  }

  public void testMakeFirst(){ 
    CsvTable sheet = new CsvTable("src/test/resources/sheet2.csv");

    String input = "Id,field1,field2,\n1,f1,f2,\n2,2f1,2f2,\n";
    assertEquals(input,sheet.toString());

    sheet.makeFirst("field1");
    assertEquals("field1,Id,field2,\nf1,1,f2,\n2f1,2,2f2,\n", sheet.toString());
    
  }
  
  
  public void testUnifyLOG() { 
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
    } catch (CsvRecordNotFoundException e){
      e = null;
    }
  }
  public void testUnifyDEFAULT() { 
    String sheet1Name = "src/test/resources/sheet1.csv";
    String sheet2Name = "src/test/resources/sheet2.csv";
    String sheet3Name = "src/test/resources/sheet3.csv";

    CsvTable sheet1 = new CsvTable(sheet1Name, UnificationOptions.DEFAULT);
    CsvTable sheet2 = new CsvTable(sheet2Name, UnificationOptions.DEFAULT);
    CsvTable sheet3 = new CsvTable(sheet3Name, UnificationOptions.DEFAULT);
    String expected = "Id,field1,field2,field3,field4,\n" +
                      "1,f1,f2,f3,f4,\n" + 
                      "2,2f1,2f2,2f3,2f4,\n" + 
                      "3,3f1,3f2,3f3,3f4,\n";
    String out = sheet1.unify(sheet2).unify(sheet3).toString();
    assertEquals(expected, out);
    
  }
}
