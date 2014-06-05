
package lyc.iping;

public class ChatMsgEntity {
    private String name;

    private String date;
    
    private int img;

    private String message;
    
    private String ID;
    private String HeadImageVersion;
    private int index;
    private boolean isComMeg = true;
    private boolean showDate = true;
    public ChatMsgEntity(String name, String date, String text, int img,
			boolean isComMsg) {
		super();
		this.name = name;
		this.date = date;
		this.message = text;
		this.img = img;
		this.isComMeg = isComMsg;
	}

    public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String text) {
        this.message = text;
    }

    public boolean getMsgType() {
        return isComMeg;
    }
    
    public int getImg() {
		return img;
	}
    
    public void setImg(int img) {
		this.img = img;
	}

    public void setMsgType(boolean isComMsg) {
    	isComMeg = isComMsg;
    }

    public ChatMsgEntity() {
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

	public boolean isShowDate() {
		return showDate;
	}

	public void setShowDate(boolean showDate) {
		this.showDate = showDate;
	}

    
}
