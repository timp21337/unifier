package net.pizey.csv;

import java.io.IOException;

public class CsvException extends RuntimeException {
  private static final long serialVersionUID = -7658916534881121257L;

  public CsvException(String message, Throwable cause) {
    super(message, cause);
  }

  public CsvException(String message) {
    super(message);
  }

  public CsvException(IOException e) {
    super(e);
  }

}
