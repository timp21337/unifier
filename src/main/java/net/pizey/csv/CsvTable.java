package net.pizey.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A representation of a CSV file.
 */
public class CsvTable implements Iterable<CsvRecord>, Map<String, CsvRecord> {

  private File dataFile = null;

  private HashMap<String, CsvColumn> columns = new HashMap<String, CsvColumn>();
  private ArrayList<CsvColumn> columnsInOrder = new ArrayList<CsvColumn>();
  private CsvColumn primaryKey = null;
  private HashMap<String, CsvRecord> keyToRecord = new HashMap<String, CsvRecord>();
  private ArrayList<String> keys = new ArrayList<String>();

  private UnificationOptions unificationOption;

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
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(this.dataFile));
    } catch (IOException e) {
      throw new CsvException("Unexpected exception", e);
    }
    load(new CsvFileParser(reader));
    try {
      reader.close();
    } catch (IOException e) {
      throw new CsvException("Unexpected exception", e);
    }
  }

  /**
   * Parse the CSV data file and store the data for saving later.
   * 
   * @throws CsvParseException
   *           if there is a malformed field in the CSV
   * @throws CSVWriteDownException
   * @throws NoPrimaryKeyInCSVTableException
   */
  private void load(CsvFileParser parser) {

    defineColumns(parser);
    CsvRecord record;
    while (null != (record = loadRecord(parser))) {
      add(record);
    }

  }

  /**
   * Process the first line to define columns. The first line contains the field
   * names - this needs to be validated against expected values, and the order
   * of the fields established.
   * 
   */
  private void defineColumns(CsvFileParser parser) {
    parser.hasNextRecord(); // FIXME relying upon side effect
    recordNo = 0;

    while (parser.recordHasMoreFields()) {
      String colName = parser.nextField();
      // First column is always the key
      if (!colName.equals("")) {
        CsvColumn column = new CsvColumn(colName, columnsInOrder.size() == 0);
        if (column.isPrimaryKey())
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
    columns.put(column.getName(), column);
  }

  public String getName() {
    return this.name;
  }

  public void add(CsvRecord record) {
    if (record.getPrimaryKeyField() == null)
      throw new RuntimeException("Bug: primary key null");
    record.setRecordNo(recordNo++);
    keys.add(record.getPrimaryKeyField().value);
    keyToRecord.put(record.getPrimaryKeyField().value, record);
  }

  /**
   * Reads the file until is has seen an object's-worth of field values (ie
   * until it sees an EOF or a line starting with '$') which it returns in a
   * CsvRecord (null if there are no field values).
   * 
   * @return a new CSVRecord
   */
  public CsvRecord loadRecord(CsvFileParser parser) {
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
            + parser.getLineNo();

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
    if (from.getPrimaryKeyField() == null)
      throw new CsvException("Invalid record primeKeyField is null");
    CsvRecord csvRecord = new CsvRecord(this);
    csvRecord.addField(from.getPrimaryKeyField());

    for (CsvColumn column : columnsInOrder) {
      if (!column.getName().equals(from.getPrimaryKeyField().column.getName()))
        csvRecord.addField(new CsvField(column, ""));
    }
    csvRecord.setLineNo(from.getLineNo());
    return csvRecord;
  }

  public String toString() {
    StringBuffer returnStringBuffer = new StringBuffer();
    for (CsvColumn column : columnsInOrder) {
      returnStringBuffer.append(column.getName());
      returnStringBuffer.append(',');
    }
    returnStringBuffer.append("\n");

    for (String key : keys) {
      CsvRecord record = keyToRecord.get(key);
      for (CsvField field : record) {
        returnStringBuffer.append(field.value);
        returnStringBuffer.append(',');
      }
      returnStringBuffer.append("\n");
    }
    return returnStringBuffer.toString();
  }

  public CsvTable copy() {
    return new CsvTable(this.dataFile, this.unificationOption);
  }

  public CsvTable unify(CsvTable candidateTable) {
    CsvTable unified = copy();
    for (CsvColumn column : candidateTable.columnsInOrder) {
      if (!unified.hasColumn(column.getName()))
        unified.addColumn(column);
    }
    for (CsvRecord candidateRecord : candidateTable.values()) {
      CsvRecord currentRecord = unified.get(candidateRecord
          .getPrimaryKeyField().value);
      if (currentRecord == null) {
        if (unificationOption == UnificationOptions.THROW)
          throw new CsvRecordNotFoundException(
              "Record not found with key equal "
                  + candidateRecord.getPrimaryKeyField().value);
        else if (unificationOption == UnificationOptions.LOG) {
          System.err.println("Record not found in " + unified.name
              + " with key equal " + candidateRecord.getPrimaryKeyField().value
              + " from line " + candidateRecord.getLineNo() + " in file "
              + candidateTable.name);
        } else if (unificationOption == UnificationOptions.DEFAULT) {
          currentRecord = unified.defaulted(candidateRecord);
          unified.add(currentRecord);
          currentRecord.unify(candidateRecord, true);
        } else
          throw new CsvException("Unexpected UnificationOption:"
              + unificationOption);
      } else
        currentRecord.unify(candidateRecord, false);
    }
    return unified;
  }

  public void makeFirst(String columnName) {
    CsvColumn column = columns.get(columnName);
    ArrayList<CsvColumn> newColumnsInOrder = new ArrayList<CsvColumn>();
    newColumnsInOrder.add(column);
    columnsInOrder.remove(column);
    for (CsvColumn existingColumn : columnsInOrder) {
      newColumnsInOrder.add(existingColumn);
    }
    columnsInOrder = newColumnsInOrder;
  }

  public static String removeExtension(String name) {
    return name.lastIndexOf('.') > -1 ? name
        .substring(0, name.lastIndexOf('.')) : name;
  }

  public void outputToFile(String outputFileName) throws IOException {
    FileOutputStream out = new FileOutputStream(outputFileName);
    PrintStream p = new PrintStream(out);

    p.println(toString());

    p.close();

  }

  public CsvColumn getColumn(String name) {
    return columns.get(name);
  }

  public ArrayList<CsvColumn> getColumnsInOrder() {
    return columnsInOrder;
  }

  public boolean hasColumn(String columnName) {
    return columns.containsKey(columnName);
  }

  @Override
  public void clear() {
    keyToRecord.clear();
  }

  @Override
  public CsvRecord get(Object key) {
    return keyToRecord.get(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return keyToRecord.containsValue(value);
  }

  @Override
  public Set<java.util.Map.Entry<String, CsvRecord>> entrySet() {
    return keyToRecord.entrySet();
  }

  @Override
  public boolean isEmpty() {
    return keyToRecord.isEmpty();
  }

  @Override
  public Set<String> keySet() {
    return keyToRecord.keySet();
  }

  @Override
  public CsvRecord put(String key, CsvRecord value) {
    return keyToRecord.put(key, value);
  }

  @Override
  public void putAll(Map<? extends String, ? extends CsvRecord> m) {
    keyToRecord.putAll(m);

  }

  @Override
  public CsvRecord remove(Object key) {
    return keyToRecord.remove(key);
  }

  @Override
  public int size() {
    return keyToRecord.size();
  }

  @Override
  public Collection<CsvRecord> values() {
    return keyToRecord.values();
  }

  @Override
  public boolean containsKey(Object key) {
    return keyToRecord.containsKey(key);
  }

  @Override
  public Iterator<CsvRecord> iterator() {
    return keyToRecord.values().iterator();
  }

}
