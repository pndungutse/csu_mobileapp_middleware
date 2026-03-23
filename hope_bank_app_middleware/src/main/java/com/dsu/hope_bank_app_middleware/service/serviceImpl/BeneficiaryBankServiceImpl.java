package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.entity.BeneficiaryBank;
import com.dsu.hope_bank_app_middleware.enumeration.ResponseType;
import com.dsu.hope_bank_app_middleware.enumeration.Status;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.repository.BeneficiaryBankRepository;
import com.dsu.hope_bank_app_middleware.request.BeneficiaryBankRequest;
import com.dsu.hope_bank_app_middleware.response.BeneficiaryBankResponse;
import com.dsu.hope_bank_app_middleware.response.GenericResponse;
import com.dsu.hope_bank_app_middleware.service.BeneficiaryBankService;
import com.dsu.hope_bank_app_middleware.utils.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class BeneficiaryBankServiceImpl implements BeneficiaryBankService {

    @Autowired
    private BeneficiaryBankRepository beneficiaryBankRepository;

    @Autowired
    private Mappings mappings;

    @Override
    public ResponseEntity<BeneficiaryBankResponse> createBeneficiaryBank(BeneficiaryBankRequest request) {
        BeneficiaryBank beneficiaryBank = new BeneficiaryBank();
        beneficiaryBank.setBeneficiaryCode(request.getBeneficiaryCode());
        beneficiaryBank.setBeneficiaryName(request.getBeneficiaryName());
        beneficiaryBank.setBeneficiaryStatus(Status.ACTIVE);
        beneficiaryBank.setBeneficiaryAddedDate(new Date());

        BeneficiaryBank savedBeneficiaryBank = beneficiaryBankRepository.save(beneficiaryBank);

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Beneficiary bank created successfully")
                .status(HttpStatus.OK)
                .build();

        BeneficiaryBankResponse beneficiaryBankResponse = mappings.mapToBeneficiaryBankResponse(savedBeneficiaryBank);
        beneficiaryBankResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(beneficiaryBankResponse, HttpStatus.CREATED);
    }

    @Override
    public List<GenericResponse> getBeneficiaryList() {
        List<GenericResponse> beneficiaryList = new ArrayList<>();

        List<BeneficiaryBank> banks = beneficiaryBankRepository.findAll();

        for (BeneficiaryBank bank : banks) {

            GenericResponse response = GenericResponse.builder()
                    .id(bank.getBeneficiaryCode())
                    .name(bank.getBeneficiaryName())
                    .retCode("0")
                    .build();

            beneficiaryList.add(response);
        }

        return beneficiaryList;
    }
}
