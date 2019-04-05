package org.terrence.testapp;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;

@JsonWriteNullProperties(false)
@JsonIgnoreProperties({"id", "revision"})
public class Status {

    @JsonProperty("_id")
    private String id;
    @JsonProperty("_rev")
    private String revision;
    private String msg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRevision() {
        return revision;
    }
}