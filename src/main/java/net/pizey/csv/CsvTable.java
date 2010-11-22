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

  private HashMap<String, CsvColumn> nameToColumn = new HashMap<String, CsvColumn>();
  private ArrayList<CsvColumn> columnsInOrder = new ArrayList<CsvColumn>();
  private CsvColumn primaryKeyColumn = null;

  private HashMap<String, CsvRecord> keyToRecord = new HashMap<String, CsvRecord>();
  private ArrayList<String> keys = new ArrayList<String>();

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
    this.dataFile = file;
    this.name = removeExtension(file.getName());
    this.unificationOption = unificationOption;
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
      System.err.println(key + ":" + other.get(key));
      this.keyToRecord.put(key, other.get(key).clone());
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

    while (parser.recordHasMoreFields()) {
      String colName = parser.nextField();
      // First column is always the key
      if (!colName.equals("")) {
        CsvColumn column = new CsvColumn(colName, columnsInOrder.size() == 0);
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

  public void add(CsvRecord record) {
    keys.add(record.getPrimaryKey());
    keyToRecord.put(record.getPrimaryKey(), record);
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
    System.err.println("copy:=\n" + unified);
    System.err.println("copy PK:" + unified.getPrimaryKeyColumn());
    /*
     * for (CsvColumn column : candidateTable.columnsInOrder) { if
     * (!unified.hasColumn(column.getName())) if (unifyWithEmpty)
     * unified.addColumn(column); }
     */
    for (CsvRecord candidateRecord : candidateTable.values()) {
      CsvRecord currentRecord = unified.get(candidateRecord.getPrimaryKey());
      if (currentRecord != null)
        System.err.println("Unifying "
            + candidateTable.getPrimaryKeyColumn().getName() + ":"
            + candidateRecord.getPrimaryKey()
            + " with "
            + unified.getPrimaryKeyColumn().getName() + ":"
            + unified.get(candidateRecord.getPrimaryKey()).getPrimaryKey());
      else
        System.err.println("Null" + candidateTable.getPrimaryKeyColumn().getName() + ":"
            + candidateRecord.getPrimaryKey());
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
    System.err.println("Returning\n" + unified);
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
      System.err.println(oldKey + " becomes " + newKey);
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
      if (record == null)
        throw new NullPointerException("No value for key " + key + " in table " + getName());
      for (CsvField field : record) {
        if (field == null)
          throw new NullPointerException("No value for field " + field + " in table " + getName());
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
    result = prime * result + ((columnsInOrder == null) ? 0 : columnsInOrder.hashCode());
    result = prime * result + ((dataFile == null) ? 0 : dataFile.hashCode());
    result = prime * result + ((keyToRecord == null) ? 0 : keyToRecord.hashCode());
    result = prime * result + ((keys == null) ? 0 : keys.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((nameToColumn == null) ? 0 : nameToColumn.hashCode());
    result = prime * result + ((primaryKeyColumn == null) ? 0 : primaryKeyColumn.hashCode());
    result = prime * result + ((unificationOption == null) ? 0 : unificationOption.hashCode());
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
    if (columnsInOrder == null) {
      if (other.columnsInOrder != null)
        return false;
    } else if (!columnsInOrder.equals(other.columnsInOrder)) {
      System.err.println("CIO");
      return false;
    }
    if (dataFile == null) {
      if (other.dataFile != null)
        return false;
    } else if (!dataFile.equals(other.dataFile)) {
      System.err.println("File");
      return false;
    }
    if (keyToRecord == null) {
      if (other.keyToRecord != null)
        return false;
    } else if (!keyToRecord.equals(other.keyToRecord)) {
      System.err.println("ktr");
      return false;
    }
    if (keys == null) {
      if (other.keys != null)
        return false;
    } else if (!keys.equals(other.keys))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (nameToColumn == null) {
      if (other.nameToColumn != null)
        return false;
    } else if (!nameToColumn.equals(other.nameToColumn))
      return false;
    if (primaryKeyColumn == null) {
      if (other.primaryKeyColumn != null)
        return false;
    } else if (!primaryKeyColumn.equals(other.primaryKeyColumn))
      return false;
    if (unificationOption == null) {
      if (other.unificationOption != null)
        return false;
    } else if (!unificationOption.equals(other.unificationOption))
      return false;

    return true;
  }

}
