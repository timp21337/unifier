package net.pizey.csv;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * A record within a CSV File.
 */
public class CsvRecord implements Iterable<CsvField>, Map<String, CsvField> {

  private CsvTable table = null;

  /** The value of the primary key of this record, from the csv file */
  private CsvField primaryKeyField = null;

  /** The line number of the CSV file. */
  private int lineNo;

  /** The record number of the CSV file. */
  private int recordNo;

  private HashMap<String, CsvField> nameToField;

  /**
   * Constructor.
   */
  public CsvRecord(CsvTable parent) {
    super();
    this.table = parent;
    this.nameToField = new HashMap<String, CsvField>();
  }

  /**
   * Add a field to this record.
   */
  public synchronized void addField(CsvField field) {
    if (field.column.isPrimaryKey())
      primaryKeyField = field;
    nameToField.put(field.column.getName(), field);
  }

  public synchronized void replaceField(CsvField oldField, CsvField newField) {
    nameToField.put(oldField.column.getName(), newField);
  }

  @Override
  public Iterator<CsvField> iterator() {
    Vector<CsvField> fieldsReversed = new Vector<CsvField>();
    for (CsvColumn column : table.getColumnsInOrder()) {
      fieldsReversed.add(nameToField.get(column.getName()));
    }
    return fieldsReversed.iterator();
  }

  public void unify(CsvRecord record, boolean allowBlankOverwrite) {
    for (CsvField field : record) {
      if (nameToField.containsKey(field.column.getName())) {
        if (allowBlankOverwrite && nameToField.get(field.column.getName()).value.equals(""))
          replaceField(nameToField.get(field.column.getName()), field);
        else if (!nameToField.get(field.column.getName()).value.equals(field.value))
          throw new CsvException("Line " + lineNo + " value found for " + field.column.getName()
              + " but not equal: '"
              + nameToField.get(field.column.getName())
              + "' != '"
              + field + "'");

      } else
        addField(field);
    }

  }

  /**
   * @param recordNo
   *          The recordNo to set.
   */
  public void setRecordNo(int recordNo) {
    this.recordNo = recordNo;
  }

  /**
   * @return Returns the recordNo.
   */
  public int getRecordNo() {
    return recordNo;
  }

  /**
   * @param lineNo
   *          The lineNo to set.
   */
  public void setLineNo(int lineNo) {
    this.lineNo = lineNo;
  }

  /**
   * @return Returns the lineNo.
   */
  public int getLineNo() {
    return lineNo;
  }

  public CsvField getPrimaryKeyField() {
    return primaryKeyField;
  }

  @Override
  public CsvField get(Object string) {
    return nameToField.get(string);
  }

  @Override
  public void clear() {
    nameToField.clear();
  }

  @Override
  public boolean containsKey(Object key) {
    return nameToField.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return nameToField.containsValue(value);
  }

  @Override
  public Set<java.util.Map.Entry<String, CsvField>> entrySet() {
    return nameToField.entrySet();
  }

  @Override
  public boolean isEmpty() {
    return nameToField.isEmpty();
  }

  @Override
  public Set<String> keySet() {
    return nameToField.keySet();
  }

  @Override
  public CsvField put(String key, CsvField value) {
    return nameToField.put(key, value);
  }

  @Override
  public void putAll(Map<? extends String, ? extends CsvField> m) {
    nameToField.putAll(m);
  }

  @Override
  public CsvField remove(Object key) {
    return nameToField.remove(key);
  }

  @Override
  public int size() {
    return nameToField.size();
  }

  @Override
  public Collection<CsvField> values() {
    return nameToField.values();
  }

  public String toString() {
    String returnString = "";
    for (CsvField f : this) {
      if (!returnString.equals(""))
        returnString += ",";
      returnString += f.toString();
    }
    return "{" + returnString + "}";
  }

}
