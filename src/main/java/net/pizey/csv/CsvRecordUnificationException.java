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

  public CsvRecordUnificationException(int lineNo, CsvField current, CsvField candidate) {
    super();
    this.lineNo = lineNo;
    this.current = current;
    this.candidate = candidate;
  }

  @Override
  public String getMessage() {
    return "Line " + lineNo + " value found for "
              + candidate.column.getName()
              + " but not equal to current value : '"
              + current.value
              + "' != '"
              + candidate.value + "'";
  }
}
