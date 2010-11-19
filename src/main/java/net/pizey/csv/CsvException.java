package net.pizey.csv;

public class CsvException extends RuntimeException {
  private static final long serialVersionUID = -7658916534881121257L;

  public CsvException() {
    super();
  }

  public CsvException(Throwable cause) {
    super(cause);
  }

  public CsvException(String message, Throwable cause) {
    super(message, cause);
  }

  public CsvException(String message) {
    super(message);
  }

}
