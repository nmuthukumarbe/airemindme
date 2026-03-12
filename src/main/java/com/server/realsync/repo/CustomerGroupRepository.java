/**
 * 
 */
package com.server.realsync.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.server.realsync.entity.CustomerGroup;

/**
 * 
 */

public interface CustomerGroupRepository extends JpaRepository<CustomerGroup, Integer> {

    List<CustomerGroup> findByAccountId(Integer accountId);

    Optional<CustomerGroup> findByAccountIdAndName(Integer accountId, String name);

}