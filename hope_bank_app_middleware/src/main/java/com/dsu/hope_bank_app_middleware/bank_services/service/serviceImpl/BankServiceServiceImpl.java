package com.dsu.hope_bank_app_middleware.bank_services.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.bank_services.entity.BankService;
import com.dsu.hope_bank_app_middleware.general_enumerations.ResponseType;
import com.dsu.hope_bank_app_middleware.exception.ResourceNotFoundException;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.bank_services.repository.BankServiceRepository;
import com.dsu.hope_bank_app_middleware.bank_services.request.BankServiceRequest;
import com.dsu.hope_bank_app_middleware.bank_services.response.BankServiceFullResponse;
import com.dsu.hope_bank_app_middleware.bank_services.response.BankServiceResponse;
import com.dsu.hope_bank_app_middleware.bank_services.service.BankServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankServiceServiceImpl implements BankServiceService {

    @Autowired
    private BankServiceRepository bankServiceRepository;

    @Override
    public ResponseEntity<BankServiceResponse> createBankService(BankServiceRequest request) {
        BankService bankService = new BankService();
        bankService.setServiceName(request.getServiceName());
        bankService.setServiceIcon(request.getServiceIcon());
        bankService.setDateAdded(new Date());

        BankService savedBankService = bankServiceRepository.save(bankService);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Bank service created successfully")
                .status(HttpStatus.OK)
                .build();


        BankServiceResponse bankServiceResponse = mapToResponse(savedBankService);
        bankServiceResponse.setSuccessResponse(successResponse);


        return new ResponseEntity<>(bankServiceResponse, HttpStatus.CREATED);

    }

    @Override
    public ResponseEntity<BankServiceResponse> getBankServiceById(String id) {
        BankService bankService = bankServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BankService not found"));

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("BankService retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        BankServiceResponse bankServiceResponse = mapToResponse(bankService);
        bankServiceResponse.setSuccessResponse(successResponse);
        return new ResponseEntity<>(bankServiceResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BankServiceFullResponse> getAllBankServices(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<BankService> bankServices = bankServiceRepository.findAll(pageable);

        List<BankService> listOfBankServices = bankServices.getContent();

        List<BankServiceResponse> content =  listOfBankServices.stream().map(bankService -> mapToResponse(bankService)).collect(Collectors.toList());

        BankServiceFullResponse bankServiceFullResponse = new BankServiceFullResponse();
        bankServiceFullResponse.setContent(content);
        bankServiceFullResponse.setPageNo(bankServices.getNumber());
        bankServiceFullResponse.setPageSize(bankServices.getSize());
        bankServiceFullResponse.setTotalElements(bankServices.getTotalElements());
        bankServiceFullResponse.setTotalPages(bankServices.getTotalPages());

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Retrieved bankServices successfully")
                .status(HttpStatus.OK)
                .build();

        bankServiceFullResponse.setSuccessResponse(successResponse);
        return new ResponseEntity<>(bankServiceFullResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BankServiceResponse> updateBankService(String id, BankServiceRequest request) {
        BankService bankService = bankServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BankService not found"));

        bankService.setServiceName(request.getServiceName());
        bankService.setServiceIcon(request.getServiceIcon());
        bankService.setDateUpdate(new Date());

        BankService updatedBankService = bankServiceRepository.save(bankService);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("BankService updated successfully")
                .status(HttpStatus.OK)
                .build();

        BankServiceResponse bankServiceResponse = mapToResponse(updatedBankService);
        bankServiceResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(bankServiceResponse, HttpStatus.OK);
    }

    private BankServiceResponse mapToResponse(BankService bankService) {
        return BankServiceResponse.builder()
                .Id(bankService.getId())
                .serviceName(bankService.getServiceName())
                .serviceIcon(bankService.getServiceIcon())
                .build();
    }
}
