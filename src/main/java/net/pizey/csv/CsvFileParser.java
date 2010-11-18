package net.pizey.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * A utility for tokenising a file made up of comma-separated variables. We
 * allow for fields having returns in them.
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
 * Each record (which is usually a line, unless some fields have a line break in
 * them) is accessed one at a time by calling <code>nextRecord()</code>. Within
 * each record <code>recordHasMoreFields()</code> and <code>nextField()</code>
 * can be used like an Enumeration to iterate through the fields.
 * 
 * @author mylesc, based heavily on original CSVStringEnumeration by williamc
 */

public class CsvFileParser {

  private BufferedReader reader = null;

  int lineNo = 0; // The first line will be line '1'
  private String line = "";
  private boolean emptyLastField = false;
  int position = 0;

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
   * @return whether there is another line
   */
  public boolean hasNextRecord() {
    try {
      line = reader.readLine();
      // This should be false anyway if we're called from nextToken()
      emptyLastField = false;
      position = 0;
      if (line == null) {
        return false;
      }
      lineNo++;
      return true;
    } catch (IOException e) {
      throw new CsvParseException("Unexpected IO exception", e);
    }
  }

  /**
   * Return the line number.
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
  public String nextField() {
    return nextToken(false);
  }

  /**
   * @return the next token as a String
   */
  private String nextToken(boolean inUnclosedQuotes) {

    if (emptyLastField) {
      emptyLastField = false;
      return "";
    }

    if (position >= line.length())
      throw new NoSuchElementException("Line " + lineNo + 
          ": Position " + position + ", line length " + line.length());

    if (inUnclosedQuotes || (line.charAt(position) == '"' && (++position > 0))) {

      // we need to allow for quotes inside quoted fields, so now test for ",
      int q = line.indexOf("\",", position);
      // if it is not there, we are (hopefully) at the end of a line
      if (q == -1 && (line.indexOf('"', position) == line.length() - 1))
        q = line.length() - 1;

      // If we don't find the end quote try reading in more lines
      // since fields can have \n in them
      if (q == -1) {
        String sofar = line.substring(position, line.length());
        if (!hasNextRecord())
          throw new IllegalArgumentException("Unclosed quotes on line "
              + lineNo);
        return sofar + "\n" + nextToken(true);
      }

      String it = line.substring(position, q);

      ++q;
      position = q + 1;
      if (q < line.length()) {
        if (line.charAt(q) != ',') {
          position = line.length();
          throw new IllegalArgumentException("No comma after quotes on line "
              + lineNo);
        } else if (q == line.length() - 1)
          emptyLastField = true;
      }
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

}
