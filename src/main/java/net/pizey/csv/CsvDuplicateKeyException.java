package net.pizey.csv;

public class CsvDuplicateKeyException extends CsvException {

  private static final long serialVersionUID = 7341450818447161242L;
  private String key;

  public CsvDuplicateKeyException(String key) {
    super();
    this.key = key;
  }

  /* (non-Javadoc)
   * @see java.lang.Throwable#getMessage()
   */
  @Override
  public String getMessage() {
    return "Key " + key + " already exists";
  }

}
