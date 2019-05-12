package org.apache.nifi.processors.edireader;

import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.behavior.SupportsBatching;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
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
import org.apache.nifi.processors.edireader.split.Splitter;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventDriven
@SideEffectFree
@SupportsBatching
@Tags({"EDI", "X12", "Split"})
@InputRequirement(InputRequirement.Requirement.INPUT_REQUIRED)
@CapabilityDescription("Split EDI X12 Files.")
@WritesAttributes({
        @WritesAttribute(attribute = "fragment.identifier",
                description = "All split FlowFiles produced from the same parent FlowFile will have the same randomly generated UUID added for this attribute"),
        @WritesAttribute(attribute = "fragment.index",
                description = "A one-up number that indicates the ordering of the split FlowFiles that were created from a single parent FlowFile"),
        @WritesAttribute(attribute = "fragment.count",
                description = "The number of split FlowFiles generated from the parent FlowFile"),
        @WritesAttribute(attribute = "segment.original.filename ", description = "The filename of the parent FlowFile")
})
public class SplitEdi extends AbstractProcessor {

    private List<PropertyDescriptor> properties;
    private Set<Relationship> relationships;
    private boolean hasError = false;

    public static final Relationship REL_ORIGINAL = new Relationship.Builder()
            .name("original")
            .description("The original FlowFile that was split into segments. If the FlowFile fails processing, nothing will be sent to this relationship")
            .build();
    public static final Relationship REL_SPLIT = new Relationship.Builder()
            .name("split")
            .description("All segments of the original FlowFile will be routed to this relationship")
            .build();

    private static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("failure")
            .build();

    @Override
    public void init(final ProcessorInitializationContext context) {

        this.properties = new ArrayList<>();

        Set<Relationship> relationshipSet = new HashSet<>();
        relationshipSet.add(REL_ORIGINAL);
        relationshipSet.add(REL_SPLIT);
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
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        final FlowFile original = session.get();
        if (original == null) {
            return;
        }

        List<FlowFile> splitFlowFiles = new ArrayList<>();

        try (InputStream input = session.read(original)) {
            Splitter ediSplitter = new Splitter(input);
            splitFlowFiles = ediSplitter.splitData(session, original);
        } catch (IOException e) {
            getLogger().error("IOException: " + e.getMessage());
            hasError = true;
        } catch (Exception e) {
            getLogger().error("Exception: " + e.getMessage());
            hasError = true;
        }

        if (hasError) {
            session.transfer(original, REL_FAILURE);
            session.remove(splitFlowFiles);
        } else {
            session.transfer(splitFlowFiles, REL_SPLIT);
            session.transfer(original, REL_ORIGINAL);
        }
        hasError = false;
        session.commit();
    }
}
