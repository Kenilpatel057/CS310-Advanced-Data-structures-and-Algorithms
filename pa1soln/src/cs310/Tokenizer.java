package cs310;

import java.io.PushbackReader;
import java.io.Reader;
import java.io.IOException;

// Tokenizer class.
//
// CONSTRUCTION: with a Reader object
// ******************PUBLIC OPERATIONS***********************
// char getNextOpenClose( )   --> Get next opening/closing sym
// String getNextID( )        --> Get next Java identifier
// int getLineNumber( )       --> Return line number
// int getErrorCount( )       --> Return error count
// String skippedText()       --> Return text skipped while looking for the current id
// ******************ERRORS**********************************
// Error checking on comments and quotes is performed
// main checks for balanced symbols.

public class Tokenizer implements JavaTokenizer {
	/**
	 * Constructor.
	 * 
	 * @param inStream the stream containing a program.
	 */
	public Tokenizer(Reader inStream) {
		errors = 0;
		ch = '\0';
		currentLine = 1;
		in = new PushbackReader(inStream);
	}

	/**
	 * Gets current line number.
	 * 
	 * @return current line number.
	 */
	public int getLineNumber() {
		return currentLine;
	}

	/**
	 * Gets error count.
	 * 
	 * @return error count.
	 */
	public int getErrorCount() {
		return errors;
	}

	/**
	 * Get the next opening or closing symbol. Return false if end of file. Skip
	 * past comments and character and string constants
	 */
	public char getNextOpenClose() {
		while (nextChar()) {
			skippedText.append(ch);
			if (ch == '/')
				processSlash();
			else if (ch == '\'' || ch == '"')
				skipQuote(ch);
			else if (ch == '\\') { // Extra case, not in text
				nextChar();
				skippedText.append(ch);
			}
			else if (ch == '(' || ch == '[' || ch == '{' || ch == ')' || ch == ']' || ch == '}')
				return ch;
		}
		return '\0'; // End of file
	}

	/**
	 * Return true if ch can be part of a Java identifier
	 */
	private static final boolean isIdChar(char ch) {
		return Character.isJavaIdentifierPart(ch);
	}

	/**
	 * Return an identifier read from input stream First character is already read
	 * into ch
	 */
	private String getRemainingString() {
		StringBuilder result = new StringBuilder("" + ch);

		for (; nextChar(); result.append(ch))
			if (!isIdChar(ch)) {
				putBackChar();
				break;
			}

		return new String(result);
	}

	/**
	 * Return next identifier, skipping comments string constants, and character
	 * constants. Place identifier in currentIdNode.word and return false only if
	 * end of stream is reached.
	 */
	public String getNextID() {
		while (nextChar()) {
			if (ch == '/') {
				skippedText.append(ch);
				processSlash();
			} else if (ch == '\\') {
				skippedText.append(ch);
				nextChar();
			} else if (ch == '\'' || ch == '"') {
				skippedText.append(ch);
				skipQuote(ch);
			} else if (!Character.isDigit(ch) && isIdChar(ch))
				return getRemainingString();
			else skippedText.append(ch);
		}

		return ""; // End of file
	}
	
	public String skippedText() {
		String returnText = skippedText.toString();
		skippedText = new StringBuilder();
		return returnText;
	}
	/**
	 * nextChar sets ch based on the next character in the input stream. putBackChar
	 * puts the character back onto the stream. It should only be used once after a
	 * nextChar. Both routines adjust currentLine if necessary.
	 */
	private boolean nextChar() {
		try {
			int readVal = in.read();
			if (readVal == -1)
				return false;
			ch = (char) readVal;
			if (ch == '\n')
				currentLine++;
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private void putBackChar() {
		if (ch == '\n')
			currentLine--;
		try {
			in.unread((int) ch);
		} catch (IOException e) {
		}
	}

	/**
	 * Precondition: We are about to process a comment; have already seen
	 * comment-start token Postcondition: Stream will be set immediately after
	 * comment-ending token
	 */
	private void skipComment(int start) {
		if (start == SLASH_SLASH) {
			while (nextChar() && (ch != '\n'))
				skippedText.append(ch);
			skippedText.append(ch);
			return;
		}

		// Look for a */ sequence
		boolean state = false; // True if we have seen *

		while (nextChar()) {
			skippedText.append(ch);
			if (state && ch == '/')
				return;
			state = (ch == '*');
		}
		errors++;
		System.out.println("Unterminated comment!");
	}

	/**
	 * Precondition: We are about to process a quote; have already seen beginning
	 * quote. Postcondition: Stream will be set immediately after matching quote
	 */
	private void skipQuote(char quoteType) {
		while (nextChar()) {
			skippedText.append(ch);
			if (ch == quoteType)
				return;
			if (ch == '\n') {
				errors++;
				System.out.println("Missing closed quote at line " + currentLine);
				return;
			} else if (ch == '\\') {			
				nextChar();
				skippedText.append(ch);
			}
		}
	}

	/**
	 * After the opening slash is seen deal with next character. If it is a comment
	 * starter, process it; otherwise put back the next character if it is not a
	 * newline.
	 */
	private void processSlash() {
		if (nextChar()) {
			if (ch == '*') {
				skippedText.append(ch);
				// Check for Javadoc comment
				if (nextChar() && ch != '*')
					putBackChar();  // so don't save as skippedText
				else
					skippedText.append(ch);
				skipComment(SLASH_STAR);
			} else if (ch == '/') {
				skippedText.append(ch);
				skipComment(SLASH_SLASH);
			}
			else if (ch != '\n')
				putBackChar();
		}
	}

	public static final int SLASH_SLASH = 0;
	public static final int SLASH_STAR = 1;

	private PushbackReader in; // The input stream
	private char ch; // Current character
	private int currentLine; // Current line
	private int errors; // Number of errors seen
	private StringBuilder skippedText = new StringBuilder("");
}
