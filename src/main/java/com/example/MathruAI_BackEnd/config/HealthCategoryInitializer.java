package com.example.MathruAI_BackEnd.config;

import com.example.MathruAI_BackEnd.entity.healthrecords.HealthCategory;
import com.example.MathruAI_BackEnd.repository.healthrecords.HealthCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HealthCategoryInitializer implements CommandLineRunner {

    private final HealthCategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            List<HealthCategory> categories = Arrays.asList(
                    HealthCategory.builder().name("Medical Checkups").slug("medical-checkups").icon("🩺")
                            .colorClass("bg-blue-100").build(),
                    HealthCategory.builder().name("Lab Test Results").slug("lab-results").icon("🔬")
                            .colorClass("bg-red-100").build(),
                    HealthCategory.builder().name("Ultrasound & Scans").slug("scans").icon("📝")
                            .colorClass("bg-purple-100").build(),
                    HealthCategory.builder().name("Medications & Supplements").slug("medications").icon("💊")
                            .colorClass("bg-pink-100").build(),
                    HealthCategory.builder().name("Vaccinations").slug("vaccinations").icon("💉")
                            .colorClass("bg-green-100").build(),
                    HealthCategory.builder().name("Personal Health Notes").slug("personal-notes").icon("📝")
                            .colorClass("bg-yellow-100").build(),
                    HealthCategory.builder().name("Others").slug("others").icon("📋").colorClass("bg-gray-100")
                            .build());
            categoryRepository.saveAll(categories);
        }
    }
}
