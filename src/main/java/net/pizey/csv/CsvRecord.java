package net.pizey.csv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * A record within a CSV File.
 */
public class CsvRecord implements Iterable<CsvField> {

  private CsvTable parent = null;

  /** The value of the primary key of this record, from the csv file */
  CsvField primaryKeyField = null;

  /** The line number of the CSV file. */
  private int lineNo;

  /** The record number of the CSV file. */
  private int recordNo;
  
  private HashMap<String,CsvField> nameToField;

  /**
   * Constructor.
   */
  public CsvRecord(CsvTable parent) {
    super();
    this.parent = parent;
    this.nameToField = new HashMap<String,CsvField>();
  }

  /**
   * Add a field to this record.
   */
  public synchronized void addField(CsvField field) {
    if (field.column.isPrimaryKey)
      primaryKeyField = field;
    nameToField.put(field.column.name, field);
  }

  public synchronized void replaceField(CsvField oldField, CsvField newField) {
    nameToField.put(oldField.column.name, newField);
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

  @Override
  public Iterator<CsvField> iterator() {
    Vector<CsvField> fields = new Vector<CsvField>();
    for (CsvColumn column : parent.columnsInOrder){
      fields.add(nameToField.get(column.name));
    }
    return fields.iterator();
  }

  public void unify(CsvRecord record, boolean allowBlankOverwrite) {
    for (CsvField field : record) { 
      if(nameToField.containsKey(field.column.name)){
        if (allowBlankOverwrite && nameToField.get(field.column.name).value.equals("")) 
          replaceField(nameToField.get(field.column.name), field);
        else if(!nameToField.get(field.column.name).value.equals(field.value))
          throw new CsvException("Line " + lineNo + " value found for " + field.column.name + 
              " but not equal: '" + 
              nameToField.get(field.column.name) + "' != '" + field.value + "'");
        
      } else
        addField(field);
    }
    
  }


}
