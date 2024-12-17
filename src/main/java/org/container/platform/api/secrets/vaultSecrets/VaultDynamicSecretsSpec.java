package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;

/**
 * DatabaseConnections 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.04
 **/
@Data
public class VaultDynamicSecretsSpec {
    private String path;

}
