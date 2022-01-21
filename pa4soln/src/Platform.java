// a platform serves one train line in one direction at a metro station

public class Platform {	
	private Station station; // the station this platform belongs to
	private String trainLine;  // trainline for this platform in this station
	private int platformId;  // unique to platform and used in graph as vertex id
								// even id on one side and id + 1 for the other side
	public Platform(Station station, String trainLine, int platformId) {
		this.station = station;
		this.trainLine = trainLine;
		this.platformId = platformId;
	}
	
	public int getPlatformId() {
		return platformId;
	}
	
	public int getPlatformSide() {
		return platformId&1; // last bit of id
	}
	
	public Station getStation() {
		return station;
	}
	public String getTrainLine() {
		return trainLine;
	}
	public String getTrainLineColor() {
		return trainLine.substring(0,3);  // Red, Gre, Blu, Ora, Sil
	}
	@Override
	public String toString() {
		return "Platform at "+ station.getStationName() + " on train line " + trainLine + " index " + platformId;
	}

}
