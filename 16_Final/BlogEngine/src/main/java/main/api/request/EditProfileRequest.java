package main.api.request;

public class EditProfileRequest implements RequestApi {

    private Byte removePhoto;
    private String name;
    private String email;
    private String password;
//    private String captcha;
//    private String captcha_secret;

    public EditProfileRequest() {
    }

    public EditProfileRequest(Byte removePhoto, String name, String email, String password
//            ,String captcha, String captcha_secret
    ) {
        this.removePhoto = removePhoto;
        this.name = name;
        this.email = email;
        this.password = password;
//        this.captcha = captcha;
//        this.captcha_secret = captcha_secret;
    }

//    public String getCaptcha_secret() {
//        return captcha_secret;
//    }

    public Byte getRemovePhoto() {
        return removePhoto;
    }

    public void setRemovePhoto(Byte removePhoto) {
        this.removePhoto = removePhoto;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public String getCaptcha() {
//        return captcha;
//    }
//
//    public void setCaptcha(String captcha) {
//        this.captcha = captcha;
//    }
//
//    public String getCaptchaSecret() {
//        return captcha_secret;
//    }
//
//    public void setCaptcha_secret(String captcha_secret) {
//        this.captcha_secret = captcha_secret;
//    }
}
