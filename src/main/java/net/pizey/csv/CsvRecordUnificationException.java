package net.pizey.csv;

/**
 * @author timp
 * @since 2001/11/19
 * 
 */
public class CsvRecordUnificationException extends CsvException {
  private static final long serialVersionUID = -7560282289926548062L;
  private int lineNo;
  private CsvField current;
  private CsvField candidate;
  private String tableName;

  public CsvRecordUnificationException(String tableName, int lineNo, CsvField current,
      CsvField candidate) {
    super();
    this.tableName = tableName;
    this.lineNo = lineNo;
    this.current = current;
    this.candidate = candidate;
  }

  @Override
  public String getMessage() {
    return "Table " + tableName + " line " + lineNo + " value found for "
              + candidate.getColumn().getName()
              + " but not equal to current value : '"
              + current.getValue()
              + "' != '"
              + candidate.getValue() + "'";
  }
}
