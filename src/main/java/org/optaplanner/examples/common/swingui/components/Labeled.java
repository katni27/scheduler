package org.optaplanner.examples.common.swingui.components;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Labeled {

    @JsonIgnore
    String getLabel();

}
