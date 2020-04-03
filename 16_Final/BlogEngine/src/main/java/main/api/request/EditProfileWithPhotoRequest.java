package main.api.request;

import org.springframework.web.multipart.MultipartFile;

public class EditProfileWithPhotoRequest extends EditProfileRequest {

    private MultipartFile photo;

    public EditProfileWithPhotoRequest(Byte removePhoto, String name, String email, String password
//            ,String captcha, String captcha_secret
            , MultipartFile photo) {
        super(removePhoto, name, email, password
//                , captcha, captcha_secret
        );
        this.photo = photo;
    }

    public EditProfileWithPhotoRequest() {
    }

    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }

    public EditProfileWithPhotoRequest(MultipartFile photo) {
        this.photo = photo;
    }
}
