package com.company.subscriptionservice.service.dto;

import com.company.subscriptionservice.domain.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.UUID;

public class SubscriptionDTO {

    private UUID id;

    @Size(max = 50)
    private String firstname;

    @Size(max = 50)
    private String lastname;

    @Size(max = 50)
    private String gender;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    private Instant birthdate = null;

    private boolean consent = false;

    private Object campaigns;

    public SubscriptionDTO() {
        super();
    }

    public SubscriptionDTO(User user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.gender = user.getGender();
        this.email = user.getEmail();
        this.birthdate = user.getBirthdate();
        this.consent = user.getConsent();
        this.campaigns = user.getCampaigns();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Instant birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isConsent() {
        return consent;
    }

    public void setConsent(boolean consent) {
        this.consent = consent;
    }

    public Object getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(Collection<Object> campaigns) {
        this.campaigns = campaigns;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SubscriptionDTO.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("firstName='" + firstname + "'")
                .add("lastName='" + lastname + "'")
                .add("gender='" + gender + "'")
                .add("email='" + email + "'")
                .add("birthdate=" + birthdate)
                .add("consent=" + consent)
                .add("campaigns=" + campaigns)
                .toString();
    }
}
