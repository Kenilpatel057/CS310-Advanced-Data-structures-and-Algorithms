package cs310;
import java.io.InputStreamReader;
// Annotate a Java source by surrounding all ids with [] markup
// A second client for JavaTokenizer, cs310 project 1

public class Annotator {
	private JavaTokenizer tok; // tokenizer object

	public Annotator(JavaTokenizer tokenizer) {
		tok = tokenizer;
	}

	public void annotate() {
		String current;
		String skipped = "";

		while ((current = tok.getNextID()) != "") {
			skipped = tok.skippedText();
			System.out.print(skipped);
			System.out.print("[");
			System.out.print(current);
			System.out.print("]");
		}
		System.out.print(skipped);
	}

	public static void main(String[] args) {
		// System.out.println("Starting...");
		Annotator ann = new Annotator(new Tokenizer(new InputStreamReader(System.in)));
		ann.annotate();
	}

}
