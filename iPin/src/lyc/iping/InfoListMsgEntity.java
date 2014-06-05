package lyc.iping;

public class InfoListMsgEntity {
	private String ID;
	private String HeadImageVersion;
	private String username;
	private String date;
	private String from;
	private String to;
	private String memberCount;
	
	
	public InfoListMsgEntity() {
		super();
	}

	public InfoListMsgEntity(String iD, String headImageVersion,
			String username, String date, String from, String to) {
		super();
		ID = iD;
		HeadImageVersion = headImageVersion;
		this.username = username;
		this.date = date;
		this.from = from;
		this.to = to;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(String memberCount) {
		this.memberCount = memberCount;
	}
	
	
	
	

}
