/**
 * 
 */
package net.pizey.csv;

/**
 * @author timp
 * @since 19 Nov 2010 12:12:20
 * 
 */
public class CsvMissingPrimaryKeyException extends CsvException {

  private static final long serialVersionUID = 3718655932745046240L;

  public CsvMissingPrimaryKeyException() {
    super();
  }

  @Override
  public String getMessage() {
    return "Invalid CsvRecord, due to null primary key";
  }

}
