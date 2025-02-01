package com.dsu.hope_bank_app_middleware.navigations.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.navigations.entity.AssociateBank;
import com.dsu.hope_bank_app_middleware.general_enumerations.ResponseType;
import com.dsu.hope_bank_app_middleware.exception.ResourceNotFoundException;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.navigations.entity.FormElement;
import com.dsu.hope_bank_app_middleware.navigations.entity.MainMenu;
import com.dsu.hope_bank_app_middleware.navigations.entity.SubMenu;
import com.dsu.hope_bank_app_middleware.navigations.repository.AssociateBankRepository;
import com.dsu.hope_bank_app_middleware.navigations.repository.FormElementRepository;
import com.dsu.hope_bank_app_middleware.navigations.repository.MainMenuRepository;
import com.dsu.hope_bank_app_middleware.navigations.repository.SubMenuRepository;
import com.dsu.hope_bank_app_middleware.navigations.request.AssociateBankRequest;
import com.dsu.hope_bank_app_middleware.navigations.response.*;
import com.dsu.hope_bank_app_middleware.navigations.service.AssociateBankService;
import com.dsu.hope_bank_app_middleware.utils.Mappings;
import lombok.*;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssociateBankServiceImpl implements AssociateBankService {

    @Autowired
    private AssociateBankRepository associateBankRepository;

    @Autowired
    private MainMenuRepository mainMenuRepository;

    @Autowired
    private SubMenuRepository subMenuRepository;

    @Autowired
    private FormElementRepository formElementRepository;

    private final Mappings mappings;

    @Autowired
    public AssociateBankServiceImpl(Mappings mappings) {
        this.mappings = mappings;
    }

    @Override
    public ResponseEntity<AssociateBankResponse> createAssociateBank(AssociateBankRequest request) {
        AssociateBank associateBank = new AssociateBank();
        associateBank.setAssociateBankName(request.getAssociateBankName());
        associateBank.setAssociateBankStatus(request.getAssociateBankStatus());
        associateBank.setAssociateBankAddedDate(new Date());
        AssociateBank savedAssociateBank = associateBankRepository.save(associateBank);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("AssociateBank created successfully")
                .status(HttpStatus.OK)
                .build();

        AssociateBankResponse associateBankResponse = mappings.mapToResponse(savedAssociateBank);
        associateBankResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(associateBankResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<AssociateBankResponse> getAssociateBankById(String id) {
        AssociateBank associateBank = associateBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssociateBank not found"));

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("AssociateBank retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        AssociateBankResponse associateBankResponse = mappings.mapToResponse(associateBank);
        associateBankResponse.setSuccessResponse(successResponse);
        return new ResponseEntity<>(associateBankResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<AssociateBankResponse>> getAllAssociateBanks() {
        // Fetch all associate banks from the repository
        List<AssociateBank> associateBanks = associateBankRepository.findAll();

        // Map each AssociateBank entity to an AssociateBankResponse object
        List<AssociateBankResponse> associateBankResponses = associateBanks.stream()
                .map(mappings::mapToResponse) // Reuse the mapToResponse method for mapping
                .collect(Collectors.toList());

        // Create a success response object
        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("All AssociateBanks retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        // Attach the success response to each AssociateBankResponse
        associateBankResponses.forEach(response -> response.setSuccessResponse(successResponse));

        // Return the list wrapped in a ResponseEntity
        return new ResponseEntity<>(associateBankResponses, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<AssociateBankResponse> updateAssociateBank(String id, AssociateBankRequest request) {
        AssociateBank associateBank = associateBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssociateBank not found"));

        associateBank.setAssociateBankName(request.getAssociateBankName());
        associateBank.setAssociateBankStatus(request.getAssociateBankStatus());
        associateBank.setAssociateBankUpdatedDate(new Date());

        AssociateBank updatedAssociateBank = associateBankRepository.save(associateBank);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("AssociateBank updated successfully")
                .status(HttpStatus.OK)
                .build();

        AssociateBankResponse associateBankResponse = mappings.mapToResponse(updatedAssociateBank);
        associateBankResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(associateBankResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<NavigationsResponse> getAllNavigationResponse(String bankId) {
        // Fetch all associate banks from the database
        // Fetch the specific associate bank by bankId
        Optional<AssociateBank> associateBankOptional = associateBankRepository.findById(bankId);


        if (associateBankOptional.isEmpty()) {
            // Handle case where bankId is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Or you can create an error response object
        }

        // Map the AssociateBank entity to a NavigationsResponse
        NavigationsResponse navigationResponse = mappings.mapToNavigationsResponse(associateBankOptional.get());

        return ResponseEntity.ok(navigationResponse);
    }

}
