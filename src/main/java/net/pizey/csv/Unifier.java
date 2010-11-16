package net.pizey.csv;

/**
 */
public class Unifier {
  public static void main(String[] args) throws Exception {
    String sheet1Name = "src/test/resources/sheet1.csv";
    String sheet2Name = "src/test/resources/sheet2.csv";
    String sheet3Name = "src/test/resources/sheet3.csv";

    CsvTable sheet1 = new CsvTable(sheet1Name);
    CsvTable sheet2 = new CsvTable(sheet2Name);
    CsvTable sheet3 = new CsvTable(sheet3Name);
    System.out.println(sheet1.unify(sheet2).unify(sheet3));
    
  }
}
