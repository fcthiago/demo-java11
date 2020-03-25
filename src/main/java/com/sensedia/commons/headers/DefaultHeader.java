package com.sensedia.commons.headers;

import java.util.HashMap;

public class DefaultHeader extends HashMap {

  public static final String HEADER_EVENT_NAME = "event_name";
  public static final String HEADER_APP_ID = "appId";
  public static final String HEADER_CONTENT_RANGE = "content-range";
  public static final String HEADER_ACCEPT_RANGE = "accept-range";

  public DefaultHeader appId(String appId) {
    put(HEADER_APP_ID, appId);
    return this;
  }

  public DefaultHeader eventName(String eventName) {
    put(HEADER_EVENT_NAME, eventName);
    return this;
  }

  public DefaultHeader contentRange(int contentRange) {
    put(HEADER_CONTENT_RANGE, contentRange);
    return this;
  }

  public DefaultHeader acceptRange(int acceptRange) {
    put(HEADER_ACCEPT_RANGE, acceptRange);
    return this;
  }
}
