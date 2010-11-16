package net.pizey.csv;

public class Unifier {
  
  public static void main(String[] args) throws Exception {
    if (args.length < 2)
      System.err.println("Expected two or more CSV file name arguments");
    else {
      CsvTable current = getCsvTable(args[0]);
      for (int i = 1; i < args.length; i++) { 
        CsvTable candidate = getCsvTable(args[i]);
        current = current.unify(candidate);
      }
      System.out.print(current);
    }
  }  
  
  private static CsvTable getCsvTable(String fileName) { 
    return new CsvTable(fileName, UnificationOptions.DEFAULT);
  }
}
