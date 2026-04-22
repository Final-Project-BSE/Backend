package com.example.MathruAI_BackEnd.service.interservice.connection;

import java.util.List;

public interface LocationService {
    List<String> getAllDistricts();
    List<String> getMohAreasByDistrict(String district);
}