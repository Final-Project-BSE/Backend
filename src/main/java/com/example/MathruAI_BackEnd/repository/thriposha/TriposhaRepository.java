
package com.example.MathruAI_BackEnd.repository.thriposha;

import com.example.MathruAI_BackEnd.entity.triposha.TriposhaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface TriposhaRepository extends JpaRepository<TriposhaRecord, Long> {

    List<TriposhaRecord> findByPatientIdOrderByDistributionDateDesc(Long patientId);

    List<TriposhaRecord> findByMidwifeId(Long midwifeId);
}

