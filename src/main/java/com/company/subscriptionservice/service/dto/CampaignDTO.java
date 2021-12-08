package com.company.subscriptionservice.service.dto;

import com.company.subscriptionservice.domain.Campaign;

import javax.validation.constraints.Size;
import java.util.StringJoiner;
import java.util.UUID;

public class CampaignDTO {

    private UUID id;

    @Size(max = 50)
    private String name;

    public CampaignDTO() {
        super();
    }

    public CampaignDTO(Campaign campaign) {
        this.id = campaign.getId();
        this.name = campaign.getName();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CampaignDTO.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }
}
