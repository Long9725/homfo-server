package com.homfo.user.repository;

import com.homfo.user.entity.MySqlUserMarketingAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMarketingAgreementRepository extends JpaRepository<MySqlUserMarketingAgreement, Long> {
    List<MySqlUserMarketingAgreement> findByUserId(Long userId);
}