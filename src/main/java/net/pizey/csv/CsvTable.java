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
public class CsvTable implements Map<String, CsvRecord>, Iterable<CsvRecord> {

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
    super();
    this.unificationOption = unificationOption;
    this.dataFile = file;
    this.name = removeExtension(file.getName());
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(this.dataFile));
      load(new CsvFileParser(reader));
      reader.close();
    } catch (IOException e) {
      // Naughty me, FileNotFoundException is provocable,
      // BufferedReader.close exception is not,
      // so shared for code coverage win.
      throw new CsvBugException("Unexpected exception", e);
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

  public CsvColumn getPrimaryKeyColumn() {
    return primaryKey;
  }

  public void add(CsvRecord record) {
    if (record.getPrimaryKeyField() == null)
      throw new CsvMissingPrimaryKeyException();
    record.setRecordNo(recordNo++);
    keys.add(record.getPrimaryKeyField().getValue());
    keyToRecord.put(record.getPrimaryKeyField().getValue(), record);
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

  /** Add defaulted values, discard unknown fields */
  public CsvRecord addMissingFields(CsvRecord record) {
    if (record.getPrimaryKeyField() == null)
      throw new CsvMissingPrimaryKeyException();
    CsvRecord csvRecord = new CsvRecord(this);
    csvRecord.addField(record.getPrimaryKeyField());

    for (CsvColumn column : columnsInOrder) {
      if (!column.getName().equals(record.getPrimaryKeyField().getColumn().getName())) {
        csvRecord.addField(new CsvField(column,
            record.containsKey(column.getName()) ? record.get(column.getName()).getValue() : ""));
      }
    }
    csvRecord.setLineNo(record.getLineNo());
    return csvRecord;
  }

  public CsvTable copy() {
    return new CsvTable(this.dataFile, this.unificationOption);
  }

  public CsvTable unify(CsvTable candidateTable, boolean unifyWithEmpty) {
    CsvTable unified = this.copy();
    for (CsvColumn column : candidateTable.columnsInOrder) {
      if (!unified.hasColumn(column.getName()))
        unified.addColumn(column);
    }
    for (CsvRecord candidateRecord : candidateTable.values()) {
      CsvRecord currentRecord = unified.get(candidateRecord
          .getPrimaryKeyField().getValue());
      if (currentRecord == null) {
        switch (unificationOption) {
        case THROW:
          throw new CsvRecordNotFoundException(
              "Record not found with key equal "
                  + candidateRecord.getPrimaryKeyField().getValue());
        case LOG:
          System.err.println("Record not found in " + unified.name
              + " with key equal " + candidateRecord.getPrimaryKeyField().getValue()
              + " from line " + candidateRecord.getLineNo() + " in file "
              + candidateTable.name);
          break;
        case DEFAULT:
          candidateRecord = unified.addMissingFields(candidateRecord);
          unified.add(candidateRecord);
          break;
        }
      } else
        currentRecord.unify(candidateRecord, unifyWithEmpty);
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
        returnStringBuffer.append(field.getValue());
        returnStringBuffer.append(',');
      }
      returnStringBuffer.append("\n");
    }
    return returnStringBuffer.toString();
  }

  @Override
  public void clear() {
    keys = new ArrayList<String>();
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
  /** Put, defaulting missing fields, dropping unknown fields. */
  public CsvRecord put(String key, CsvRecord value) {
    if (keys.contains(key))
      throw new CsvDuplicateKeyException("Key " + key + " already exists");
    keys.add(key);
    return keyToRecord.put(key, addMissingFields(value));
  }

  @Override
  public void putAll(Map<? extends String, ? extends CsvRecord> m) {
    for (Entry<? extends String, ? extends CsvRecord> e : m.entrySet()) {
      put(e.getKey(), e.getValue());
    }
  }

  @Override
  public CsvRecord remove(Object key) {
    keys.remove(key);
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
    ArrayList<CsvRecord> records = new ArrayList<CsvRecord>();
    for (String key : keys) {
      records.add(keyToRecord.get(key));
    }
    return records.iterator();
  }

}
