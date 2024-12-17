package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;
import org.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;

/**
 * DatabaseConnections 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.04
 **/
@Data
public class DatabaseInfoList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List items;
}
