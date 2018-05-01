package org.apache.nifi.processors.edireader.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Transaction {
    private String name;
    private String transactionSetIdentifierCode;
    private String transactionSetControlNumber;
    private String implementationConventionReference;

    private List<HashMap<String, String>> segments;
    private List<Loop> loops;

    public Transaction() {
        segments = new ArrayList<>();
        loops = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransactionSetIdentifierCode() {
        return transactionSetIdentifierCode;
    }

    public void setTransactionSetIdentifierCode(String transactionSetIdentifierCode) {
        this.transactionSetIdentifierCode = transactionSetIdentifierCode;
    }

    public String getTransactionSetControlNumber() {
        return transactionSetControlNumber;
    }

    public void setTransactionSetControlNumber(String transactionSetControlNumber) {
        this.transactionSetControlNumber = transactionSetControlNumber;
    }

    public String getImplementationConventionReference() {
        return implementationConventionReference;
    }

    public void setImplementationConventionReference(String implementationConventionReference) {
        this.implementationConventionReference = implementationConventionReference;
    }

    public List<HashMap<String, String>> getSegments() {
        return segments;
    }

    public void setSegments(List<HashMap<String, String>> segments) {
        this.segments = segments;
    }

    public void addSegment(HashMap<String, String> segment) {
        segments.add(segment);
    }

    public List<Loop> getLoops() {
        return loops;
    }

    public void setLoops(List<Loop> loops) {
        this.loops = loops;
    }

    public void addLoop(Loop loop) {
        loops.add(loop);
    }
}

