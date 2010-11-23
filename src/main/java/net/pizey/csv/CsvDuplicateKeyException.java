package net.pizey.csv;

public class CsvDuplicateKeyException extends CsvException {

  private static final long serialVersionUID = 7341450818447161242L;
  private String key;
  private int lineNo;

  public CsvDuplicateKeyException(int lineNo, String key) {
    super();
    this.key = key;
    this.lineNo = lineNo;
  }

  @Override
  public String getMessage() {
    return "Line " + lineNo + ": Key " + key + " already exists";
  }

}
