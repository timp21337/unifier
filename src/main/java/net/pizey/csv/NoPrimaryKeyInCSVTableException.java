package net.pizey.csv;

public class NoPrimaryKeyInCSVTableException extends CsvException {

  private static final long serialVersionUID = 1938053299486320301L;

  public NoPrimaryKeyInCSVTableException(String message, Throwable cause) {
    super(message, cause);
  }

}
