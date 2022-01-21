import java.util.HashMap;
import java.util.Map;

// LineUsageData.java: Handle one line's data, using a LL
public class LineUsage {
	private Map<String, Integer> data;

	public LineUsage() {
		data = new HashMap<String, Integer>();
	}

	// add one sighting of a user on this line
	public void addObservation(String username) {
		Integer count;
		if ((count = data.get(username)) != null) {
			data.put(username, count + 1);
		} else {
			data.put(username, 1);
		}
	}

	// find the user with the most sightings on this line
	public Usage findMaxUsage() {
		int maxCount = 0;
		String maxUser = "NONE";
		// Go through map and find max count
		for (String u: data.keySet()) {
			int count;
			if ((count = data.get(u)) > maxCount) {
				maxCount = count;
				maxUser = u;
			}
		}
		return new Usage(maxUser, maxCount);
	}
}
