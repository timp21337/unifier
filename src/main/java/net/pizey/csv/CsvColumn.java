/*
 * $Source: /usr/cvsroot/melati/poem/src/main/java/org/melati/poem/csv/CSVColumn.java,v $
 * $Revision: 1.4 $
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
 *     Myles Chippendale <mylesc@paneris.org>
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

/**
 * A column in a CSV file.
 * <p>
 * A CSVColumn can be a primary key for this table, ie unique and used by other
 * tables to refer to this record.
 * <p>
 * A CSVColumn can also be a foreign key into another {@link CsvTable}.
 * 
 */
public class CsvColumn {

  String name = null;
  boolean isPrimaryKey = false;
  CsvTable foreignTable = null;

  /**
   * Simplest case constructor.
   * 
   * @param name
   *          the name of the column.
   */
  public CsvColumn(String name) {
    this.name = name;
  }

  /**
   * Constructor for a key value into another table.
   * 
   * @param name
   *          the name of the POEM column this is to be mapped to.
   * @param foreignTable
   *          another CSVTable in which this value should be found.
   */
  public CsvColumn(String name, CsvTable foreignTable) {
    this.name = name;
    this.foreignTable = foreignTable;
  }

  /**
   * Constructor for a key value into another table.
   * 
   * @param name
   *          the name of the POEM column this is to be mapped to.
   * @param isPrimaryKey
   *          flag to indicate this is the primary key column.
   */
  public CsvColumn(String name, boolean isPrimaryKey) {
    this.name = name;
    this.isPrimaryKey = isPrimaryKey;
  }

}
