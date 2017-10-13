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
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

        final List<PropertyDescriptor> properties = new ArrayList<>();
        this.properties = properties;

        Set<Relationship> relationships = new HashSet<>();
        relationships.add(REL_ORIGINAL);
        relationships.add(REL_SPLIT);
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
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        final FlowFile original = session.get();
        if (original == null) {
            return;
        }

        InputStream input = session.read(original);
        final List<FlowFile> splits = new ArrayList<>();
        Splitter ediSplitter = new Splitter(input);

        try {
            final AtomicInteger numberOfRecords = new AtomicInteger(0);
            for (Map<String, String> ediSplits : ediSplitter.split()) {
                for (Map.Entry<String, String> entry : ediSplits.entrySet()) {
                    FlowFile split = session.create(original);
                    split = session.write(split, out -> out.write(entry.getValue().getBytes("UTF-8")));
                    split = session.putAttribute(split, "fragment.identifier", entry.getKey());
                    split = session.putAttribute(split, "fragment.index", Integer.toString(numberOfRecords.getAndIncrement()));
                    split = session.putAttribute(split, "segment.original.filename", split.getAttribute(CoreAttributes.FILENAME.key()));
                    splits.add(split);
                }
            }

            input.close();

            splits.forEach((split) -> {
                split = session.putAttribute(split, "fragment.count", Integer.toString(numberOfRecords.get()));
                session.transfer(split, REL_SPLIT);
            });

            session.transfer(original, REL_ORIGINAL);
            session.commit();
            getLogger().info("Split {} into {} FlowFiles", new Object[]{original, splits.size()});

        } catch (IOException e) {
            getLogger().error("IOException: " + e.getMessage());
            session.transfer(original, REL_FAILURE);
        }
    }
}
