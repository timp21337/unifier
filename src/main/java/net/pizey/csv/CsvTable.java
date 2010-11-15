package net.pizey.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * A representation of a CSV file.
 */
public class CsvTable implements Iterable<CsvRecord> {

  protected File dataFile = null;
  protected HashMap<String, CsvColumn> columns = new HashMap<String, CsvColumn>();
  protected Vector<CsvColumn> columnsInUploadOrder = new Vector<CsvColumn>();
  protected CsvColumn primaryKey = null;
  protected Vector<CsvRecord> records = new Vector<CsvRecord>();
  protected BufferedReader reader = null;
  protected CsvFileParser parser = null;

  /** The record number of the CSV file: lineNo -1 */
  private int recordNo;

  /** Name of the file */
  private String name;

  /**
   * Constructor.
   */
  public CsvTable(String fileName) {
    this(new File(fileName));
  }

  /**
   * Constructor.
   * 
   * @param dataFile
   *          CSV file to read from
   */
  public CsvTable(File dataFile) {
    this.dataFile = dataFile;
    this.name = removeExtension(dataFile.getName());
    try {
      reader = new BufferedReader(new FileReader(this.dataFile));
      parser = new CsvFileParser(this.reader);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String removeExtension(String name) {
    return name.substring(0, name.lastIndexOf('.'));
  }

  /**
   * Process the first line to define columns. The first line contains the field
   * names - this needs to be validated against expected values, and the order
   * of the fields established.
   * 
   * @throws IOException
   */
  public void define() {
    parser.hasNextRecord(); // FIXME relying upon side effect
    recordNo = 0;

    while (parser.recordHasMoreFields()) {
      String colName = parser.nextField();
      // First column is always the key
      if (!colName.equals("")) {
        CsvColumn column = new CsvColumn(colName,
            columnsInUploadOrder.size() == 0);
        System.err.println("Adding column definition :" + column.name);
        if (column.isPrimaryKey)
          if (primaryKey != null)
            throw new CsvPrimaryKeyColumnAlreadySetException(getName());
          else
            primaryKey = column;
        columnsInUploadOrder.addElement(column);
        columns.put(column.name, column);
      }
    }
    System.err.println("Defined " + columnsInUploadOrder.size());
  }

  public String getName() {
    return this.name;
  }

  /**
   * Parse the CSV data file and store the data for saving later.
   * 
   * @throws IOException
   *           if there is a file system problem
   * @throws CsvParseException
   *           if there is a malformed field in the CSV
   * @throws CSVWriteDownException
   * @throws NoPrimaryKeyInCSVTableException
   */
  public void load() throws CsvParseException {

    define();
    CsvRecord record;
    while (null != (record = parseRecord())) {
      record.setRecordNo(recordNo++);
      records.addElement(record);
    }

    try {
      reader.close();
    } catch (IOException e) {
      throw new CsvException(e);
    }
  }

  /**
   * Reads the file until is has seen an object's-worth of field values (ie
   * until it sees an EOF or a line starting with '$') which it returns in a
   * hashtable (null if there are no field values).
   * 
   * @return a new CSVRecord
   * @throws IOException
   *           if there is a problem with the file system
   * @throws CsvParseException
   *           if there is a problem parsing the input
   */
  public CsvRecord parseRecord() throws CsvParseException {
    // FIXME relying on side effect
    if (!parser.hasNextRecord())
      return null;

    String value = null;
    CsvRecord record = new CsvRecord();
    for (int i = 0; i < columnsInUploadOrder.size(); i++) {
      try {
        value = parser.nextField();
      } catch (IllegalArgumentException e) {
        throw new CsvParseException("Failed to read data field no. " + (i + 1)
            + " in " + dataFile + " line " + parser.getLineNo(), e);
      } catch (NoSuchElementException f) {
        String message = "Problem with data field no. " + (i + 1) + " of "
            + columnsInUploadOrder.size() + " in " + dataFile + " line "
            + getLineNo();

        if (value == null) {
          message += " (Check last line of file) : " + f.toString();
        } else {
          message += ", Value:" + value + ": " + f.toString();
        }
        throw new CsvParseException(message, f);
      }
      CsvColumn col = (CsvColumn) columnsInUploadOrder.elementAt(i);
      record.addField(new CsvField(col, value));
    }
    record.setLineNo(parser.getLineNo());
    return record;
  }

  /**
   * Return a string reporting on the data added to this table.
   */
  public void report(boolean recordDetails, boolean fieldDetails, Writer output)
      throws IOException {

    output.write("*** TABLE: " + getName().toUpperCase() + " **\n\n");
    output.write("** I have read " + records.size() + " records of "
        + columnsInUploadOrder.size() + " fields\n");

    if (recordDetails) {
      for (int i = 0; i < records.size(); i++) {
        CsvRecord record = (CsvRecord) records.elementAt(i);
        output.write("   Record: CSV primary key = " + record.primaryKeyValue);

        if (fieldDetails) {
          Iterator<CsvField> it = record.iterator();
          while (it.hasNext()) {
            CsvField field = it.next();
            output.write(field.column + "=\"" + field.value);
            if (it.hasNext())
              output.write("\",");
            else
              output.write("\"\n");
          }
        }
      }
    }
    output.write("** Currently " + records.size()
        + " records in this table\n\n");
  }

  public String toString() {
    System.err.println("ToString :" + name + " " + columns.size());
    StringBuffer returnStringBuffer = new StringBuffer();
    for (String columnName : columns.keySet()) {
      System.err.println("ColumnName:" + columnName);
      returnStringBuffer.append(columnName);
      returnStringBuffer.append(',');
    }
    returnStringBuffer.append("\n");

    for (CsvRecord record : records) {
      for (CsvField field : record) {
        returnStringBuffer.append(field.value);
        returnStringBuffer.append(',');
      }
      returnStringBuffer.append("\n");
    }
    return returnStringBuffer.toString();
  }

  @Override
  public Iterator<CsvRecord> iterator() {
    return records.iterator();
  }

  public int getLineNo() {
    return parser.getLineNo();
  }

}
