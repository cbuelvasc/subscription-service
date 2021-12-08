package com.company.subscriptionservice.web;

import com.company.subscriptionservice.domain.User;
import com.company.subscriptionservice.repository.UserRepository;
import com.company.subscriptionservice.service.SubscriptionService;
import com.company.subscriptionservice.service.dto.SubscriptionDTO;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SubscriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionResource.class);

    private final SubscriptionService subscriptionService;

    private final UserRepository userRepository;

    public SubscriptionResource(SubscriptionService subscriptionService, UserRepository userRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions(Pageable pageable) {
        LOG.debug("REST request to get all Subscription");

        final Page<SubscriptionDTO> page = subscriptionService.getAllSubscriptions(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/subscriptions/{id}")
    public ResponseEntity<SubscriptionDTO> getSubscription(@PathVariable UUID id) {
        LOG.debug("REST request to get Subscription : {}", id);
        return ResponseEntity.ok(subscriptionService.getSubscription(id));
    }

    @PostMapping("/subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SubscriptionDTO> createSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Subscription : {}", subscriptionDTO);
        SubscriptionDTO subscription = subscriptionService.createSubscription(subscriptionDTO);
        return ResponseEntity
                .created(new URI("/api/subscriptions/" + subscription.getEmail()))
                .body(subscription);
    }

    @PutMapping("/subscriptions/{id}")
    public ResponseEntity<SubscriptionDTO> updateSubscription(@PathVariable UUID id, @Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        LOG.debug("REST request to update Subscription : {}", subscriptionDTO);
        Optional<SubscriptionDTO> updatedSubscription = subscriptionService.updateSubscription(id, subscriptionDTO);
        return ResponseUtil.wrapOrNotFound(
                updatedSubscription,
                HeaderUtil.createAlert("Subscription System", "A subscription is updated with identifier " + subscriptionDTO.getEmail(), subscriptionDTO.getEmail())
        );
    }

    @PatchMapping("/subscriptions/{id}")
    public ResponseEntity<SubscriptionDTO> cancelSubscription(@PathVariable UUID id) {
        LOG.debug("REST request to cancel Subscription : {}", id);

        Optional<User> existingSubscription = userRepository.findById(id);
        if (!existingSubscription.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Optional<SubscriptionDTO> updatedUser = subscriptionService.cancelSubscription(id);

        return ResponseUtil.wrapOrNotFound(
                updatedUser,
                HeaderUtil.createAlert("Subscription System", "A subscription is updated with identifier " + id, id.toString())
        );
    }

    @DeleteMapping("/subscriptions/{userId}/campaign/{campaignId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteSubscription(@PathVariable UUID userId, @PathVariable UUID campaignId) {
        LOG.debug("REST request to delete subscription for user id: {}", userId);
        subscriptionService.deleteSubscription(userId, campaignId);
        return ResponseEntity.noContent().build();
    }
}
