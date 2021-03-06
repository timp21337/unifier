package net.pizey.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * A utility for tokenising a file made up of comma-separated variables. We allow for fields having returns in them.
 * 
 * <PRE>
 *   foo, bar om,,"baz, ,oof",xyz,   ->
 *     "foo", " bar om", "", "baz, , oof", "xyz", ""
 * 
 *   foo, "bar
 *   bar
 *   bar", baz ->
 *   "foo", "bar\u0015bar\u0015bar", "baz"
 * </PRE>
 * 
 * Each record (which is usually a line, unless some fields have a line break in them) is accessed one at a time by
 * calling <code>nextRecord()</code>. Within each record <code>recordHasMoreFields()</code> and <code>nextField()</code>
 * can be used like an Enumeration to iterate through the fields.
 * 
 * @author mylesc, based heavily on original CSVStringEnumeration by williamc
 */

public class CsvFileParser {

  private BufferedReader reader = null;

  private int lineNo = 0; // The first line will be line '1'
  private String line = "";
  private boolean emptyLastField = false;
  private int position = 0;

  /**
   * Constructor.
   * 
   * @param reader
   *          file reader
   */
  public CsvFileParser(BufferedReader reader) {
    this.reader = reader;
  }

  /**
   * Return the current line number.
   * 
   * @return the current lineNo
   */
  public int getLineNo() {
    return lineNo;
  }

  /**
   * Are there any more tokens to come?
   * 
   * @return whether there are more fields
   */
  public boolean recordHasMoreFields() {
    return emptyLastField || position < line.length();
  }

  /**
   * @return the next token as a String
   */
  public String nextField() throws IOException {
    return nextToken(false);
  }

  /**
   * @return the next token as a String
   */
  private String nextToken(boolean inUnclosedQuotes) throws IOException {

    if (emptyLastField) {
      emptyLastField = false;
      return "";
    }

    if (position >= line.length())
      throw new NoSuchElementException("Line " + lineNo + ": Position "
          + position + ", line length " + line.length() + " (eof before end of token)");

    if (line.charAt(position) == '"') {
      ++position;
      inUnclosedQuotes = true;
    }
    if (inUnclosedQuotes ) {

      // we need to allow for quotes inside quoted fields, so now test for ",
      int closingQuotePosition = line.indexOf("\",", position);
      // if it is not there, we are (hopefully) at the end of a line
      if (closingQuotePosition == -1) {
        if (line.indexOf('"', position) == line.length() - 1)
          closingQuotePosition = line.length() - 1;

        // If we don't find the end quote try reading in more lines
        // since fields can have \n in them
        else {
          String sofar = line.substring(position, line.length());
          if (!hasNextRecord())
            throw new IllegalArgumentException("Unclosed quoted field on line "
                + lineNo);
          return sofar + "\n" + nextToken(inUnclosedQuotes);
        }
      }

      String it = line.substring(position, closingQuotePosition);

      if (closingQuotePosition + 1 < line.length()) { // we found a comma
        if (closingQuotePosition + 1 == line.length() - 1)
          emptyLastField = true;
      }
      position = closingQuotePosition + 2;
      return it;
    } else {
      int q = line.indexOf(',', position);
      if (q == -1) {
        String it = line.substring(position);
        position = line.length();
        return it;
      } else {
        String it = line.substring(position, q);
        if (q == line.length() - 1)
          emptyLastField = true;
        position = q + 1;
        return it;
      }
    }
  }

  /**
   * @return whether there is another line
   */
  public boolean hasNextRecord() throws IOException {
    line = reader.readLine();
    // This should be false anyway if we're called from nextToken()
    emptyLastField = false;
    position = 0;
    if (line == null) {
      return false;
    }
    lineNo++;
    return true;
  }

}
