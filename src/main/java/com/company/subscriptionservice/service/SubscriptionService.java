package com.company.subscriptionservice.service;

import com.company.subscriptionservice.domain.Campaign;
import com.company.subscriptionservice.domain.User;
import com.company.subscriptionservice.event.Event;
import com.company.subscriptionservice.repository.CampaignRepository;
import com.company.subscriptionservice.repository.UserRepository;
import com.company.subscriptionservice.service.dto.SubscriptionDTO;
import com.company.subscriptionservice.web.handler.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionService.class);

    private final UserRepository userRepository;

    private final CampaignRepository campaignRepository;

    private final StreamBridge streamBridge;

    public SubscriptionService(UserRepository userRepository, CampaignRepository campaignRepository, StreamBridge streamBridge) {
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
        this.streamBridge = streamBridge;
    }

    @Transactional(readOnly = true)
    public Page<SubscriptionDTO> getAllSubscriptions(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(entity -> {
                    if (entity.getCampaigns() != null) {
                        Set<Campaign> campaigns = entity
                                .getCampaigns()
                                .stream()
                                .map(campaign -> campaignRepository.findById(campaign.getId()))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toSet());
                        entity.setCampaigns(campaigns);
                    }
                    return new SubscriptionDTO(entity);
                });
    }

    @Transactional(readOnly = true)
    public SubscriptionDTO getSubscription(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Not found user with id: %s", id)));

        if (user.getCampaigns() != null) {
            Set<Campaign> campaigns = user.getCampaigns()
                    .stream()
                    .map(campaign -> campaignRepository.findById(campaign.getId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            user.setCampaigns(campaigns);
        }
        return new SubscriptionDTO(user);
    }

    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        Optional<User> optionalUser = userRepository.findOneByEmail(subscriptionDTO.getEmail());
        SubscriptionDTO dto;
        if (optionalUser.isPresent()) {
            if (((List<String>) subscriptionDTO.getCampaigns()).isEmpty()) {
                dto = subscriptionDTO;
            } else {
                Set<Campaign> campaigns = ((List<String>) subscriptionDTO.getCampaigns())
                        .stream()
                        .filter(id -> !optionalUser.get().getCampaigns().contains(UUID.fromString(id)))
                        .map(id -> campaignRepository.findById(UUID.fromString(id)).get())
                        .collect(Collectors.toSet());
                optionalUser.get().setCampaigns(campaigns);
                dto = new SubscriptionDTO(userRepository.save(optionalUser.get()));
            }
        } else {
            User user = new User();
            if (subscriptionDTO.getFirstname() != null) {
                user.setFirstname(subscriptionDTO.getFirstname());
            }
            if (subscriptionDTO.getLastname() != null) {
                user.setLastname(subscriptionDTO.getLastname());
            }
            if (subscriptionDTO.getGender() != null) {
                user.setGender(subscriptionDTO.getGender());
            }
            user.setEmail(subscriptionDTO.getEmail());
            user.setBirthdate(subscriptionDTO.getBirthdate());
            user.setConsent(subscriptionDTO.isConsent());
            if (subscriptionDTO.getCampaigns() != null) {
                Set<Campaign> campaigns = ((List<String>) subscriptionDTO
                        .getCampaigns())
                        .stream()
                        .map(campaign -> campaignRepository.findById(UUID.fromString(campaign)))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
                user.setCampaigns(campaigns);
            }
            user.setCreatedBy(subscriptionDTO.getEmail());
            user.setVersion(0);
            LOG.debug("Created Information for Subscription: {}", user);
            dto = new SubscriptionDTO(userRepository.save(user));
        }
        sendMessage("email-out-0", new Event(dto.getId(), dto));
        return dto;
    }

    public Optional<SubscriptionDTO> updateSubscription(UUID id, SubscriptionDTO subscriptionDTO) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Not found user with id: %s", id));
        }
        return Optional
                .of(optionalUser)
                .map(Optional::get)
                .map(user -> {
                    if (subscriptionDTO.getFirstname() != null) {
                        user.setFirstname(subscriptionDTO.getFirstname());
                    }
                    if (subscriptionDTO.getLastname() != null) {
                        user.setLastname(subscriptionDTO.getLastname());
                    }
                    if (subscriptionDTO.getGender() != null) {
                        user.setGender(subscriptionDTO.getGender());
                    }
                    user.setEmail(subscriptionDTO.getEmail());
                    user.setBirthdate(subscriptionDTO.getBirthdate());
                    user.setConsent(subscriptionDTO.isConsent());
                    Set<Campaign> campaigns = user.getCampaigns();
                    campaigns.clear();
                    ((List<String>) subscriptionDTO
                            .getCampaigns())
                            .stream()
                            .map(campaign -> campaignRepository.findById(UUID.fromString(campaign)))
                            .filter(optional -> {
                                if (optional.isEmpty()) {
                                    throw new NotFoundException(String.format("Not found campaign with id: %s", id));
                                }
                                return true;
                            })
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .forEach(campaigns::add);
                    LOG.debug("Changed Information for Subscription: {}", user);
                    return user;
                })
                .map(SubscriptionDTO::new);
    }

    public Optional<SubscriptionDTO> cancelSubscription(UUID id) {

        return Optional.of(userRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> {
                    if (user.getConsent()) {
                        user.setConsent(Boolean.FALSE);
                    }
                    user.getCampaigns().forEach(campaign -> {
                        System.out.println(campaign.toString());
                    });
                    LOG.debug("Cancel Subscription: {}", user);
                    return user;
                })
                .map(SubscriptionDTO::new);
    }

    public void deleteSubscription(UUID userId, UUID campaignId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Not found user with id: %s", userId)));

        if (!user.getCampaigns().isEmpty()) {

            Campaign campaign = campaignRepository.findById(campaignId)
                    .orElseThrow(() -> new NotFoundException(String.format("Not found campaign with id %s for user %s", campaignId, userId)));

            if (user.getCampaigns().contains(campaign)) {
                user.getCampaigns().remove(campaign);
            }
        } else {
            throw new NotFoundException(String.format("Not found campaign with id %s for user %s", campaignId, userId));
        }
    }

    private void sendMessage(String bindingName, Event event) {
        LOG.debug("Sending message to {}", bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(bindingName, message);
    }
}
