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

  /** The line number of the CSV file. */
  private int lineNo;

  /** The values, keyed by name */
  private HashMap<String, CsvField> nameToField;

  /**
   * Constructor.
   */
  public CsvRecord(CsvTable table) {
    super();
    this.table = table;
    this.nameToField = new HashMap<String, CsvField>();
  }

  public synchronized void replaceField(CsvField oldField, CsvField newField) {
    System.err.println("Replacing " + oldField.getColumn().getName() + " with " + newField);
    nameToField.put(oldField.getColumn().getName(), newField);
  }

  /**
   * Unify this record with another, adding new fields.
   * 
   * @param unifyWithEmpty
   *          whether a filled field can unify with an empty one
   */
  public void unify(CsvRecord candidateRecord, boolean unifyWithEmpty) {
    System.err.println("CsvRecord.unify unifywithempty=" + unifyWithEmpty);
    for (CsvField candidateField : candidateRecord) {
      if (this.nameToField.containsKey(candidateField.getColumn().getName())) {
        CsvField currentField = nameToField.get(candidateField.getColumn().getName());
        if (currentField.getValue().equals("") && unifyWithEmpty)
          replaceField(currentField, candidateField);
        else if (!currentField.getValue().equals(candidateField.getValue()))
          throw new CsvRecordUnificationException(candidateRecord.getTable().getName(),
              candidateRecord.getLineNo(),
              currentField,
              candidateField);
      } else {
        if (unifyWithEmpty) {
          if (!getTable().hasColumn(candidateField.getColumn().getName())) {
            System.err.println("Adding column:" + candidateField.getColumn());
            getTable().addColumn(candidateField.getColumn());
          }
          System.err.println("Adding value:" + candidateField);
          addField(candidateField);
        }
      }
    }

  }

  public CsvTable getTable() {
    return table;
  }

  /**
   * Add a field to this record.
   */
  public synchronized CsvField addField(CsvField field) {
    return nameToField.put(field.getColumn().getName(), field);
  }

  public int getLineNo() {
    return lineNo;
  }

  public void setLineNo(int lineNo) {
    this.lineNo = lineNo;
  }

  @Override
  public Iterator<CsvField> iterator() {
    System.err.println("CsvRecord iterator");
    Vector<CsvField> fieldsReversed = new Vector<CsvField>();
    for (CsvColumn column : getTable().getColumnsInOrder()) {
      fieldsReversed.add(nameToField.get(column.getName()));
      System.err.println("  adding " + column.getName());
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
    if (!field.getColumn().getName().equals(key))
      throw new CsvInvalidKeyException(
          "Key (" + key + ") not equal to " +
              "Field column name (" + field.getColumn().getName() + ")");
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

  @Override
  protected CsvRecord clone() {
    return new CsvRecord(this.getTable());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + lineNo;
    result = prime * result + ((nameToField == null) ? 0 : nameToField.hashCode());
    result = prime * result + ((table == null) ? 0 : table.hashCode());
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
    CsvRecord other = (CsvRecord) obj;
    if (lineNo != other.lineNo)
      return false;
    if (nameToField == null) {
      if (other.nameToField != null)
        return false;
    } else if (!nameToField.equals(other.nameToField))
      return false;
    if (table == null) {
      if (other.table != null)
        return false;
    } else if (!table.equals(other.table))
      return false;
    return true;
  }

  public String getPrimaryKey() {
    CsvColumn primaryKeyColumn = getTable().getPrimaryKeyColumn();
    if (primaryKeyColumn == null)
      throw new CsvMissingPrimaryKeyException();

    CsvField f = get(primaryKeyColumn.getName());
    if (f == null)
      throw new CsvMissingPrimaryKeyException();

    return f.getValue();
  }
}
