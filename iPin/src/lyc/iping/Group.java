package lyc.iping;

import java.io.Serializable;

public class Group implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String GroupID;
	private String CreatorID;
	private String CreatorUsername;
	private String CreatorHeadImageVersion;
	private String from;
	private String to;
	private String date;
	private String detail;
	private String memberCount;
	private String memberList;
	public String getGroupID() {
		return GroupID;
	}
	public void setGroupID(String groupID) {
		GroupID = groupID;
	}
	public String getCreatorID() {
		return CreatorID;
	}
	public void setCreatorID(String creatorID) {
		CreatorID = creatorID;
	}
	public String getCreatorUsername() {
		return CreatorUsername;
	}
	public void setCreatorUsername(String creatorUsername) {
		CreatorUsername = creatorUsername;
	}
	public String getCreatorHeadImageVersion() {
		return CreatorHeadImageVersion;
	}
	public void setCreatorHeadImageVersion(String creatorHeadImageVersion) {
		CreatorHeadImageVersion = creatorHeadImageVersion;
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getMemberCount() {
		return memberCount;
	}
	public void setMemberCount(String memberCount) {
		this.memberCount = memberCount;
	}
	public String getMemberList() {
		return memberList;
	}
	public void setMemberList(String memberList) {
		this.memberList = memberList;
	}	
	
	

	

}
