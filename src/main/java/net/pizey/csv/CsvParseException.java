package net.pizey.csv;

public class CsvParseException extends RuntimeException {

  private static final long serialVersionUID = 5352405172053904041L;

  public CsvParseException(String string) {
    super(string);
  }

  public CsvParseException(String message, Throwable cause) {
    super(message, cause);
  }

}
