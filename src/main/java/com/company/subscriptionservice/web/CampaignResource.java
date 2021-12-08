package com.company.subscriptionservice.web;

import com.company.subscriptionservice.repository.CampaignRepository;
import com.company.subscriptionservice.service.CampaignService;
import com.company.subscriptionservice.service.dto.CampaignDTO;
import com.company.subscriptionservice.web.util.HeaderUtil;
import com.company.subscriptionservice.web.util.PaginationUtil;
import com.company.subscriptionservice.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CampaignResource {

    private static final Logger LOG = LoggerFactory.getLogger(CampaignResource.class);

    private final CampaignService campaignService;

    private final CampaignRepository campaignRepository;

    public CampaignResource(CampaignService campaignService, CampaignRepository campaignRepository) {
        this.campaignService = campaignService;
        this.campaignRepository = campaignRepository;
    }

    @GetMapping("/campaigns")
    public ResponseEntity<List<CampaignDTO>> getAllCampaigns(Pageable pageable) {
        LOG.debug("REST request to get all Campaign");

        final Page<CampaignDTO> page = campaignService.getAllCampaigns(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/campaigns/{id}")
    public ResponseEntity<CampaignDTO> getCampaign(@PathVariable UUID id) {
        LOG.debug("REST request to get Campaign : {}", id);
        return ResponseEntity.ok(campaignService.getCampaign(id));
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/campaigns", consumes = "application/json")
    public ResponseEntity<CampaignDTO> createCampaign(@Valid @RequestBody CampaignDTO campaignDTO) throws URISyntaxException {
        LOG.debug("REST request to save Campaign : {}", campaignDTO);
        CampaignDTO campaign = campaignService.createCampaign(campaignDTO);
        return ResponseEntity
                .created(new URI("/api/campaigns/" + campaign.getName()))
                .body(campaign);
    }

    @PutMapping("/campaigns/{id}")
    public ResponseEntity<CampaignDTO> updateCampaign(@PathVariable UUID id, @Valid @RequestBody CampaignDTO campaignDTO) {
        LOG.debug("REST request to update Campaign : {}", campaignDTO);
        Optional<CampaignDTO> updatedCampaign = campaignService.updateCampaign(id, campaignDTO);

        return ResponseUtil.wrapOrNotFound(
                updatedCampaign,
                HeaderUtil.createAlert("Campaign System", "A campaign is updated with identifier " + campaignDTO.getName(), campaignDTO.getName())
        );
    }

    @DeleteMapping("/campaigns/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable UUID id) {
        LOG.debug("REST request to delete Campaign: {}", id);
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }
}
