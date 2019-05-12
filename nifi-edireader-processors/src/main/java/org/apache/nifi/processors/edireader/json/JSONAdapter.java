package org.apache.nifi.processors.edireader.json;

import com.berryworks.edireader.XMLTags;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONAdapter extends DefaultHandler {

    private static final Logger logger = LoggerFactory.getLogger(JSONAdapter.class);

    private final XMLTags xmlTags;
    private final List<Interchange> interchanges = new ArrayList<>();
    private Interchange interchange;
    private boolean senderAddress = false;
    private boolean receiverAddress = false;

    private FunctionalGroup functionalGroup;

    private Transaction transaction;

    private int loopCount = 0;
    private Loop loop = null;

    private HashMap<String, String> elements;
    private String currentElement;
    private String currentSubelementSequence;

    private String elementString;

    public JSONAdapter(XMLTags xmlTags) {
        this.xmlTags = xmlTags;
        interchange = new Interchange();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (localName.startsWith(xmlTags.getInterchangeTag())) {
            startInterchange(uri, localName, qName, attributes);
        } else if (localName.startsWith(xmlTags.getReceiverTag())) {
            receiverAddress = true;
        } else if (localName.startsWith(xmlTags.getSenderTag())) {
            senderAddress = true;
        } else if (localName.startsWith(xmlTags.getAddressTag())) {
            if (senderAddress) {
                startSender(uri, localName, qName, attributes);
            }

            if (receiverAddress) {
                startReceiver(uri, localName, qName, attributes);
            }

            senderAddress = false;
            receiverAddress = false;
        } else if (localName.startsWith(xmlTags.getGroupTag())) {
            startFunctionalGroup(uri, localName, qName, attributes);
        } else if (localName.startsWith(xmlTags.getDocumentTag())) {
            startTransaction(uri, localName, qName, attributes);
        } else if (localName.startsWith(xmlTags.getLoopTag())) {
            startLoop(uri, localName, qName, attributes);
        } else if (localName.startsWith(xmlTags.getSegTag())) {
            startSegment(uri, localName, qName, attributes);
        } else if (localName.startsWith(xmlTags.getElementTag())) {
            elementString = "";
            startSegmentElement(uri, localName, qName, attributes);
        } else if (localName.startsWith(xmlTags.getSubElementTag())) {
            elementString = "";
            startSegmentSubElement(uri, localName, qName, attributes);
        } else {
            logger.info("{}", localName);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.startsWith(xmlTags.getLoopTag())) {
            endLoop(uri, localName, qName);
        }
        if (localName.startsWith(xmlTags.getSegTag())) {
            endSegment(uri, localName, qName);
        } else if (localName.startsWith(xmlTags.getElementTag())) {
            endSegmentElement(uri, localName, qName);
            elementString = "";
        } else if (localName.startsWith(xmlTags.getSubElementTag())) {
            endSegmentSubElement(uri, localName, qName);
            elementString = "";
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String s = new String(ch, start, length);
        if (elementString != null) elementString += s;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(interchanges);
    }

    public String toPrettyJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(interchanges);
    }

    public byte[] toJsonBytes() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(interchanges);
    }

    public byte[] toPrettyJsonBytes() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(interchanges);
    }

    private void startInterchange(String uri, String localName, String qName, Attributes attributes) {
        interchange = new Interchange();
        interchange.setAuthorizationQualifier(attributes.getValue("AuthorizationQual"));
        interchange.setAuthorizationInformation(attributes.getValue("Authorization"));
        interchange.setSecurityQualifier(attributes.getValue("SecurityQual"));
        interchange.setSecurityInformation(attributes.getValue("Security"));
        interchange.setDate(attributes.getValue("Date"));
        interchange.setTime(attributes.getValue("Time"));
        interchange.setStandardsId(attributes.getValue("StandardsId"));
        interchange.setVersion(attributes.getValue("Version"));
        interchange.setInterchangeControlNumber(attributes.getValue("Control"));
        interchange.setAcknowledgmentRequested(attributes.getValue("AckRequest"));
        interchange.setTestIndicator(attributes.getValue("TestIndicator"));
        interchanges.add(interchange);
    }

    private void startReceiver(String uri, String localName, String qName, Attributes attributes) {
        interchange.setReceiverId(attributes.getValue("Id"));
        interchange.setReceiverQualifier(attributes.getValue("Qual"));
    }

    private void startSender(String uri, String localName, String qName, Attributes attributes) {
        interchange.setSenderId(attributes.getValue("Id"));
        interchange.setSenderQualifier(attributes.getValue("Qual"));
    }

    private void startFunctionalGroup(String uri, String localName, String qName, Attributes attributes) {
        functionalGroup = new FunctionalGroup();
        functionalGroup.setFunctionalIdentifierCode(attributes.getValue("GroupType"));
        functionalGroup.setApplicationSenderCode(attributes.getValue("ApplSender"));
        functionalGroup.setApplicationReceiverCode(attributes.getValue("ApplReceiver"));
        functionalGroup.setDate(attributes.getValue("Date"));
        functionalGroup.setTime(attributes.getValue("Time"));
        functionalGroup.setGroupControlNumber(attributes.getValue("Control"));
        functionalGroup.setResponsibleAgencyCode(attributes.getValue("StandardCode"));
        functionalGroup.setVersion(attributes.getValue("StandardVersion"));
        interchange.addFunctionalGroup(functionalGroup);
    }

    private void startTransaction(String uri, String localName, String qName, Attributes attributes) {
        transaction = new Transaction();
        transaction.setName(attributes.getValue("Name"));
        transaction.setTransactionSetIdentifierCode(attributes.getValue("DocType"));
        transaction.setTransactionSetControlNumber(attributes.getValue("Control"));
        transaction.setImplementationConventionReference(attributes.getValue(""));

        functionalGroup.addTransaction(transaction);
    }

    private void startLoop(String uri, String localName, String qName, Attributes attributes) {
        String loopId = attributes.getValue("Id");

        if (loopCount == 0) {
            loop = new Loop();
            loop.setId(loopId);
            loop.setParentLoop(loop);
            transaction.addLoop(loop);
        } else {
            Loop l = new Loop();
            l.setId(loopId);
            l.setParentLoop(loop);
            loop.addLoop(l);
            loop = l;
        }

        loopCount++;
    }

    private void endLoop(String uri, String localName, String qName) {
        if (loopCount != 0) {
            loop = loop.getParentLoop();
        }

        loopCount--;
    }

    private void startSegment(String uri, String localName, String qName, Attributes attributes) {
        elements = new HashMap<>();
        elements.put("id", attributes.getValue("Id"));
    }

    private void endSegment(String uri, String localName, String qName) {
        if (loop == null) {
            transaction.addSegment(elements);
        } else {
            loop.addSegment(elements);
        }
    }

    private void startSegmentElement(String uri, String localName, String qName, Attributes attributes) {
        currentElement = attributes.getValue(0);
    }

    private void endSegmentElement(String uri, String localName, String qName) {
        elements.put(currentElement, elementString);
    }

    private void startSegmentSubElement(String uri, String localName, String qName, Attributes attributes) {
        currentSubelementSequence = currentElement + "-" + attributes.getValue("Sequence");
    }

    private void endSegmentSubElement(String uri, String localName, String qName) {
        elements.put(currentSubelementSequence, elementString);
    }
}

