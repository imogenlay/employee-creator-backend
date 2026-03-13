package com.imogenlay.ecs.contract;

import com.imogenlay.ecs.contract.dtos.ContractResponse;
import com.imogenlay.ecs.contract.entity.Contract;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ContractAccessHandler
{
	private final ContractRepository contractRepository;

	public ContractAccessHandler(ContractRepository contractRepository)
	{
		this.contractRepository = contractRepository;
	}

	public List<ContractResponse> findAll(Sort sort)
	{
		return contractRepository.findAll(sort).stream().map((c) -> c.createResponse()).toList();
	}

	public Optional<Contract> findById(Long id)
	{
		return contractRepository.findById(id);
	}

	public void saveAndFlush(Contract contract) { contractRepository.saveAndFlush(contract); }

	public void delete(Contract contract) { contractRepository.delete(contract); }


}
