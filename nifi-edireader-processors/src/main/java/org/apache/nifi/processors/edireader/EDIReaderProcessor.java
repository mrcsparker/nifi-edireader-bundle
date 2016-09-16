package org.apache.nifi.processors.edireader;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.StreamCallback;
import org.apache.nifi.stream.io.ByteArrayOutputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Tags({"EDI", "X12", "XML"})
@CapabilityDescription("Transform EDI X12 into XML.")
public class EDIReaderProcessor extends AbstractProcessor {

    private List<PropertyDescriptor> properties;
    private Set<Relationship> relationships;

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

        final List<PropertyDescriptor> properties = new ArrayList<>();
        this.properties = properties;

        Set<Relationship> relationships = new HashSet<>();
        relationships.add(REL_SUCCESS);
        relationships.add(REL_FAILURE);
        this.relationships = Collections.unmodifiableSet(relationships);
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

        InputStream input = session.read(flowFile);
        InputSource inputSource = new InputSource(input);

        OutputStream output = new ByteArrayOutputStream();

        try {

            // Establish an XMLReader which is actually an EDIReader.
            System.setProperty("javax.xml.parsers.SAXParserFactory","com.berryworks.edireader.EDIParserFactory");
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();

            // Establish the SAXSource
            SAXSource saxSource = new SAXSource(xmlReader, inputSource);

            // Establish an XSL Transformer to generate the XML output.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // The StreamResult to capture the generated XML output.
            StreamResult streamResult = new StreamResult(output);

            // Call the XSL Transformer with no stylesheet to generate
            // XML output from the parsed input.
            transformer.transform(saxSource, streamResult);


            FlowFile updatedFlowfile = session.write(flowFile, new StreamCallback() {
                @Override
                public void process(InputStream inputStream, OutputStream outputStream) throws IOException {
                    outputStream.write(output.toString().getBytes());
                }
            });

            input.close();
            output.close();

            session.transfer(updatedFlowfile, REL_SUCCESS);
            session.commit();

        } catch (SAXException e) {
            getLogger().error("SAXException: " + e.getMessage());
            session.transfer(flowFile, REL_FAILURE);
        } catch (ParserConfigurationException e) {
            getLogger().error("ParserConfigurationException: " + e.getMessage());
            session.transfer(flowFile, REL_FAILURE);
        } catch (TransformerConfigurationException e) {
            getLogger().error("TransformerConfigurationException: " + e.getMessage());
            session.transfer(flowFile, REL_FAILURE);
        } catch (TransformerException e) {
            getLogger().error("TransformerException: " + e.getMessage());
            e.printStackTrace();
            session.transfer(flowFile, REL_FAILURE);
        } catch (IOException e) {
            getLogger().error("IOException: " + e.getMessage());
            session.transfer(flowFile, REL_FAILURE);
        }
    }
}
