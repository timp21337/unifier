package net.pizey.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A representation of a CSV file.
 */
public class CsvTable implements Iterable<CsvRecord> {

  protected File dataFile = null;
  protected BufferedReader reader = null;
  protected CsvFileParser parser = null;

  protected HashMap<String, CsvColumn> columns = new HashMap<String, CsvColumn>();
  protected ArrayList<CsvColumn> columnsInOrder = new ArrayList<CsvColumn>();
  protected CsvColumn primaryKey = null;
  protected ArrayList<CsvRecord> records = new ArrayList<CsvRecord>();
  protected HashMap<String, Integer> keyToIndex = new HashMap<String, Integer>();
  
  protected UnificationOptions unificationOption;
  
  /** The record number of the CSV file: lineNo -1 */
  private int recordNo;

  /** Name of the file */
  private String name;

  public CsvTable(String fileName) {
    this(new File(fileName));
  }
  public CsvTable(String fileName, UnificationOptions unificationOption) {
    this(new File(fileName), unificationOption);
  }
  public CsvTable(File file) {
    this(file, UnificationOptions.THROW);
  }
  public CsvTable(File file, UnificationOptions unificationOption) {
    this.unificationOption = unificationOption;
    this.dataFile = file;
    this.name = removeExtension(file.getName());
    try {
      reader = new BufferedReader(new FileReader(this.dataFile));
      parser = new CsvFileParser(this.reader);
    } catch (Exception e) {
      throw new CsvException("Unexpected exception", e);
    }
    load();
    try{
      reader.close();
    } catch (Exception e) {
      throw new CsvException("Unexpected exception", e);
    }
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
   void load() throws CsvParseException {

    defineColumns();
    CsvRecord record;
    while (null != (record = parseRecord())) {
      add(record);
    }

  }
  /**
   * Process the first line to define columns. The first line contains the field
   * names - this needs to be validated against expected values, and the order
   * of the fields established.
   * 
   * @throws IOException
   */
  void defineColumns() {
    parser.hasNextRecord(); // FIXME relying upon side effect
    recordNo = 0;

    while (parser.recordHasMoreFields()) {
      String colName = parser.nextField();
      // First column is always the key
      if (!colName.equals("")) {
        CsvColumn column = new CsvColumn(colName,
            columnsInOrder.size() == 0);
        if (column.isPrimaryKey)
          if (primaryKey != null)
            throw new CsvPrimaryKeyColumnAlreadySetException(getName());
          else
            primaryKey = column;
        addColumn(column);
      }
    }
  }

  public void addColumn(CsvColumn column) {
     columnsInOrder.add(column);
     columns.put(column.name, column);
  }

  public String getName() {
     return this.name;
  }

  private void add(CsvRecord record) {
    if (record.primaryKeyField == null)
      throw new RuntimeException("Bug: primary key null");
    record.setRecordNo(recordNo++);
    records.add(record);
    keyToIndex.put(record.primaryKeyField.value, new Integer(records.size() -1));
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
    CsvRecord record = new CsvRecord(this);
    for (int i = 0; i < columnsInOrder.size(); i++) {
      try {
        value = parser.nextField();
      } catch (IllegalArgumentException e) {
        throw new CsvParseException("Failed to read data field no. " + (i + 1)
            + " in " + dataFile + " line " + parser.getLineNo(), e);
      } catch (NoSuchElementException f) {
        String message = "Problem with data field no. " + (i + 1) + " of "
            + columnsInOrder.size() + " in " + dataFile + " line "
            + getLineNo();

        if (value == null) {
          message += " (Check last line of file) : " + f.toString();
        } else {
          message += ", Value:" + value + ": " + f.toString();
        }
        throw new CsvParseException(message, f);
      }
      CsvColumn col = (CsvColumn) columnsInOrder.get(i);
      record.addField(new CsvField(col, value));
    }
    record.setLineNo(parser.getLineNo());
    return record;
  }

  public CsvRecord defaulted(CsvRecord from) {
    if (from.primaryKeyField == null)
      throw new CsvException(
          "Invalid record primeKeyField is null");
    CsvRecord csvRecord = new CsvRecord(this);
    csvRecord.addField(from.primaryKeyField);
    
    for (CsvColumn column : columnsInOrder ) { 
      if (!column.name.equals(from.primaryKeyField.column.name)) 
        csvRecord.addField(new CsvField(column,""));
    }
    csvRecord.setLineNo(from.getLineNo());
    return csvRecord;
  }

  public String toString() {
    StringBuffer returnStringBuffer = new StringBuffer();
    for (CsvColumn column : columnsInOrder) {
      returnStringBuffer.append(column.name);
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

  public CsvTable copy() { 
    return new CsvTable(this.dataFile, this.unificationOption);
  }
  
  public CsvTable unify(CsvTable candidateTable) {
    CsvTable unified = copy();
    for (CsvColumn column : candidateTable.columnsInOrder){
      if (!unified.containsKey(column.name))
        unified.addColumn(column);
    }
    for (CsvRecord candidateRecord : candidateTable.records) { 
      CsvRecord currentRecord = unified.get(candidateRecord.primaryKeyField.value);
      if (currentRecord == null) { 
        if (unificationOption == UnificationOptions.THROW)
          throw new CsvRecordNotFoundException(
              "Record not found with key equal " + candidateRecord.primaryKeyField.value);
        else if (unificationOption == UnificationOptions.LOG) {
          System.err.println("Record not found in " + unified.name + 
              " with key equal " + candidateRecord.primaryKeyField.value + 
              " from line " + candidateRecord.getLineNo() + " in file " + candidateTable.name);
        } else if (unificationOption == UnificationOptions.DEFAULT) {
          currentRecord = unified.defaulted(candidateRecord);
          unified.add(currentRecord);
          currentRecord.unify(candidateRecord, true);
        } else
          throw new CsvException("Unexpected UnificationOption:" + unificationOption);
      } else
        currentRecord.unify(candidateRecord, false);
    }
    return unified;
  }

  private CsvRecord get(String key) {
    if (keyToIndex.get(key) == null)
      return null;
    else
      return records.get(keyToIndex.get(key));
  }

  public boolean containsKey(String key) {
    return columns.containsKey(key);
  }
  
  public void makeFirst(String columnName) {
    CsvColumn column = columns.get(columnName);
    ArrayList<CsvColumn> newColumnsInOrder = new ArrayList<CsvColumn>();
    newColumnsInOrder.add(column);
    columnsInOrder.remove(column);
    for (CsvColumn existingColumn : columnsInOrder){
      newColumnsInOrder.add(existingColumn);
    }
    columnsInOrder = newColumnsInOrder;
  }

  public static String removeExtension(String name) {
    return name.substring(0, name.lastIndexOf('.'));
  }
  
  public void outputToFile(String outputFileName) throws IOException {
    FileOutputStream out = new FileOutputStream(outputFileName);
    PrintStream p = new PrintStream( out );
      
    p.println (toString());

    p.close();
    
  }


}
