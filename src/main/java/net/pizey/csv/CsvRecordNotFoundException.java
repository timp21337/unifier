package net.pizey.csv;

/**
 * @author timp
 *
 */
public class CsvRecordNotFoundException extends CsvException {

  private static final long serialVersionUID = 3394638589885932426L;

  /**
   * @param message
   * @param cause
   */
  public CsvRecordNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public CsvRecordNotFoundException(String message) {
    super(message);
  }


}
