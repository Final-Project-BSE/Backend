package com.example.MathruAI_BackEnd.service.impl.connection;

import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.service.interservice.connection.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    private final UserRepository userRepository;

    @Override
    public List<String> getAllDistricts() {
        return userRepository.findDistinctDistricts()
                .stream()
                .map(this::normalize)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .toList();
    }

    @Override
    public List<String> getMohAreasByDistrict(String district) {
        if (district == null || district.isBlank()) {
            throw new RuntimeException("District is required.");
        }

        return userRepository.findDistinctMohAreasByDistrict(district.trim())
                .stream()
                .map(this::normalize)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .toList();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}