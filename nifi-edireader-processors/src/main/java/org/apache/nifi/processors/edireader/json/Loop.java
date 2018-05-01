package org.apache.nifi.processors.edireader.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Loop {
    private String id;
    @JsonIgnore
    private transient Loop parentLoop;
    private List<HashMap<String, String>> segments = new ArrayList<>();
    private List<Loop> loops = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Loop getParentLoop() {
        return parentLoop;
    }

    public void setParentLoop(Loop parentLoop) {
        this.parentLoop = parentLoop;
    }
}
