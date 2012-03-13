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

  /** Name of the file */
  private String name;

  private UnificationOptions unificationOption;

  private HashMap<String, CsvColumn> nameToColumn;
  private ArrayList<CsvColumn> columnsInOrder;
  private CsvColumn primaryKeyColumn;

  private HashMap<String, CsvRecord> keyToRecord;
  private ArrayList<String> keys;

  private String primaryKeyName;

  public CsvTable(String fileName) {
    this(new File(fileName), null);
  }

  public CsvTable(String fileName, String primeKeyName) {
    this(new File(fileName), primeKeyName);
  }

  public CsvTable(String fileName, UnificationOptions unificationOption) {
    this(new File(fileName), null, unificationOption);
  }

  public CsvTable(String fileName, String primeKeyName, UnificationOptions unificationOption) {
    this(new File(fileName), primeKeyName, unificationOption);
  }

  public CsvTable(File file, String primeKeyName) {
    this(file, primeKeyName, UnificationOptions.THROW);
  }

  public CsvTable(File file, String primeKeyName, UnificationOptions unificationOption) {
    super();
    this.dataFile = file;
    this.name = removeExtension(file.getName());
    this.unificationOption = unificationOption;
    this.nameToColumn = new HashMap<String, CsvColumn>();
    this.columnsInOrder = new ArrayList<CsvColumn>();
    this.primaryKeyName = primeKeyName;
    this.primaryKeyColumn = null; // Set in load()
    this.keyToRecord = new HashMap<String, CsvRecord>();
    this.keys = new ArrayList<String>();
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

  public CsvTable(CsvTable other) {
    super();
    this.dataFile = other.dataFile;
    this.name = other.getName();
    this.unificationOption = other.unificationOption;

    this.nameToColumn = new HashMap<String, CsvColumn>();
    for (Entry<String, CsvColumn> e : other.nameToColumn.entrySet()) {
      this.nameToColumn.put(e.getKey(), e.getValue());
    }
    this.columnsInOrder = new ArrayList<CsvColumn>();
    for (CsvColumn c : other.columnsInOrder)
      this.columnsInOrder.add(c);
    this.primaryKeyColumn = other.primaryKeyColumn;
    this.keys = new ArrayList<String>();
    this.keyToRecord = new HashMap<String, CsvRecord>();
    for (String key : other.keys) {
      this.keys.add(key);
      this.keyToRecord.put(key, other.get(key).clone(this));
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
  private void load(CsvFileParser parser) throws IOException {

    defineColumns(parser, this.primaryKeyName);
    CsvRecord record;
    while (null != (record = loadRecord(parser))) {
      add(record);
    }

  }

  /**
   * Process the first line to define columns. The first line contains the field names - this needs to be validated
   * against expected values, and the order of the fields established.
   * 
   * @param primeKeyName
   *          Optional key name
   * 
   */
  private void defineColumns(CsvFileParser parser, String primeKeyName) throws IOException {
    parser.hasNextRecord(); // FIXME relying upon side effect

    while (parser.recordHasMoreFields()) {
      String colName = parser.nextField();
      // If no primeKeyName specified then first column is key
      if (!colName.equals("")) {
        boolean isPrimeKey = primeKeyName == null ?
            columnsInOrder.size() == 0 : colName.equals(this.primaryKeyName);
        CsvColumn column = new CsvColumn(colName, isPrimeKey);
        if (column.isPrimaryKey())
          primaryKeyColumn = column;
        addColumn(column);
      }
    }
  }

  public void addColumn(CsvColumn column) {
    columnsInOrder.add(column);
    nameToColumn.put(column.getName(), column);
    for (CsvRecord r : this) {
      r.put(column.getName(), new CsvField(column, ""));
    }
  }

  public String getName() {
    return this.name;
  }

  public CsvColumn getPrimaryKeyColumn() {
    return primaryKeyColumn;
  }

  public File getDataFile() {
    return dataFile;
  }

  public UnificationOptions getUnificationOption() {
    return unificationOption;
  }

  public HashMap<String, CsvColumn> getNameToColumn() {
    return nameToColumn;
  }

  public HashMap<String, CsvRecord> getKeyToRecord() {
    return keyToRecord;
  }

  public ArrayList<String> getKeys() {
    return keys;
  }

  /**
   * Reads the file until is has seen an object's-worth of field values (ie until it sees an EOF or a line starting with
   * '$') which it returns in a CsvRecord (null if there are no field values).
   * 
   * @return a new CSVRecord
   */
  public CsvRecord loadRecord(CsvFileParser parser) throws IOException {
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
        message += " (Check last line of file)";
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
    CsvRecord csvRecord = new CsvRecord(this);
    csvRecord.addField(new CsvField(
        record.getTable().getPrimaryKeyColumn(), record.getPrimaryKey()));

    for (CsvColumn column : columnsInOrder) {
      if (!column.getName().equals(record.getTable().getPrimaryKeyColumn().getName())) {
        csvRecord.addField(new CsvField(column,
            record.containsKey(column.getName()) ? record.get(column.getName()).getValue() : ""));
      }
    }
    csvRecord.setLineNo(record.getLineNo());
    return csvRecord;
  }

  public CsvTable unify(CsvTable candidateTable, boolean unifyWithEmpty) {
    CsvTable unified = new CsvTable(this);
    for (CsvRecord candidateRecord : candidateTable.values()) {
      CsvRecord currentRecord = unified.get(candidateRecord.getPrimaryKey());
      if (currentRecord == null) {
        String message = "Record not found in " + unified.name
            + " with key equal " + candidateRecord.getPrimaryKey()
            + " from line " + candidateRecord.getLineNo() + " in file "
            + candidateTable.name;
        switch (unificationOption) {
        case THROW:
          throw new CsvRecordNotFoundException(message);
        case LOG:
          System.err.println(message);
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

  public void makeFirstAndPrimary(String columnName) {
    CsvColumn currentPrimaryKeyColumn = getPrimaryKeyColumn();
    currentPrimaryKeyColumn.setPrimaryKey(false);
    CsvColumn column = nameToColumn.get(columnName);
    column.setPrimaryKey(true);
    ArrayList<CsvColumn> newColumnsInOrder = new ArrayList<CsvColumn>();

    newColumnsInOrder.add(column);
    columnsInOrder.remove(column);
    for (CsvColumn existingColumn : columnsInOrder) {
      existingColumn.setPrimaryKey(false);
      newColumnsInOrder.add(existingColumn);
    }
    columnsInOrder = newColumnsInOrder;
    primaryKeyColumn = column;

    ArrayList<String> newKeys = new ArrayList<String>();
    HashMap<String, CsvRecord> reKeyed = new HashMap<String, CsvRecord>();
    for (String oldKey : keys) {
      CsvRecord r = keyToRecord.get(oldKey);
      String newKey = r.get(columnName).getValue();
      newKeys.add(newKey);
      reKeyed.put(newKey, r);
    }
    keys = newKeys;
    keyToRecord = reKeyed;
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
    return nameToColumn.get(name);
  }

  public ArrayList<CsvColumn> getColumnsInOrder() {
    return columnsInOrder;
  }

  public boolean hasColumn(String columnName) {
    return nameToColumn.containsKey(columnName);
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
      returnStringBuffer.append(record.toString());
      returnStringBuffer.append(",\n");
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

  public void add(CsvRecord record) {
    put(record.getPrimaryKey(), record);
  }

  @Override
  /** Put, defaulting missing fields, dropping unknown fields. */
  public CsvRecord put(String key, CsvRecord record) {
    if (keys.contains(key))
      throw new CsvDuplicateKeyException(record.getLineNo(), key);
    keys.add(key);
    return keyToRecord.put(key, addMissingFields(record));
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
    ArrayList<CsvRecord> records = new ArrayList<CsvRecord>();
    for (String key : keys) {
      records.add(keyToRecord.get(key));
    }
    return records;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + columnsInOrder.hashCode();
    result = prime * result + keyToRecord.hashCode();
    result = prime * result + keys.hashCode();
    result = prime * result + name.hashCode();
    result = prime * result + primaryKeyColumn.hashCode();
    result = prime * result + unificationOption.ordinal();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CsvTable other = (CsvTable) obj;
    if (!name.equals(other.name))
      return false;
    if (!primaryKeyColumn.equals(other.primaryKeyColumn))
      return false;
    if (!columnsInOrder.equals(other.columnsInOrder))
      return false;
    if (!keys.equals(other.keys))
      return false;
    if (!keyToRecord.equals(other.keyToRecord))
      return false;
    if (!unificationOption.equals(other.unificationOption))
      return false;

    return true;
  }

}
