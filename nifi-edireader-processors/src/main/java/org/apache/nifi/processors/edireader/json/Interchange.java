package org.apache.nifi.processors.edireader.json;

import java.util.ArrayList;
import java.util.List;

class Interchange {

    // ISA01
    private String authorizationQualifier;
    // ISA02
    private String authorizationInformation;
    // ISA03
    private String securityQualifier;
    // ISA04
    private String securityInformation;
    // ISA05
    private String senderQualifier;
    // ISA06
    private String senderId;
    // ISA07
    private String receiverQualifier;
    // ISA08
    private String receiverId;
    // ISA09
    private String date;
    // ISA10
    private String time;
    // ISA11
    private String standardsId;
    // ISA12
    private String version;
    // ISA13
    private String interchangeControlNumber;
    // ISA14
    private String acknowledgmentRequested;
    // ISA15
    private String testIndicator;

    private List<FunctionalGroup> functionalGroups;

    public Interchange() {
        functionalGroups = new ArrayList<>();
    }

    public String getAuthorizationQualifier() {
        return authorizationQualifier;
    }

    public void setAuthorizationQualifier(String authorizationQualifier) {
        this.authorizationQualifier = authorizationQualifier;
    }

    public String getAuthorizationInformation() {
        return authorizationInformation;
    }

    public void setAuthorizationInformation(String authorizationInformation) {
        this.authorizationInformation = authorizationInformation;
    }

    public String getSecurityQualifier() {
        return securityQualifier;
    }

    public void setSecurityQualifier(String securityQualifier) {
        this.securityQualifier = securityQualifier;
    }

    public String getSecurityInformation() {
        return securityInformation;
    }

    public void setSecurityInformation(String securityInformation) {
        this.securityInformation = securityInformation;
    }

    public String getSenderQualifier() {
        return senderQualifier;
    }

    public void setSenderQualifier(String senderQualifier) {
        this.senderQualifier = senderQualifier;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverQualifier() {
        return receiverQualifier;
    }

    public void setReceiverQualifier(String receiverQualifier) {
        this.receiverQualifier = receiverQualifier;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStandardsId() {
        return standardsId;
    }

    public void setStandardsId(String standardsId) {
        this.standardsId = standardsId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInterchangeControlNumber() {
        return interchangeControlNumber;
    }

    public void setInterchangeControlNumber(String interchangeControlNumber) {
        this.interchangeControlNumber = interchangeControlNumber;
    }

    public String getAcknowledgmentRequested() {
        return acknowledgmentRequested;
    }

    public void setAcknowledgmentRequested(String acknowledgmentRequested) {
        this.acknowledgmentRequested = acknowledgmentRequested;
    }

    public String getTestIndicator() {
        return testIndicator;
    }

    public void setTestIndicator(String testIndicator) {
        this.testIndicator = testIndicator;
    }

    public List<FunctionalGroup> getFunctionalGroups() {
        return functionalGroups;
    }

    public void setFunctionalGroups(List<FunctionalGroup> functionalGroups) {
        this.functionalGroups = functionalGroups;
    }

    public void addFunctionalGroup(FunctionalGroup functionalGroup) {
        this.functionalGroups.add(functionalGroup);
    }
}

