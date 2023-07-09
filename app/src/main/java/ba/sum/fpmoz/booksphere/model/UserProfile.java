package ba.sum.fpmoz.booksphere.model;

public class UserProfile {
    private String fullNameTxt;
    private String email;
    private String phone;
    private String image;

    public UserProfile() {

    }

    public UserProfile(String fullNameTxt, String email, String phone, String image) {
        this.fullNameTxt = fullNameTxt;
        this.email = email;
        this.phone = phone;
        this.image = image;

    }

    public String getFullNameTxt() {
        return fullNameTxt;
    }

    public void setFullNameTxt(String fullNameTxt) {
        this.fullNameTxt = fullNameTxt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {return phone;}

    public void setPhone(String phone) {this.phone = phone;}

    public String getImage() {return image;}

    public void setImage(String image) {this.image = image;}


}
