package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.entity.navigations.GeneralNeededInfo;
import com.dsu.hope_bank_app_middleware.entity.navigations.AssociateBank;
import com.dsu.hope_bank_app_middleware.enumeration.ResponseType;
import com.dsu.hope_bank_app_middleware.exception.ResourceNotFoundException;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.repository.AssociateBankRepository;
import com.dsu.hope_bank_app_middleware.repository.GeneralNeededInfoRepository;
import com.dsu.hope_bank_app_middleware.request.navigations.BankRequest;
import com.dsu.hope_bank_app_middleware.request.navigations.GeneralNeededInfoRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.GeneralNeededInfoResponse;
import com.dsu.hope_bank_app_middleware.service.GeneralNeededInfoService;
import com.dsu.hope_bank_app_middleware.utils.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class GeneralNeededInfoServiceImpl implements GeneralNeededInfoService {

    @Autowired
    private GeneralNeededInfoRepository generalNeededInfoRepository;

    @Autowired
    private AssociateBankRepository associateBankRepository;

    private final Mappings mappings;

    @Autowired
    public GeneralNeededInfoServiceImpl(Mappings mappings) {
        this.mappings = mappings;
    }

    private static final Logger logger = Logger.getLogger(GeneralNeededInfoServiceImpl.class.getName());

    @Override
    public ResponseEntity<GeneralNeededInfoResponse> createGeneralNeededInfo(GeneralNeededInfoRequest request) {
        AssociateBank associateBank = associateBankRepository.findByAssociateBankName(request.getAssociateBank())
                .orElseThrow(() -> new ResourceNotFoundException("Associate bank not found with bank name: " + request.getAssociateBank()));

        GeneralNeededInfo generalNeededInfo = GeneralNeededInfo.builder()
                .primaryColor(request.getPrimaryColor())
                .secondaryColor(request.getSecondaryColor())
                .thirdColor(request.getThirdColor())
                .fourthColor(request.getFourthColor())
                .contactEmail(request.getContactEmail())
                .contactPhoneCall(request.getContactPhoneCall())
                .contactPhoneSms(request.getContactPhoneSms())
                .isAnyServiceDown(request.getIsAnyServiceDown())
                .bannerWarningMessage(request.getBannerWarningMessage())
                .bank(associateBank)
                .build();

        GeneralNeededInfo saved = generalNeededInfoRepository.save(generalNeededInfo);

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("General needed info created successfully")
                .status(HttpStatus.CREATED)
                .build();

        GeneralNeededInfoResponse response = mappings.mapToGeneralNeededInfoResponse(saved);
        response.setSuccessResponse(successResponse);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<GeneralNeededInfoResponse> getGeneralNeededInfoById(String id) {
        GeneralNeededInfo generalNeededInfo = generalNeededInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("General needed info not found with id: " + id));

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("General needed info retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        GeneralNeededInfoResponse response = mappings.mapToGeneralNeededInfoResponse(generalNeededInfo);
        response.setSuccessResponse(successResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<GeneralNeededInfoResponse>> getAllGeneralNeededInfo() {
        List<GeneralNeededInfo> generalNeededInfos = generalNeededInfoRepository.findAll();

        if (generalNeededInfos.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<GeneralNeededInfoResponse> responses = generalNeededInfos.stream()
                .map(mappings::mapToGeneralNeededInfoResponse)
                .collect(Collectors.toList());

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("All general needed info retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        responses.forEach(response -> response.setSuccessResponse(successResponse));

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<GeneralNeededInfoResponse>> getAllGeneralNeededInfoByBankId(String bankId) {
        associateBankRepository.findById(bankId)
                .orElseThrow(() -> new ResourceNotFoundException("Associate bank not found with id: " + bankId));

        List<GeneralNeededInfo> generalNeededInfos = generalNeededInfoRepository.findByBank_Id(bankId);

        if (generalNeededInfos.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<GeneralNeededInfoResponse> responses = generalNeededInfos.stream()
                .map(mappings::mapToGeneralNeededInfoResponse)
                .collect(Collectors.toList());

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("General needed info retrieved successfully for bank id: " + bankId)
                .status(HttpStatus.OK)
                .build();

        responses.forEach(response -> response.setSuccessResponse(successResponse));

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GeneralNeededInfoResponse> updateGeneralNeededInfo(String id, GeneralNeededInfoRequest request) {
        logger.log(Level.INFO, "Update general util request: {0}", request);
        GeneralNeededInfo generalNeededInfo = generalNeededInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("General needed info not found with id: " + id));

        generalNeededInfo.setPrimaryColor(request.getPrimaryColor());
        generalNeededInfo.setSecondaryColor(request.getSecondaryColor());
        generalNeededInfo.setThirdColor(request.getThirdColor());
        generalNeededInfo.setFourthColor(request.getFourthColor());
        generalNeededInfo.setContactEmail(request.getContactEmail());
        generalNeededInfo.setContactPhoneCall(request.getContactPhoneCall());
        generalNeededInfo.setContactPhoneSms(request.getContactPhoneSms());
        generalNeededInfo.setIsAnyServiceDown(request.getIsAnyServiceDown());
        generalNeededInfo.setBannerWarningMessage(request.getBannerWarningMessage());

        GeneralNeededInfo updated = generalNeededInfoRepository.save(generalNeededInfo);

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("General needed info updated successfully")
                .status(HttpStatus.OK)
                .build();

        GeneralNeededInfoResponse response = mappings.mapToGeneralNeededInfoResponse(updated);
        response.setSuccessResponse(successResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GeneralNeededInfoResponse> deleteGeneralNeededInfo(String id) {
        GeneralNeededInfo generalNeededInfo = generalNeededInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("General needed info not found with id: " + id));

        generalNeededInfoRepository.delete(generalNeededInfo);

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("General needed info deleted successfully")
                .status(HttpStatus.OK)
                .build();

        GeneralNeededInfoResponse response = mappings.mapToGeneralNeededInfoResponse(generalNeededInfo);
        response.setSuccessResponse(successResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GeneralNeededInfoResponse> getGeneralNeededInfoByBankName(BankRequest request) {
        logger.log(Level.INFO, "Get general util by name request: {0}", request);
        AssociateBank associateBank = associateBankRepository.findByAssociateBankName(request.getAssociateBankName()).
                orElseThrow(() -> new ResourceNotFoundException("Associate bank is not found with name: " + request.getAssociateBankName()));

        GeneralNeededInfo generalNeededInfo = generalNeededInfoRepository.findByBank(associateBank)
                .orElseThrow(() -> new ResourceNotFoundException("General needed info not found for associate bank: " + associateBank.getAssociateBankName()));

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("General needed info retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        GeneralNeededInfoResponse response = mappings.mapToGeneralNeededInfoResponse(generalNeededInfo);
        response.setSuccessResponse(successResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
