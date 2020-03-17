package main.services.Impl;

import main.model.responses.GeneralData;
import main.model.responses.ResponseAPI;
import main.services.interfaces.GeneralDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GeneralDataServiceImpl implements GeneralDataService {
    @Override
    public ResponseEntity<ResponseAPI> getData() {
        return new ResponseEntity<>(GeneralData.getInstance(), HttpStatus.OK);
    }
}