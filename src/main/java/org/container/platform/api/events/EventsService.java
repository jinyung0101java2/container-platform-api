package org.container.platform.api.events;

import org.container.platform.api.common.CommonService;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Events Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.25
 */
@Service
public class EventsService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new Events service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public EventsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * Resource 의 Events 목록 조회(Get Events List)
     *
     * @param params the params
     * @return the events list
     */
    public EventsList getEventsList(Params params) {
        String reqUrl = generateEventsListUrl(params);
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API, reqUrl, HttpMethod.GET, null, Map.class, params);
        EventsList eventsList = commonService.setResultObject(responseMap, EventsList.class);
        // 이벤트 메세지 최신 10개만 limit
        params.setLimit(10);
        params.setOffset(0);
        eventsList = commonService.resourceListProcessing(eventsList, params, EventsList.class);
        return (EventsList) commonService.setResultModel(eventsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 특정 Namespace 의 전체 Events Admin 목록 조회(Get Events Admin list in a Namespace)
     *
     * @param params the params
     * @return the events list
     */
    public Object getNamespaceEventsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API, propertyService.getCpMasterApiListEventsListUrl(),
                HttpMethod.GET, null, Map.class, params);
        EventsList eventsList = commonService.setResultObject(responseMap, EventsList.class);

        // 이벤트 메세지 최신 10개만 limit
        params.setLimit(10);
        params.setOffset(0);
        eventsList = commonService.resourceListProcessing(eventsList, params, EventsList.class);
        return commonService.setResultModel(eventsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Node 와 타 리소스의 Event 목록 조회 Endpoint 구분 (Separate Endpoints from Nodes and Other Resources)
     *
     * @param params the params
     * @return the String
     */
    public String generateEventsListUrl(Params params) {
        String url = propertyService.getCpMasterApiListEventsListUrl() + "?fieldSelector=involvedObject.uid=" + params.getResourceUid();
        if (!params.getType().equals(Constants.EMPTY_STRING)) {
            // node
            params.setNamespace(Constants.EMPTY_STRING);
            url = propertyService.getCpMasterApiListEventsListAllNamespacesUrl() + "?fieldSelector=involvedObject.name=" + params.getResourceUid();
        }
        return url;
    }

}
