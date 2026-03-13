package com.imogenlay.ecs.contract;

import com.imogenlay.ecs.common.ConditionalObject;
import com.imogenlay.ecs.contract.dtos.ContractResponse;
import com.imogenlay.ecs.contract.dtos.CreateContractDto;
import com.imogenlay.ecs.contract.entity.Contract;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractService
{
	private final ContractAccessHandler contractAccessHandler;

	public ContractService(ContractAccessHandler contractAccessHandler)
	{
		this.contractAccessHandler = contractAccessHandler;
	}

	public List<ContractResponse> findAll(Sort sort)
	{
		return contractAccessHandler.findAll(sort);
	}

	public ConditionalObject<Contract> findById(Long id)
	{
		Optional<Contract> result = contractAccessHandler.findById(id);
		if (result.isEmpty())
			return new ConditionalObject<>(HttpStatus.NOT_FOUND, "Contract with ID [" + id + "] does not exist");

		return new ConditionalObject<>(result.get());
	}

	public ConditionalObject<ContractResponse> create(CreateContractDto data)
	{
		Contract contract = new Contract();
		contract.setName(data.name());
		contract.setIsFullTime(data.isFullTime());
		contractAccessHandler.saveAndFlush(contract);
		return new ConditionalObject<>(contract.createResponse());
	}

	public ConditionalObject<ContractResponse> update(Long id, CreateContractDto data)
	{
		ConditionalObject<Contract> result = findById(id);
		if (result.hasError())
			return result.copyError();

		Contract contract = result.getObject();
		contract.setName(data.name());
		contract.setIsFullTime(data.isFullTime());
		contractAccessHandler.saveAndFlush(contract);
		return new ConditionalObject<>(contract.createResponse());
	}

	public ConditionalObject<ContractResponse> delete(Long id)
	{
		ConditionalObject<Contract> result = findById(id);
		if (result.hasError())
			return result.copyError();

		Contract contract = result.getObject();
		contractAccessHandler.delete(contract);
		return new ConditionalObject<>(contract.createResponse());
	}
}
