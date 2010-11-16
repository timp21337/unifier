/*
 * $Source: /usr/cvsroot/melati/poem/src/main/java/org/melati/poem/csv/CSVRecord.java,v $
 * $Revision: 1.19 $
 *
 * Part of Melati (http://melati.org), a framework for the rapid
 * development of clean, maintainable web applications.
 *
 *  Copyright (C) 2001 Myles Chippendale
 *
 * Melati is free software; Permission is granted to copy, distribute
 * and/or modify this software under the terms either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation; either version 2 of the License, or (at your option)
 *    any later version,
 *
 *    or
 *
 * b) any version of the Melati Software License, as published
 *    at http://melati.org
 *
 * You should have received a copy of the GNU General Public License and
 * the Melati Software License along with this program;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA to obtain the
 * GNU General Public License and visit http://melati.org to obtain the
 * Melati Software License.
 *
 * Feel free to contact the Developers of Melati (http://melati.org),
 * if you would like to work out a different arrangement than the options
 * outlined here.  It is our intention to allow Melati to be used by as
 * wide an audience as possible.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Contact details for copyright holder:
 *
 *     Myles Chippendale <mylesc At paneris.org>
 *
 *
 * ------
 *  Note
 * ------
 *
 * I will assign copyright to PanEris (http://paneris.org) as soon as
 * we have sorted out what sort of legal existence we need to have for
 * that to make sense. 
 * In the meantime, if you want to use Melati on non-GPL terms,
 * contact me!
 */

package net.pizey.csv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import sun.reflect.generics.tree.FieldTypeSignature;

/**
 * A record within a CSV File.
 */
public class CsvRecord implements Iterable<CsvField> {

  private Vector<CsvField> fields;

  /** The value of the primary key of this record, from the csv file */
  String primaryKeyValue = null;

  /** The line number of the CSV file. */
  private int lineNo;

  /** The record number of the CSV file. */
  private int recordNo;
  
  private HashMap<String,String> namesToValues;

  /**
   * Constructor.
   */
  public CsvRecord() {
    super();
    this.fields = new Vector<CsvField>();
    this.namesToValues = new HashMap<String,String>();
  }

  /**
   * Add a field to this record.
   */
  public synchronized void addField(CsvField field) {
    if (field.column.isPrimaryKey)
      primaryKeyValue = field.value;
    fields.addElement(field);
    namesToValues.put(field.column.name, field.value);
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
    return fields.iterator();
  }

  public void unify(CsvRecord record) {
    for (CsvField field : record.fields) { 
      if(namesToValues.containsKey(field.column.name))
        if(!namesToValues.get(field.column.name).equals(field.value))
          throw new RuntimeException("Value found for " + field.column.name + " but not equal:" + 
              namesToValues.get(field.column.name) + " != " + field.value);
        else
          addField(field);
    }
    
  }

}
