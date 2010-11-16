package net.pizey.csv;

/**
 * A field within a record of a CSV file.
 */
public class CsvField {

  CsvColumn column = null;
  String value = null;

  /**
   * @param column the column this field is in
   * @param value the field value as a string
   */
  public CsvField(CsvColumn column, String value) {
    this.column = column;
    this.value = value;
  }

}

