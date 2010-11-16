package net.pizey.csv;

/**
 * A column in a CSV file.
 * <p>
 * A CSVColumn can be a primary key for this table, ie unique and used by other
 * tables to refer to this record.
 * <p>
 * A CSVColumn can also be a foreign key into another {@link CsvTable}.
 * 
 */
public class CsvColumn {

  String name = null;
  boolean isPrimaryKey = false;
  CsvTable foreignTable = null;

  /**
   * Simplest case constructor.
   * 
   * @param name
   *          the name of the column.
   */
  public CsvColumn(String name) {
    this.name = name;
  }

  /**
   * Constructor for a key value into another table.
   * 
   * @param name
   *          the name of the POEM column this is to be mapped to.
   * @param foreignTable
   *          another CSVTable in which this value should be found.
   */
  public CsvColumn(String name, CsvTable foreignTable) {
    this.name = name;
    this.foreignTable = foreignTable;
  }

  /**
   * Constructor for a key value into another table.
   * 
   * @param name
   *          the name of the POEM column this is to be mapped to.
   * @param isPrimaryKey
   *          flag to indicate this is the primary key column.
   */
  public CsvColumn(String name, boolean isPrimaryKey) {
    this.name = name;
    this.isPrimaryKey = isPrimaryKey;
  }

  public String toString() { 
    return name + (isPrimaryKey ? "(PK)" : "");
  }
}
