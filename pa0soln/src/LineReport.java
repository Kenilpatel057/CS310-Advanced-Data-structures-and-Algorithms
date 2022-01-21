
import java.util.Scanner;

public class LineReport {
	public static final int NLINES = 500;
	// Create an array of 500 LineUsage objects
	// since there are NLINES = 500 individual lines,
	// numbered 1 to 500, so use array size NLINES+1
	LineUsage[] lines = new LineUsage[NLINES + 1];

	public LineReport() {
		for (int i = 1; i <= NLINES; i++)
			lines[i] = new LineUsage();
	}

	// read input data, put facts into lines array
	void loadData() {
		// Using String.split(regex) to break up a line into tokens
		// Easy/good-enough way for this assignment: split(" "); 
		// For general whitespace delimiters (tabs, etc.), use split("\\s+")
		// We'll cover this regex expression later
		
		Scanner in = new Scanner(System.in);
		String line, user;
		int lineNumber;
		// For reading a text data file, with one record on each line,
		// it's best to read the lines separately and then analyze them:
		while (in.hasNextLine()) {
			line = in.nextLine();
				String[] tokens = line.split(" ");
			if (tokens.length < 2) {
				System.err.println("Error in data format, line = " + line + " , continuing");
			} else {
				// System.out.println("tokens: " + tokens[0] + "  " + tokens[1]);
				lineNumber = Integer.parseInt(tokens[0]);
				user = tokens[1];
				// Place data in proper LineUsageData element of array.
				lines[lineNumber].addObservation(user);
			}
		}
		in.close();
	}

	// given loaded lines array, generate report on lines
	void generateReport() {
		// All done reading data, now print out records.
		System.out.println("Line\tMost Common User\tCount");
		// Loop through array and print out the most common user
		// and number of logins.
		for (int i = 1; i <= NLINES; i++) {
			Usage record = lines[i].findMaxUsage();
			System.out.println(i + "\t" + record.getUser() + "\t" + record.getCount());
		}
	}

	public static void main(String[] args) {
		LineReport report = new LineReport();
		report.loadData();
		report.generateReport();
	}

}
