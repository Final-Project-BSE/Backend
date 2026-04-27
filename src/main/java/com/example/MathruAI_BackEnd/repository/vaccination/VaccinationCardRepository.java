package com.example.MathruAI_BackEnd.repository.vaccination;

import com.example.MathruAI_BackEnd.entity.vaccination.VaccinationCard;
import com.example.MathruAI_BackEnd.entity.vaccination.VaccinationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VaccinationCardRepository extends JpaRepository<VaccinationCard, Long> {

    List<VaccinationCard> findByMidwifeIdOrderByDueDateAsc(Long midwifeId);

    List<VaccinationCard> findByMidwifeIdAndPatientIsNullOrderByDueDateAsc(Long midwifeId);

    List<VaccinationCard> findByMidwifeIdAndPatientIdOrderByDueDateAsc(Long midwifeId, Long patientId);

    long countByMidwifeIdAndPatientIsNull(Long midwifeId);

    long countByMidwifeIdAndPatientIsNullAndDueDate(Long midwifeId, LocalDate dueDate);

    long countByMidwifeIdAndPatientIsNullAndDueDateAfterAndStatus(
            Long midwifeId,
            LocalDate dueDate,
            VaccinationStatus status
    );

    boolean existsByMidwifeIdAndPatientIdAndVaccineNameAndVaccineTypeAndDoseAndDueDate(
            Long midwifeId,
            Long patientId,
            String vaccineName,
            String vaccineType,
            String dose,
            LocalDate dueDate
    );

    @Query("""
            select count(distinct v.patient.id)
            from VaccinationCard v
            where v.midwife.id = :midwifeId
              and v.patient is not null
              and v.status = :status
            """)
    long countDistinctPatientsByMidwifeAndStatus(
            @Param("midwifeId") Long midwifeId,
            @Param("status") VaccinationStatus status
    );

    @Query("""
            select v
            from VaccinationCard v
            where v.midwife.id = :midwifeId
              and v.patient is null
              and v.status = com.example.MathruAI_BackEnd.entity.vaccination.VaccinationStatus.PENDING
              and v.dueDate between :start and :end
            order by v.dueDate asc
            """)
    List<VaccinationCard> findUpcoming(
            @Param("midwifeId") Long midwifeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
            select v.vaccineName, coalesce(v.vaccineType, ''), coalesce(v.dose, ''), count(distinct v.patient.id)
            from VaccinationCard v
            where v.midwife.id = :midwifeId
              and v.patient is not null
              and v.status = com.example.MathruAI_BackEnd.entity.vaccination.VaccinationStatus.PENDING
            group by v.vaccineName, v.vaccineType, v.dose
            order by v.vaccineName asc
            """)
    List<Object[]> countEligiblePatientsByVaccine(@Param("midwifeId") Long midwifeId);

    List<VaccinationCard> findByPatientIdOrderByDueDateAsc(Long patientId);
}