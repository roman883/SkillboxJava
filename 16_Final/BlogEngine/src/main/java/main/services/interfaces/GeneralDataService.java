package main.services.interfaces;

import main.model.responses.ResponseAPI;
import org.springframework.http.ResponseEntity;

public interface GeneralDataService {

    ResponseEntity<ResponseAPI> getData();
}