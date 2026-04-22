package com.example.MathruAI_BackEnd.repository;

import com.example.MathruAI_BackEnd.entity.Role;
import com.example.MathruAI_BackEnd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNationalIdNumber(String nationalIdNumber);

    @Query("""
            select distinct u
            from User u
            join u.roles r
            where lower(u.area) = lower(:area)
              and r in :roles
            """)
    List<User> findByAreaAndAnyRole(String area, Collection<Role> roles);

    @Query("""
            select distinct u
            from User u
            join u.roles r
            where u.assignedMidwife.id = :midwifeId
              and r in :roles
            """)
    List<User> findAssignedUsersForMidwife(Long midwifeId, Collection<Role> roles);

    @Query("""
            select distinct u
            from User u
            join u.roles r
            where lower(u.district) = lower(:district)
              and lower(u.mohArea) = lower(:mohArea)
              and r in :roles
            order by u.firstName asc, u.lastName asc
            """)
    List<User> findByDistrictAndMohAreaAndAnyRole(
            String district,
            String mohArea,
            Collection<Role> roles
    );

    @Query("""
            select distinct u
            from User u
            join u.roles r
            where lower(u.district) = lower(:district)
              and lower(u.mohArea) = lower(:mohArea)
              and u.latitude is not null
              and u.longitude is not null
              and r in :roles
            order by u.firstName asc, u.lastName asc
            """)
    List<User> findMappableUsersByDistrictAndMohAreaAndAnyRole(
            String district,
            String mohArea,
            Collection<Role> roles
    );

    @Query("""
            select distinct u.district
            from User u
            where u.district is not null
              and trim(u.district) <> ''
            order by u.district asc
            """)
    List<String> findDistinctDistricts();

    @Query("""
            select distinct u.mohArea
            from User u
            where u.district is not null
              and lower(u.district) = lower(:district)
              and u.mohArea is not null
              and trim(u.mohArea) <> ''
            order by u.mohArea asc
            """)
    List<String> findDistinctMohAreasByDistrict(@Param("district") String district);
}