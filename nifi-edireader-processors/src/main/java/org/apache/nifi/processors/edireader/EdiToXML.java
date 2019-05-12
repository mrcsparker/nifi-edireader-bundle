package org.apache.nifi.processors.edireader;

import com.berryworks.edireader.EDIParserFactory;

import com.berryworks.edireader.error.MissingMandatoryElementException;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Tags({"EDI", "X12", "XML"})
@CapabilityDescription("Transform EDI X12 into XML.")
public class EdiToXML extends AbstractProcessor {

    private List<PropertyDescriptor> properties;
    private Set<Relationship> relationships;
    private boolean hasError = false;

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("success")
            .build();

    public static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("failure")
            .build();

    @Override
    public void init(final ProcessorInitializationContext context) {
        this.properties = new ArrayList<>();

        Set<Relationship> relationshipSet = new HashSet<>();
        relationshipSet.add(REL_SUCCESS);
        relationshipSet.add(REL_FAILURE);
        this.relationships = Collections.unmodifiableSet(relationshipSet);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return this.properties;
    }

    @Override
    public void onTrigger(ProcessContext processContext, ProcessSession session) throws ProcessException {
        final FlowFile flowFile = session.get();
        if (flowFile == null) {
            return;
        }

        final FlowFile flowFileClone = session.clone(flowFile);

        session.read(flowFile, inputStream -> session.write(flowFileClone, outputStream -> {
            InputSource inputSource = new InputSource(inputStream);
            try {
                // Establish an XMLReader which is actually an EDIReader.
                SAXParserFactory saxParserFactory = EDIParserFactory.newInstance();
                SAXParser saxParser = saxParserFactory.newSAXParser();
                XMLReader xmlReader = saxParser.getXMLReader();

                // Establish the SAXSource
                SAXSource saxSource = new SAXSource(xmlReader, inputSource);

                // Establish an XSL Transformer to generate the XML output.
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "no");

                // The StreamResult to capture the generated XML output.
                StreamResult streamResult = new StreamResult(outputStream);

                // Call the XSL Transformer with no stylesheet to generate
                // XML output from the parsed input.
                transformer.transform(saxSource, streamResult);
            } catch (SAXException e) {
                getLogger().error("SAXException: " + e.getMessage());
                hasError = true;
            } catch (ParserConfigurationException e) {
                getLogger().error("ParserConfigurationException: " + e.getMessage());
                hasError = true;
            } catch (TransformerConfigurationException e) {
                getLogger().error("TransformerConfigurationException: " + e.getMessage());
                hasError = true;
            } catch (TransformerException e) {
                getLogger().error("TransformerException: " + e.getMessage());
                hasError = true;
            } catch (Exception e) {
                getLogger().error("Exception: " + e.getMessage());
                hasError = true;
            }
        }));

    	if (hasError) {
    		session.transfer(flowFile, REL_FAILURE);
    		session.remove(flowFileClone);
    	} else {
            session.transfer(flowFileClone, REL_SUCCESS);
            session.remove(flowFile);            		
    	}
    	hasError = false;
        session.commit();
    }
}
