package net.pizey.csv;

public class CsvPrimaryKeyColumnAlreadySetException extends CsvException {

  private static final long serialVersionUID = 7126068034527131985L;

  public CsvPrimaryKeyColumnAlreadySetException(String message, Throwable cause) {
    super(message, cause);
  }

  public CsvPrimaryKeyColumnAlreadySetException(String message) {
    super(message);
  }

}
