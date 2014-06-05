package lyc.iping;

public class MemberEntity {
	
	private String name;

    private String email;
    
    private String phone;

    private String other;

    private boolean isCreator = true;
    
    public MemberEntity(String name, String email, String phone, String other,
			boolean isCreator) {
		super();
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.other = other;
		this.isCreator = isCreator;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public boolean getCreator() {
        return isCreator;
    }
    
    public String getPhone() {
		return phone;
	}
    
    public void setPhone(String phone) {
		this.phone = phone;
	}

    public void setCreator(boolean isCreator) {
    	isCreator = isCreator;
    }

    public MemberEntity() {
    }

}




