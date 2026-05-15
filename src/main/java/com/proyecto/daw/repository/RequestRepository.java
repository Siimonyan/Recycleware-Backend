package com.proyecto.daw.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.daw.model.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    
      
    List<Request> findByApplicantId(int applicantId);
    List<Request> findByProductId(int productId);
    long countByStateName(String stateName);
}