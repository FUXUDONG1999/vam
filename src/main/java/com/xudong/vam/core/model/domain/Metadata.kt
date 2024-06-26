package com.xudong.vam.core.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {
    private String licenseType;

    private String creatorName;

    private String packageName;

    private String description;

    private String credits;

    private String instructions;

    private String promotonalLink;

    private String programVersion;

    private String standardReferenceVersionOption;

    private List<String> contentList;

    private Map<String, Metadata> dependencies;
}
