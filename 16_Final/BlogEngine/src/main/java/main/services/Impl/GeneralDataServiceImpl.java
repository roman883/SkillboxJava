package main.services.Impl;

import main.api.response.ResponseApi;
import main.api.response.ResponseGeneralData;
import main.services.interfaces.GeneralDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GeneralDataServiceImpl implements GeneralDataService {

    @Value("${general_data.title}")
    private String title;
    @Value("${general_data.subtitle}")
    private String subtitle;
    @Value("${general_data.phone}")
    private String phone;
    @Value("${general_data.email}")
    private String email;
    @Value("${general_data.copyright}")
    private String copyright;
    @Value("${general_data.copyright_from}")
    private String copyrightFrom;

    @Override
    public ResponseEntity<ResponseApi> getData() {
        return new ResponseEntity<>(new ResponseGeneralData(title, subtitle, phone, email, copyright, copyrightFrom),
                HttpStatus.OK);
    }
}