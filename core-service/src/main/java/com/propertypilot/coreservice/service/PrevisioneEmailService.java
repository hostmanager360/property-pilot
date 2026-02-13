package com.propertypilot.coreservice.service;

public interface PrevisioneEmailService {
    void sendPrevisioneToOwner(Integer previsioneId, String ownerEmail, String ownerName);

}
