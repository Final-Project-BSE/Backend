package com.example.MathruAI_BackEnd.service.impl.reproductive.healthmonitoring;

import com.example.MathruAI_BackEnd.dto.reproductiveDto.healthmonitoring.HealthDataDto;
import com.example.MathruAI_BackEnd.entity.reproductive.healthmonitoring.HealthData;
import com.example.MathruAI_BackEnd.repository.reproductive.HealthMonitoring.HealthDataRepository;
import com.example.MathruAI_BackEnd.service.interservice.healthmonitoring.HealthDataServiceInter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HealthDataServiceImpl implements HealthDataServiceInter {

    private final HealthDataRepository healthDataRepository;

    @Autowired
    public HealthDataServiceImpl(HealthDataRepository healthDataRepository){
        this.healthDataRepository = healthDataRepository;
    }

    public List<HealthDataDto> getAllHealthData(){
        List<HealthData> healthData = healthDataRepository.findAll();
        List<HealthDataDto> arrdata = new ArrayList<>();
        for(HealthData data : healthData){
            HealthDataDto health = new HealthDataDto();
            health.setAge(data.getAge());
            health.setSystolicBP(data.getSystolicBP());
            health.setDiastolicBP(data.getDiastolicBP());
            health.setBloodSugar(data.getBloodSugar());
            health.setBodyTemp(data.getBodyTemp());
            health.setHeartRate(data.getHeartRate());
            health.setWeight(data.getWeight());
            health.setHeight(data.getHeight());
            health.setBmi(data.getBmi());
            health.setPreviousComplications(data.getPreviousComplications());
            health.setPreexistingDiabetes(data.getPreexistingDiabetes());
            health.setGestationalDiabetes(data.getGestationalDiabetes());
            health.setMentalHealth(data.getMentalHealth());

            arrdata.add(health);
        }

        return arrdata;

    }
    public HealthDataDto getHealthDataById(int id){
        HealthData data = healthDataRepository.findById(id).get();
        if(data != null){
            return new HealthDataDto(data.getId(), data.getAge(), data.getSystolicBP(), data.getDiastolicBP(), data.getBloodSugar(), data.getBodyTemp(), data.getHeartRate(), data.getWeight(), data.getHeight(), data.getBmi(), data.getPreviousComplications(), data.getPreexistingDiabetes(), data.getGestationalDiabetes(), data.getMentalHealth());
        }else{
            return null;
        }
    }
    public HealthDataDto createHealthData(HealthDataDto healthDataDto){
        HealthData data = new HealthData();
        data.setAge(healthDataDto.getAge());
        data.setSystolicBP(healthDataDto.getSystolicBP());
        data.setDiastolicBP(healthDataDto.getDiastolicBP());
        data.setBloodSugar(healthDataDto.getBloodSugar());
        data.setBodyTemp(healthDataDto.getBodyTemp());
        data.setHeartRate(healthDataDto.getHeartRate());
        data.setWeight(healthDataDto.getWeight());
        data.setHeight(healthDataDto.getHeight());
        data.setBmi(healthDataDto.getBmi());
        data.setPreviousComplications(healthDataDto.getPreviousComplications());
        data.setPreexistingDiabetes(healthDataDto.getPreexistingDiabetes());
        data.setGestationalDiabetes(healthDataDto.getGestationalDiabetes());
        data.setMentalHealth(healthDataDto.getMentalHealth());

        HealthData healthdata = healthDataRepository.save(data);

        return new HealthDataDto(healthdata.getId(),healthdata.getAge(), healthdata.getSystolicBP(), healthdata.getDiastolicBP(), healthdata.getBloodSugar(),
                healthdata.getBodyTemp(), healthdata.getHeartRate(), healthdata.getWeight(), healthdata.getHeight(),
                healthdata.getBmi(), healthdata.getPreviousComplications(), healthdata.getPreexistingDiabetes(),
                healthdata.getGestationalDiabetes(), healthdata.getMentalHealth()
        );
    }

    public HealthDataDto updateHealthData(int id, HealthDataDto healthDataDto){
        HealthData databyid = healthDataRepository.findById(id).get();

        databyid.setAge(healthDataDto.getAge());
        databyid.setSystolicBP(healthDataDto.getSystolicBP());
        databyid.setDiastolicBP(healthDataDto.getDiastolicBP());
        databyid.setBloodSugar(healthDataDto.getBloodSugar());
        databyid.setBodyTemp(healthDataDto.getBodyTemp());
        databyid.setHeartRate(healthDataDto.getHeartRate());
        databyid.setWeight(healthDataDto.getWeight());
        databyid.setHeight(healthDataDto.getHeight());
        databyid.setBmi(healthDataDto.getBmi());
        databyid.setPreviousComplications(healthDataDto.getPreviousComplications());
        databyid.setPreexistingDiabetes(healthDataDto.getPreexistingDiabetes());
        databyid.setGestationalDiabetes(healthDataDto.getGestationalDiabetes());
        databyid.setMentalHealth(healthDataDto.getMentalHealth());

        HealthData data = healthDataRepository.save(databyid);

        return new HealthDataDto(data.getId(), data.getAge(), data.getSystolicBP(), data.getDiastolicBP(), data.getBloodSugar(), data.getBodyTemp(), data.getHeartRate(), data.getWeight(), data.getHeight(), data.getBmi(), data.getPreviousComplications(), data.getPreexistingDiabetes(), data.getGestationalDiabetes(), data.getMentalHealth());

    }
    public void deleteHealthData(int id){
        HealthData data = healthDataRepository.findById(id).get();
        healthDataRepository.delete(data);
    }

}
