package org.container.platform.api.clusters.namespaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.common.CommonUtils;
import org.container.platform.api.common.model.CommonAnnotations;
import org.container.platform.api.common.model.CommonMetaData;
import org.container.platform.api.common.model.CommonSpec;
import org.container.platform.api.common.model.CommonStatus;

import java.util.List;

/**
 * Namespaces Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class Namespaces {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String name;
    private String uid;
    private Object labels;
    private List<CommonAnnotations> annotations;
    private String namespaceStatus;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private CommonStatus status;

    public String getName() {
        return metadata.getName();
    }

    public String getUid() {
        return metadata.getUid();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getNamespaceStatus() {
        return status.getPhase();
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }
}
