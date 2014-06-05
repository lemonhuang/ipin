
package lyc.iping;

public class EditMsgEntity {
    private static final String TAG = EditMsgEntity.class.getSimpleName();
    
    private String UserHead;   

	private String name;
    
    private String time;

//    private String date;
//
//    private String from;
//    
//    private String to;
    
    private String detail;

    private boolean isComMeg = true;
    
    public String getUserHead() {
		return UserHead;
	}

	public void setUserHead(String userHead) {
		UserHead = userHead;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public String getFrom() {
//        return from;
//    }
//
//    public void setFrom(String from) {
//        this.from = from;
//    }
//    
//    public String getTo() {
//        return to;
//    }
//
//    public void setTo(String to) {
//        this.to = to;
//    }
    
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean getMsgType() {
        return isComMeg;
    }

    public void setMsgType(boolean isComMsg) {
    	isComMeg = isComMsg;
    }

    public EditMsgEntity() {
    }

    public EditMsgEntity(String name, String time, String detail, boolean isComMsg) {
        super();
        this.name = name;
        this.time = time;
//        this.date = date;
//        this.from = from;
//        this.to = to;
        this.detail = detail;
        this.isComMeg = isComMsg;
    }

}
