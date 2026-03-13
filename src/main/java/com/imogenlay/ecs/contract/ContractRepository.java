package com.imogenlay.ecs.contract;

import com.imogenlay.ecs.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long>
{
}
