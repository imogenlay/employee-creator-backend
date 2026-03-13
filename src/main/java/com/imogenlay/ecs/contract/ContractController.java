package com.imogenlay.ecs.contract;

import com.imogenlay.ecs.common.ConditionalObject;
import com.imogenlay.ecs.common.SortOrder;
import com.imogenlay.ecs.contract.dtos.ContractResponse;
import com.imogenlay.ecs.contract.dtos.CreateContractDto;
import com.imogenlay.ecs.employee.dtos.EmployeeQueryParams;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contracts")
public class ContractController
{
	private final ContractService contractService;

	public ContractController(ContractService contractService)
	{
		this.contractService = contractService;
	}

	@GetMapping
	public ResponseEntity<List<ContractResponse>> findAll(
			@RequestParam(defaultValue = "DESC") SortOrder order,
			@RequestParam(defaultValue = "name") String sortBy)
	{
		EmployeeQueryParams params = new EmployeeQueryParams(order);

		Sort sort = Sort.by(
				params.orderOrDefault() == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC,
				sortBy
		);

		return ResponseEntity.status(HttpStatus.OK).body(contractService.findAll(sort));
	}

	@PostMapping()
	public ResponseEntity<ContractResponse> create(@Valid @RequestBody CreateContractDto data)
	{
		ConditionalObject<ContractResponse> result = this.contractService.create(data);
		if (result.hasError())
			result.throwError();

		return ResponseEntity.status(HttpStatus.CREATED).body(result.getObject());
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ContractResponse> update(
			@PathVariable Long id,
			@Valid @RequestBody CreateContractDto data)
	{
		ConditionalObject<ContractResponse> result = this.contractService.update(id, data);
		if (result.hasError())
			result.throwError();

		return ResponseEntity.status(HttpStatus.OK).body(result.getObject());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id)
	{
		// TODO: Make sure that employees get a new contract if their contract is deleted.
		ConditionalObject<ContractResponse> result = this.contractService.delete(id);
		if (result.hasError())
			result.throwError();

		return ResponseEntity.noContent().build();
	}
}
