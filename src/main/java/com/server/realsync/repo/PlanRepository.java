package com.server.realsync.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.realsync.entity.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
}