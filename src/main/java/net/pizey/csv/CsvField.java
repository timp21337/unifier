package net.pizey.csv;

/**
 * A field within a record of a CSV file.
 */
public class CsvField {

  private CsvColumn column = null;

  private String value = null;

  /**
   * @param column
   *          the column this field is in
   * @param value
   *          the field value as a string
   */
  public CsvField(CsvColumn column, String value) {
    if (column == null)
      throw new NullPointerException();
    if (value == null)
      throw new NullPointerException();
    this.column = column;
    this.value = value;
  }

  public String toString() {
    return "\"" + column.getName() + "\": \"" + value + "\"";
  }

  public CsvColumn getColumn() {
    return column;
  }

  public String getValue() {
    return value;
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + column.hashCode();
    result = prime * result + value.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CsvField)) {
      return false;
    }
    CsvField other = (CsvField) obj;
    if (!column.equals(other.column)) {
      return false;
    }
    if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

}
