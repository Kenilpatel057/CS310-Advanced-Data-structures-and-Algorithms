// One user's record on one line: how many times
// this user has been seen on this line
public class Usage {
	private final String username;
	private int count;

	public Usage(String x, int count) {
		username = x;
		this.count = count;
	}

	public void setCount(int x) {
		count = x;
	}

	public String getUser() {
		return username;
	}

	public int getCount() {
		return count;
	}
}
