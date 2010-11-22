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

  public synchronized void replaceField(CsvField oldField, CsvField newField) {
    nameToField.put(oldField.getColumn().getName(), newField);
  }

  /**
   * Unify this record with another, adding new fields.
   * 
   * @param unifyWithEmpty
   *          whether a filled field can unify with an empty one
   */
  public void unify(CsvRecord candidateRecord, boolean unifyWithEmpty) {
    for (CsvField candidateField : candidateRecord) {
      if (this.nameToField.containsKey(candidateField.getColumn().getName())) {
        CsvField currentField = nameToField.get(candidateField.getColumn().getName());
        if (currentField.getValue().equals("") && unifyWithEmpty)
          replaceField(currentField, candidateField);
        else if (!currentField.getValue().equals(candidateField.getValue()))
          throw new CsvRecordUnificationException(lineNo, currentField, candidateField);
      } else
        addField(candidateField);
    }

  }

  /**
   * Add a field to this record.
   */
  public synchronized CsvField addField(CsvField field) {
    if (field.getColumn().isPrimaryKey())
      setPrimaryKeyField(field);
    return nameToField.put(field.getColumn().getName(), field);
  }

  public CsvField getPrimaryKeyField() {
    return primaryKeyField;
  }

  private void setPrimaryKeyField(CsvField pk) {
    if (primaryKeyField != null && !primaryKeyField.equals(pk))
      throw new CsvPrimaryKeyAlreadySetException(primaryKeyField, pk);
    primaryKeyField = pk;
  }

  public int getRecordNo() {
    return recordNo;
  }

  public void setRecordNo(int recordNo) {
    this.recordNo = recordNo;
  }

  public int getLineNo() {
    return lineNo;
  }

  public void setLineNo(int lineNo) {
    this.lineNo = lineNo;
  }

  @Override
  public Iterator<CsvField> iterator() {
    Vector<CsvField> fieldsReversed = new Vector<CsvField>();
    for (CsvColumn column : table.getColumnsInOrder()) {
      fieldsReversed.add(nameToField.get(column.getName()));
    }
    return fieldsReversed.iterator();
  }

  @Override
  public CsvField get(Object string) {
    return nameToField.get(string);
  }

  /** Would lead to invalid Tables */
  @Override
  public void clear() {
    throw new java.lang.UnsupportedOperationException();
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
  public CsvField put(String key, CsvField field) {
    if(!field.getColumn().getName().equals(key))
      throw new CsvInvalidKeyException(
          "Key ("+key+") not equal to " + 
          "Field column name (" + field.getColumn().getName() +")");
    return addField(field);
  }

  @Override
  public void putAll(Map<? extends String, ? extends CsvField> m) {
    for (CsvField f : m.values()) {
      addField(f);
    }
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
