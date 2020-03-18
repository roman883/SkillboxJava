package main.services.Impl;

import main.api.response.*;
import main.api.response.GeneralData;
import main.api.response.ResponseApi;
import main.services.interfaces.GeneralDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GeneralDataServiceImpl implements GeneralDataService {
    @Override
    public ResponseEntity<ResponseApi> getData() {
        return new ResponseEntity<>(GeneralData.getInstance(), HttpStatus.OK);
    }
}