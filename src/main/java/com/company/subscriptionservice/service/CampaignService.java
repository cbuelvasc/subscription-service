package com.company.subscriptionservice.service;

import com.company.subscriptionservice.domain.Campaign;
import com.company.subscriptionservice.domain.User;
import com.company.subscriptionservice.repository.CampaignRepository;
import com.company.subscriptionservice.service.dto.CampaignDTO;
import com.company.subscriptionservice.web.handler.exceptions.AlreadyExistsException;
import com.company.subscriptionservice.web.handler.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CampaignService {

    private static final Logger LOG = LoggerFactory.getLogger(CampaignService.class);

    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Transactional(readOnly = true)
    public Page<CampaignDTO> getAllCampaigns(Pageable pageable) {
        return campaignRepository.findAll(pageable).map(CampaignDTO::new);
    }

    @Transactional(readOnly = true)
    public CampaignDTO getCampaign(UUID id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Not found campaign with id: %s", id)));

        return new CampaignDTO(campaign);
    }

    public CampaignDTO createCampaign(CampaignDTO campaignDTO) {
        if (campaignRepository.existsByName(campaignDTO.getName())) {
            throw new AlreadyExistsException(String.format("Campaign with name %s already exists", campaignDTO.getName()));
        }
        Campaign campaign = new Campaign();
        campaign.setName(campaignDTO.getName());
        campaign.setCreatedBy("admin");
        LOG.debug("Created Information for Campaign: {}", campaign);
        return new CampaignDTO(campaignRepository.save(campaign));
    }

    public Optional<CampaignDTO> updateCampaign(UUID id, CampaignDTO campaignDTO) {
        Optional <Campaign> optionalCampaign = campaignRepository.findById(id);

        if (optionalCampaign.isEmpty()) {
            throw new NotFoundException(String.format("Not found campaign with id: %s", id));
        }
        return Optional
                .of(optionalCampaign)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(campaign -> {
                    if (campaignDTO.getName() != null) {
                        campaign.setName(campaignDTO.getName());
                    }
                    return campaign;
                })
                .map(CampaignDTO::new);
    }

    public void deleteCampaign(UUID id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Not found campaign with id: %s", id)));
        LOG.debug("Deleted Campaign: {}", campaign);
        campaignRepository.delete(campaign);
    }
}