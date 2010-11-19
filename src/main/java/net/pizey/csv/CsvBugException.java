package net.pizey.csv;

public class CsvBugException extends CsvException {
  private static final long serialVersionUID = -8166488866144020929L;

  public CsvBugException(String message, Throwable cause) {
    super(message, cause);
  }

  public CsvBugException(String message) {
    super(message);
  }

}
