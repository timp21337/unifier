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

}
