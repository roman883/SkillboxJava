package main.services.interfaces;

import main.model.GeneralData;
import org.springframework.http.ResponseEntity;

public interface GeneralDataService {

    public ResponseEntity<GeneralData> getData();
}