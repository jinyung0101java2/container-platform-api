package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;

/**
 * DatabaseCredentials 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.09.27
 **/
@Data
public class DatabaseCredentials {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String lease_id;
    private String lease_duration;
    private String default_ttl;
    private String max_ttl;
    private String plugin_name;
    private DatabaseCredentialsData data;
    private String application;
    private String namespace;
    private String status;
    private String flag;
}
