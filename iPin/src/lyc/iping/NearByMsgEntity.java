package lyc.iping;

public class NearByMsgEntity {
	private String ID;
	private String HeadImageVersion;
	private String username;
	private String distance;
	private String telnum;
	private String destination;
	
	
	public NearByMsgEntity() {
		super();
	}

	public NearByMsgEntity(String iD, String headImageVersion,
			String username) {
		super();
		ID = iD;
		HeadImageVersion = headImageVersion;
		this.username = username;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getHeadImageVersion() {
		return HeadImageVersion;
	}

	public void setHeadImageVersion(String headImageVersion) {
		HeadImageVersion = headImageVersion;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	public String getTelnum() {
		return telnum;
	}

	public void setTelnum(String telnum) {
		this.telnum = telnum;
	}
	
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
}
