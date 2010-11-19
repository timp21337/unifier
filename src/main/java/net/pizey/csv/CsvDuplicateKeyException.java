package net.pizey.csv;

public class CsvDuplicateKeyException extends CsvException {

  private static final long serialVersionUID = 7341450818447161242L;

  public CsvDuplicateKeyException(String message) {
    super(message);
  }

}
