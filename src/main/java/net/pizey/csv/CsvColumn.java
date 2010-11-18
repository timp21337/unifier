package net.pizey.csv;

/**
 * A column in a CSV file.
 * <p>
 * A CSVColumn can be a primary key for this table.
 * <p>
 * Every CsvTable must have one primary key.
 * 
 */
public class CsvColumn {

  private String name = null;
  private boolean isPrimaryKey = false;

  /**
   * Constructor for a key value into another table.
   * 
   * @param name
   *          the name of the column
   * @param isPrimaryKey
   *          flag to indicate this is the primary key column.
   */
  public CsvColumn(String name, boolean isPrimaryKey) {
    this.name = name;
    this.isPrimaryKey = isPrimaryKey;
  }

  public String getName() {
    return name;
  }

  public boolean isPrimaryKey() {
    return isPrimaryKey;
  }

  public String toString() {
    return name + (isPrimaryKey ? "(PK)" : "");
  }

}
