
// For cs310 pa4 Boston metro graph 
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.princeton.cs.algs4.*;

public class MetroSystem {
	// The platforms, one for each vertex, i.e. app info for that vertex
	private Platform[] platforms = null;
	private int nPlatforms;
	private Station[] stations = null;
	private TreeMap<String, Station> stationMap = new TreeMap<String, Station>();
	private Set<SimpleEdge> neighbors = new HashSet<SimpleEdge>();
	private Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();
	// The graph, made available by getGraph()
	private Graph stationGraph;
	private Digraph platformGraph;

	public StationGraph getStationGraph() {
		return new StationGraph(stationGraph, stationMap);
	}

	public PlatformGraph getPlatformGraph() {
		return new PlatformGraph(platformGraph, platforms);
	}

	// provide app info on this vertex via a Station object
	private Station stationOf(int id) {
		if (id > 0 && id < stationGraph.V()) {
			return stations[id];
		} else
			return null;
	}

	// private to MetroSystem for its use
	// clients should call PlatformGraph.platformOf, since Platforms
	// are the nodes of the PlatformGraph
	private Platform platformOf(int id) {
		if (id > 1 && id < platforms.length) {
			return platforms[id];
		} else
			return null;
	}

	// In Platform graph, find platform for opposite direction (same trainline) at a
	// station
	// Each pair of platforms are given ids e and e+1 where e is an even number.
	// Thus the last bit says which side the platform is on.
	private int oppositePlatformOf(int id) {
		int platformDir = id & 1; // last bit gives side, 0 or 1
		int oppPlatformId;
		if (platformDir == 1)
			oppPlatformId = id - 1; // clear last bit (it was on)
		else
			oppPlatformId = id + 1; // set last bit (it was off)
		return oppPlatformId;
	}

	private int countLines(String filePath) {
		int count = 0; // one for spot 0, not used
		try {
			Scanner in = null;
			in = new Scanner(new File(filePath));
			while (in.hasNextLine()) {
				count++;
				in.nextLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + filePath);
			return 0;
		}
		return count;
	}

	public MetroSystem(String filePath) {
		int nV = countLines(filePath) + 1; // upper bound, redo later
		// stationGraph = new Graph(nV);
		platforms = new Platform[2 * nV];
		stations = new Station[nV];
		// handle file-not-found here, for convenience of caller
		// (like SymbolGraph in S&W)
		try {
			findNeighbors(filePath); // including fake vertex 0
			fillGraph(); // use neighbor info to create graph
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + filePath);
			return;
		}
		for (String s : stationMap.keySet()) {
			Station station = stationMap.get(s);
			stations[station.getStationId()] = station;
		}
		System.out.println("orig graph #edges =" + stationGraph.E());
		deDupStationGraph();
		System.out.println("after dedup, graph #edges =" + stationGraph.E());

		// platformGraph = digraphFromGraph(stationGraph);
		printStationNeighbors("Assembly");
		printStationNeighbors("Courthouse");
		platformGraph = new Digraph(nPlatforms);
		fillPlatformGraph();
	}

	// Helper to constructor: fill out platform array
	// while finding neighboring stations from file data
	// The stations are known temporarily by their "fileIds",
	// the first number on the input line. Many stations have
	// multiple fileIds because they show up on different trainlines
	// And a fileId may show up as a neighbor before it is known by stationName
	// But eventually all the fileIds are mapped to unique station ids.
	// by idMap, and all the stations are in stationMap by stationName
	private void findNeighbors(String filePath) throws FileNotFoundException {
		Scanner in = null;
		in = new Scanner(new File(filePath));
		// graph can't have vertex 0, so start from 2, using 2 vertices per input line
		// of file
		// placeholder for spot vertex 0
		platforms[0] = new Platform(null, "fake vertex", 0);
		platforms[1] = new Platform(null, "fake vertex", 0);
		stations[0] = new Station("fake vertex", 0, 0, 0);
		int platformId = 0; // soon inc'd to 2
		int stationId = 0; // soon inc'd to 1
		while (in.hasNextLine()) {
			platformId += 2; // start from 2, use 2 Platforms per input line, one for ea direction
			double lat = 0.0, lon = 0.0;
			String line1 = in.nextLine();
			String[] tokens = line1.split(",");
			int thisFileId = Integer.parseInt(tokens[0]);
			int thisPlatformId = platformId;
			// System.out.println("doing station " + tokens[1] + " id " + thisId);
			assert thisFileId == thisPlatformId / 2 : "bad line Id: expect ids in order in file";
			if (tokens.length > 5 && !tokens[5].isEmpty())
				lat = Double.parseDouble(tokens[5]);
			if (tokens.length > 6 && !tokens[6].isEmpty())
				lon = Double.parseDouble(tokens[6]);
			String stationName = tokens[1].trim();
			String trainLine = tokens[2];
			Station station = stationMap.get(stationName);
			int thisStationId = -1;
			if (station == null) {
				stationId++;
				thisStationId = stationId;
				station = new Station(stationName, lat, lon, stationId);
				stationMap.put(stationName, station);
			} else {
				thisStationId = station.getStationId();
			}

			idMap.put(thisFileId, thisStationId);
			// System.out.println(" see idMap(135) = "+ idMap.get(135));
			station.addTrainLine(trainLine);
			platforms[thisPlatformId] = new Platform(station, trainLine, thisPlatformId);
			platforms[thisPlatformId + 1] = new Platform(station, trainLine, thisPlatformId + 1);
			int neighborFileId1 = Integer.parseInt(tokens[3]);
			int neighborFileId2 = Integer.parseInt(tokens[4]);
			if (neighborFileId1 > 0) {
				SimpleEdge e1 = new SimpleEdge(thisFileId, neighborFileId1);
				neighbors.add(e1);
			}
			if (neighborFileId2 > 0) {
				SimpleEdge e2 = new SimpleEdge(thisFileId, neighborFileId2);
				neighbors.add(e2);
			}
		}
		nPlatforms = platformId + 2;
		in.close();
	}

	private void fillGraph() {
		// now have stationMap in full, so know how many vertices we have
		// and idMap, telling us what station various fileIds are for
		stationGraph = new Graph(stationMap.size() + 1); // leaving vertex 0 unused
		for (SimpleEdge e : neighbors) {
			int stationId1 = idMap.get(e.x); // look up real id of stations
			int stationId2 = idMap.get(e.y);
			if (stationId1 == stationId2) {
				System.out.println("bad edge id-id " + stationId1 + " " + stationId2);
			}
			stationGraph.addEdge(stationId1, stationId2);
		}
	}

	// de-duplicate Graph edges by using a Set of undirected edges
	// Rebuild G using dedup'd edges
	private void deDupStationGraph() {
		Set<SimpleEdge> edges = new HashSet<SimpleEdge>();
		int count = 0;
		for (int i = 1; i < stationGraph.V(); i++) {
			if (stationGraph.adj(i).iterator().hasNext()) {
				count++; // count real vertices
				for (int j : stationGraph.adj(i)) {
					if (i != j) {
						edges.add(new SimpleEdge(i, j));
					} else {
						System.out.println("Unexpected i<->i edge, i = " + i + " " + stations[i].getStationName());
					}
				}
			}
		}
		stationGraph = new Graph(count + 1); // recreate it, smaller
		for (SimpleEdge e : edges) {
			stationGraph.addEdge(e.x, e.y);
		}
	}

	// Add platforms to platformGrahp by following down each line
	// given starting station
	// Use platform 0 for inbound track on primary lines Red, Green, Blue, Orange
	// and Silver at their starting station
	// Use platform 1 for inbound track on split lines so that they meet up
	// properly in the middle with their primary
	// and also for Mattapan so it mates with RedA
	private void fillPlatformGraph() {
		followTrainLine("Red", 0, "Alewife");
		followTrainLine("RedA", 1, "Ashmont");
		followTrainLine("RedB", 1, "Braintree");
		followTrainLine("Mattapan", 1, "Mattapan");
		followTrainLine("Green", 0, "Lechmere");
		followTrainLine("GreenB", 1, "BostonCollege");
		followTrainLine("GreenC", 1, "ClevelandCircle");
		followTrainLine("GreenD", 1, "Riverside");
		followTrainLine("GreenE", 1, "HeathStreet");
		followTrainLine("Orange", 0, "OakGrove");
		followTrainLine("Blue", 0, "Wonderland");
		followTrainLine("Silver", 0, "Chelsea");
		connectStationPlatforms();
		// dedup--
		deDupPlatformGraph();
	}
	
	private Set<String> trainLineColors(Station s) {
		Set<String> trainLines = s.getTrainLines();
		Set<String> trainLineColors = new HashSet<String>();
		for (String tl : trainLines) {
			trainLineColors.add(tl.substring(0, 3));
		}
		return trainLineColors;
	}

	// Go through stations on a trainline, starting from the end station that is
	// specified by endStationName, filling in the Platform graph
	// inboundSide = 0 or 1, which of two platforms is the inbound one at the end
	// station
	private void followTrainLine(String trainLine, int inboundSide, String endStationName) {
	//	System.out.println("followTrainLine for " + trainLine + " from station " + endStationName);
		int endPlatformId = -1;
		Station endStation = stationMap.get(endStationName);
		for (int i = 2; i < nPlatforms; i++) {
			if (platforms[i].getStation().getStationName().equals(endStation.getStationName())
					&& platforms[i].getTrainLine().equals(trainLine)) {
				endPlatformId = i;
				break;
			}
		}
		int thisPlatformId = endPlatformId;
		int thisStationId = endStation.getStationId();
		List<String> foundStations = new ArrayList<String>();
		foundStations.add(endStation.getStationName());

		while (true) {
			// find next station
			int prevPlatformId = thisPlatformId;
			boolean found = false;
			boolean match = false;
			for (int i : stationGraph.adj(thisStationId)) {
				Set<String> trainLineColors = trainLineColors(stationOf(i));
				if (trainLineColors.contains(trainLine.substring(0, 3))) {
					String foundStationName = stationOf(i).getStationName();
					// System.out.println("found " + foundStationName);
					if (foundStations.contains(foundStationName)) {
						continue; // don't go backwards along line
					}
					found = true; // trainline colors match
					if (stationOf(i).getTrainLines().contains(trainLine))
						match = true; // trainLine names match as well as colors
					thisStationId = i; // one to use next
					foundStations.add(foundStationName); // prevent backtracking on line
					break; // done with find-station
				}
			}
			if (!found || (inboundSide == 0 && !match)) { // end of line: go on if found && parity == 1 || matched
				break;
			}
			// System.out.println("inboundSide " + inboundSide + " match: " + match);
			// have next station, find right platforms
			for (int i = 2; i < nPlatforms; i++) {
				if (platforms[i].getStation().getStationId() == thisStationId
						&& platforms[i].getTrainLine().substring(0, 3).equals(trainLine.substring(0, 3))) {
					// found platform for next station on this line or at least this-color line
					if (match) { // this edge owned by this line, not just same color
						ckPlatformEdge(thisPlatformId + inboundSide, i + inboundSide);
						platformGraph.addEdge(thisPlatformId + inboundSide, i + inboundSide);
						ckPlatformEdge(oppositePlatformOf(i + inboundSide),
								oppositePlatformOf(thisPlatformId + inboundSide));
						platformGraph.addEdge(oppositePlatformOf(i + inboundSide),
								oppositePlatformOf(thisPlatformId + inboundSide));
					}
					if (match || inboundSide == 1) { // let split lines join up with primary same-color
						ckPlatformEdge(prevPlatformId + inboundSide, i + inboundSide);
						platformGraph.addEdge(prevPlatformId + inboundSide, i + inboundSide);
						ckPlatformEdge(oppositePlatformOf(i + inboundSide),
								oppositePlatformOf(prevPlatformId + inboundSide));
						platformGraph.addEdge(oppositePlatformOf(i + inboundSide),
								oppositePlatformOf(prevPlatformId + inboundSide));
					}
					thisPlatformId = i;
					break; // use first match at each station
				}
			}
			if (!match) // only continue if edge matched desired trainline
				break;
		}
	}

	// connect the vertices for one station all together
	// with edges for (pedestrian) transfers
	private void connectStationPlatforms() {
		for (String s : stationMap.keySet()) {
			// Find all vertices for station
			Set<Integer> ids = new HashSet<Integer>();
			for (int i = 2; i < nPlatforms; i++) {
				// System.out.println("i = " + i);
				if (platforms[i].getStation().getStationName().equals(s))
					ids.add(i);
			}
			if (ids.size() > 1) {
				// case of multiple platforms for a station: link together
				// to represent possible connections for riders to take
				for (int i : ids)
					for (int j : ids)
						if (i != j)
							platformGraph.addEdge(i, j);
			}
		}
	}

	private void deDupPlatformGraph() {
		Set<SimpleDEdge> edges = new HashSet<SimpleDEdge>();
		for (int i = 2; i < nPlatforms; i++) {
			for (int j : platformGraph.adj(i)) {
				SimpleDEdge e = new SimpleDEdge(i, j);
				edges.add(e);
			}
		}
		platformGraph = new Digraph(nPlatforms);
		for (SimpleDEdge e : edges) {
			platformGraph.addEdge(e.x, e.y);
		}
	}

	// named SimpleEdge to be different from Edge of book
	// edge for undirected graph: SimpleEdge(x,y) and SimpleEdge<y,x) are .equals
	private class SimpleEdge {
		int x, y;

		SimpleEdge(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object other) {
			// code on pg. 103, adapted
			if (this == other)
				return true;
			if (other == null)
				return false;
			if (this.getClass() != other.getClass())
				return false;
			SimpleEdge o = (SimpleEdge) other;
			return x == o.x && y == o.y || x == o.y && y == o.x;
		}

		@Override
		public int hashCode() {
			return Integer.valueOf(x).hashCode() & Integer.valueOf(y).hashCode();
		}
	}

	// named SimpleDEdge to be different from DirectedEdge of book
	// edge for directed graph: SimpleEdge(x,y) and SimpleEdge<y,x) are different
	private class SimpleDEdge {
		int x, y;

		SimpleDEdge(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object other) {
			// code on pg. 103, adapted
			if (this == other)
				return true;
			if (other == null)
				return false;
			if (this.getClass() != other.getClass())
				return false;
			SimpleDEdge o = (SimpleDEdge) other;
			return x == o.x && y == o.y;
		}

		@Override
		public int hashCode() {
			return Integer.valueOf(x).hashCode() >> 16 | Integer.valueOf(y).hashCode();
		}

		@Override
		public String toString() {
			return "" + x + "," + y;
		}
	}

	// Check that an edge between platforms x and y would be reasonable.
	// Edges should be between platforms on the same-color trainLine or
	// platforms at the same station, so check if neither is true
	// Note this allow a RedA station to connect directly to a Red station
	// because that's just following a split, no rider transfer needed.
	// The first three characters of a trainLine String give the trainline
	// grouping: Red, Gre, Blu, Ora, Sil i.e. the trainline color
	private void ckPlatformEdge(int x, int y) {
		// check edge: OK to match RedA with Red and GreenC with Green
		// i.e. match on trainLineColor
		// since these connections allow trains to go through, no transfer
		try {
			if ((x & 1) != (y & 1)
					&& !platforms[x].getStation().getStationName().equals(platforms[y].getStation().getStationName())) {
				System.out.println("Data error/bug: edge connects even platform to odd platform at another station");
			}
			if (!platforms[x].getStation().getStationName().equals(platforms[y].getStation().getStationName())
					&& !platforms[x].getTrainLineColor().equals(platforms[y].getTrainLineColor())) {
				System.out.println("Data error/bug: edge connects different stations on different-color lines: "
						+ platforms[x].getStation().getStationName() + " id " + x + " on " + platforms[x].getTrainLine()
						+ " and  " + platforms[y].getStation().getStationName() + " id " + y + " on "
						+ platforms[y].getTrainLine());
			}
		} catch (Exception e) {
			System.out.println("ckPlatformEdge: exception at x, y " + x + "," + y + e);
			System.out.println("stations: " + platforms[x].getStation().getStationName() + ","
					+ platforms[y].getStation().getStationName());
		}
	}


	// Report on how this station is directly connected to other stations in the
	// system using the Station graph
	public void printStationNeighbors(String stationName) {

		Station station = stationMap.get(stationName);
		if (station == null) {
			System.out.println("printStationNeighbors: can't find station " + stationName);
			return;
		}
		System.out.println("printStationNeighbors for " + stationName + ", id " + station.getStationId()
				+ " train lines " + station.getTrainLines());
		for (int i : stationGraph.adj(station.getStationId())) {
			System.out.println("Neighbor station: " + stations[i].getStationName() + "id " + i + ", train lines "
					+ stations[i].getTrainLines());
		}
	}

	// find all train lines in system
	public Set<String> findAllLines() {
		Set<String> lines = new TreeSet<String>();
		for (int i = 2; i < platformGraph.V(); i++) {
			Platform p = platformOf(i);
			lines.add(p.getTrainLine());
		}
		return lines;
	}

	public static void main(String[] args) {
		MetroSystem mS = new MetroSystem(args[0]);
		StationGraph G = mS.getStationGraph();
		System.out.println(G.toString());
		// System.out.println("Graph G has " + G.V() + " vertices, including unused
		// vertex 0");
		Station s = mS.stationOf(1);
		System.out.println("Station 1 is for station " + s.getStationName());
		PlatformGraph pG = mS.getPlatformGraph();
		System.out.println("platfomrm 14: " + pG.platformOf(14));

		pG.printTrainLine("Green");
		// pG.printTrainLine("RedA");
		pG.printTrainLine("GreenD");
		pG.printStationPlatformConnections("JFK/UMass");
	}
}
