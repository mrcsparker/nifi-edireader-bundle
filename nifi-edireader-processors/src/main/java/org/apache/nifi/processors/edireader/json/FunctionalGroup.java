package org.apache.nifi.processors.edireader.json;

import java.util.ArrayList;
import java.util.List;

class FunctionalGroup {
    private String functionalIdentifierCode;
    private String applicationSenderCode;
    private String applicationReceiverCode;
    private String date;
    private String time;
    private String groupControlNumber;
    private String responsibleAgencyCode;
    private String version;
    private List<Transaction> transactions;

    public FunctionalGroup() {
        transactions = new ArrayList<>();
    }

    public String getFunctionalIdentifierCode() {
        return functionalIdentifierCode;
    }

    public void setFunctionalIdentifierCode(String functionalIdentifierCode) {
        this.functionalIdentifierCode = functionalIdentifierCode;
    }

    public String getApplicationSenderCode() {
        return applicationSenderCode;
    }

    public void setApplicationSenderCode(String applicationSenderCode) {
        this.applicationSenderCode = applicationSenderCode;
    }

    public String getApplicationReceiverCode() {
        return applicationReceiverCode;
    }

    public void setApplicationReceiverCode(String applicationReceiverCode) {
        this.applicationReceiverCode = applicationReceiverCode;
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

    public String getGroupControlNumber() {
        return groupControlNumber;
    }

    public void setGroupControlNumber(String groupControlNumber) {
        this.groupControlNumber = groupControlNumber;
    }

    public String getResponsibleAgencyCode() {
        return responsibleAgencyCode;
    }

    public void setResponsibleAgencyCode(String responsibleAgencyCode) {
        this.responsibleAgencyCode = responsibleAgencyCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
