package org.container.platform.api.users;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.api.clusters.namespaces.NamespacesService;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.exception.ResultStatusException;
import org.container.platform.api.users.serviceAccount.ServiceAccountList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * User Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.25
 **/
@Api(value = "UsersController v1")
@RestController
public class UsersController {
    @Value("${cpNamespace.defaultNamespace}")
    private String defaultNamespace;

    private final UsersService usersService;
    private final NamespacesService namespacesService;
    private final CommonService commonService;
    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;

    @Autowired
    public UsersController(UsersService usersService, NamespacesService namespacesService, CommonService commonService, RestTemplateService restTemplateService, PropertyService propertyService) {
        this.usersService = usersService;
        this.namespacesService = namespacesService;
        this.commonService = commonService;
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
    }


    /**
     * Users 전체 목록 조회(Get Users List)
     *
     * @param params the params
     * @return the users list
     */
    @ApiOperation(value = "Users 전체 목록 조회(Get Users list)", nickname = "getUsersList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/usersList")
    public Object getUsersList(Params params) {
        if (params.getType().equalsIgnoreCase(Constants.SELECTED_ADMINISTRATOR)) {
            return usersService.getClusterAdminListByCluster(params);
        }
        return usersService.getUsersAllByCluster(params);
    }


    /**
     * 특정 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users cluster namespace)
     *
     * @param params the params
     * @return the users detail
     */
    @ApiOperation(value = "하나의 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users cluster namespace)", nickname = "getUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/clusters/{cluster:.+}/users/{userAuthId:.+}")
    public UsersDetails getUsers(Params params) throws Exception {
        return usersService.getUsersDetailsByCluster(params);
    }


    /**
     * 하나의 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users Access Info)
     *
     * @param params the params
     * @return the users detail
     */
    @ApiOperation(value = "하나의 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users Access Info)", nickname = "getUsersAccessInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/accessesInfo")
    public Object getUsersAccessInfo(Params params) throws Exception {
        return usersService.getUsersAccessInfo(params);
    }


    /**
     * Users 수정(Update Users)
     *
     * @param params the params
     * @return return is succeeded
     */
    @ApiOperation(value = "Users 수정(Update Users)", nickname = "modifyUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @PutMapping(value = "/clusters/{cluster:.+}/users/{userAuthId:.+}")
    public Object modifyUsers(Params params, @RequestBody Users users) throws Exception {
        if(users.getUserType().equalsIgnoreCase(Constants.AUTH_CLUSTER_ADMIN)) {
            return usersService.modifyToClusterAdmin(params, users);
        }
        return usersService.modifyToUser(params, users);
    }



    /**
     * Users 와 맵핑된 Clusters 목록 조회(Get Clusters List Used By User)
     *
     * @return the users list
     */
    @ApiOperation(value = "Users 와 맵핑된 Clusters 목록 조회(Get Clusters List Used By User)", nickname = "getClustersListByUserOwns")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/users/clustersList")
    public UsersList getClustersListByUserOwns(Params params) {
        return usersService.getMappingClustersListByUser(params);
    }


    /**
     * Users 와 맵핑된 Namespaces 목록 조회(Get Namespaces List Used By User)
     *
     * @return the users list
     */
    @ApiOperation(value = "Users 와 맵핑된 Namespaces 목록 조회(Get Namespaces List Used By User)", nickname = "getNamespacesListByUserOwns")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/clusters/{cluster:.+}/users/namespacesList")
    public UsersList getNamespacesListByUserOwns(Params params) {
        try{
            params.setIsClusterToken(true);
            restTemplateService.sendPing(Constants.TARGET_CP_MASTER_API, ResultStatus.class, params); // check ping
        }catch (Exception e) {
            throw new ResultStatusException(MessageConstant.UNABLE_TO_COMMUNICATE_K8S_API_SERVER.getMsg());
        }

        String userAuthority = commonService.getClusterAuthorityFromContext(params.getCluster());

        if (Constants.AUTH_ADMIN_LIST.contains(userAuthority)) {
            return namespacesService.getMappingNamespacesListByAdmin(params);
        }
        return usersService.getMappingNamespacesListByUser(params);
    }

    /**
     * Users 와 맵핑되고 컨테이너 플랫폼 운영 Namespaces 제외된 Namespaces 목록 조회(Get a list of namespaces that map to Users and exclude container platform operational namespaces)
     *
     * @return the users list
     */
    @ApiOperation(value = "Users 와 맵핑되고 컨테이너 플랫폼 운영 Namespaces 제외된 Namespaces 목록 조회(Get a list of namespaces that map to Users and exclude container platform operational namespaces)", nickname = "getUserMappedNamespacesExcludingOperational")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/clusters/{cluster:.+}/users/nonOperationalNamespacesList")
    public UsersList getUserMappedNamespacesExcludingOperational(Params params) {
        List<String> namespacesToExclude = propertyService.getExceptNamespaceList();
        UsersList usersList = getNamespacesListByUserOwns(params);
        List<Users> filteredItems = usersList.getItems().stream()
                .filter(user -> !namespacesToExclude.contains(user.getCpNamespace()))
                .collect(Collectors.toList());
        UsersList filteredUsersList = new UsersList(filteredItems);
        return (UsersList) commonService.setResultModel(filteredUsersList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Service Accounts 목록 조회(Get Service Accounts List)
     *
     * @param params the params
     * @return the Service Accounts list
     */
    @ApiOperation(value = "Service Accounts 목록 조회(Get Service Accounts List)", nickname = "getServiceAccountsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)
    })
    @GetMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/serviceAccountsList")
    public ServiceAccountList getServiceAccountsList(Params params) {

        return usersService.getServiceAccountsList(params);
    }
}