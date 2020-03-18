package main.api.request;

import java.io.File;

public class EditProfileRequest implements RequestApi {

    private File photo;
    private Byte removePhoto;
    private String name;
    private String email;
    private String password;

    public EditProfileRequest(File photo, Byte removePhoto, String name, String email, String password) {
        this.photo = photo;
        this.removePhoto = removePhoto;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public EditProfileRequest() {
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

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
}
