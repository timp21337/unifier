package net.pizey.csv;

/**
 * @author timp
 * @since 21 Nov 2010 16:56:32
 *
 */
public class CsvInvalidKeyException extends CsvException {

  private static final long serialVersionUID = 7662520378582672443L;

  public CsvInvalidKeyException() {
  }

  /**
   * @param cause
   */
  public CsvInvalidKeyException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public CsvInvalidKeyException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public CsvInvalidKeyException(String message) {
    super(message);
  }

}
