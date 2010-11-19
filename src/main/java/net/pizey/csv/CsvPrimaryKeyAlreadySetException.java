package net.pizey.csv;

/**
 * @author timp
 * 
 */
public class CsvPrimaryKeyAlreadySetException extends CsvException {

  private static final long serialVersionUID = -750363928858474792L;
  private CsvField currentPrimaryKeyField;
  private CsvField replacementPrimaryKeyField;

  /**
   * @param primaryKeyField
   * @param pk
   */
  public CsvPrimaryKeyAlreadySetException(CsvField currentPrimaryKeyField,
      CsvField replacementPrimaryKeyField) {
    super();
    this.currentPrimaryKeyField = currentPrimaryKeyField;
    this.replacementPrimaryKeyField = replacementPrimaryKeyField;
  }

  @Override
  public String getMessage() {
    return "Primary key already set to " + currentPrimaryKeyField
        + " cannot reset to " + replacementPrimaryKeyField;
  }

}
