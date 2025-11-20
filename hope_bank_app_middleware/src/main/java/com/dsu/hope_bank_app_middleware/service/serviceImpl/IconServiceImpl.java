package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.entity.navigations.AssociateBank;
import com.dsu.hope_bank_app_middleware.entity.navigations.IconItem;
import com.dsu.hope_bank_app_middleware.enumeration.ResponseType;
import com.dsu.hope_bank_app_middleware.exception.ResourceNotFoundException;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.repository.IconRepository;
import com.dsu.hope_bank_app_middleware.request.navigations.IconRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.AssociateBankResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.IconResponse;
import com.dsu.hope_bank_app_middleware.service.IconService;
import com.dsu.hope_bank_app_middleware.utils.Mappings;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IconServiceImpl implements IconService {
    @Autowired
    private IconRepository iconRepository;

    private final Mappings mappings;

    @Autowired
    public IconServiceImpl(Mappings mappings) {
        this.mappings = mappings;
    }

    @Override
    public ResponseEntity<IconResponse> createIcon(IconRequest request) {
        IconItem iconItem = new IconItem();
        iconItem.setIconName(request.getIconName());
        iconItem.setIcon(request.getIcon());
        iconItem.setIconAddedDate(new Date());

        IconItem savedIcon = iconRepository.save(iconItem);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Icon created successfully")
                .status(HttpStatus.OK)
                .build();

        IconResponse iconResponse = mappings.mapToIconResponse(savedIcon);
        iconResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(iconResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<IconResponse> getIconById(String id) {
        IconItem iconItem = iconRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Icon Item not found"));

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Icon Item retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        IconResponse iconResponse = mappings.mapToIconResponse(iconItem);
        iconResponse.setSuccessResponse(successResponse);
        return new ResponseEntity<>(iconResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<IconResponse> updateIconById(String id, IconRequest request) {
        IconItem iconItem = iconRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Icon Item not found"));

        iconItem.setIconName(request.getIconName());
        iconItem.setIcon(request.getIcon());
        iconItem.setIconUpdatedDate(new Date());

        IconItem updatedIconItem = iconRepository.save(iconItem);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Icon Item updated successfully")
                .status(HttpStatus.OK)
                .build();

        IconResponse iconResponse = mappings.mapToIconResponse(updatedIconItem);
        iconResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(iconResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<IconResponse>> getAllIconItems() {
        List<IconItem> iconItems = iconRepository.findAll();

        List<IconResponse> iconResponses = iconItems.stream()
                .map(mappings::mapToIconResponse) // Reuse the mapToResponse method for mapping
                .collect(Collectors.toList());

        // Create a success response object
        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("All Icon Items retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        iconResponses.forEach(response -> response.setSuccessResponse(successResponse));

        // Return the list wrapped in a ResponseEntity
        return new ResponseEntity<>(iconResponses, HttpStatus.OK);
    }


}
