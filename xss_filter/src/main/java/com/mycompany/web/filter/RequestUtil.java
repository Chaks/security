/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcs.web.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;

/**
 *
 * @author Chakravarthi
 */
public class RequestUtil {

  private final HttpServletRequest httpServletRequest;
  private Map<String, String> requestParamMap = null;

  public RequestUtil(HttpServletRequest httpServletRequest) {
    this.httpServletRequest = httpServletRequest;
  }

  public String getParameter(String paramName) {
    String paramValue = "";
    if ("GET".equals(httpServletRequest.getMethod())) {
      paramValue = httpServletRequest.getParameter(paramName);
    } else if ("POST".equals(httpServletRequest.getMethod())) {
      if (requestParamMap == null) {
        prepareRequestParamMap();
      }
      if (requestParamMap.containsKey(paramName)) {
        return getParameter(paramName, true, 2000, "HTTPParameterValue");
      }
    }

    return paramValue;
  }

  public void validateHTTPParameterValue(String paramValue, String regExKey) {
    if (regExKey == null || "".equals(regExKey)) {
      regExKey = "HTTPParameterValue";
    }
    try {
      ESAPI.validator().getValidInput("HTTP parameter value: " + paramValue, paramValue, regExKey, 2000, true);
    } catch (ValidationException ex) {
      Logger.getLogger(RequestUtil.class.getName()).log(Level.SEVERE, null, ex);
      throw new IntrusionException("Intrusion detected", "Throwing sorry/error page, " + ex.getMessage());
    }
  }

  private void prepareRequestParamMap() {
    String postData = getPostData();
    if (postData != null && !"".equals(postData)) {
      requestParamMap = new HashMap<String, String>();
      String[] params = postData.split("&");
      for (String param : params) {
        String[] nameValuePair = param.split("=");
        if (nameValuePair[0] != null && nameValuePair[1] != null) {
          requestParamMap.put(nameValuePair[0], nameValuePair[1]);
        }
      }
    }
    Logger.getLogger(RequestUtil.class.getName()).log(Level.INFO, requestParamMap.toString());
  }

  private String getPostData() {
    String postData;
    try {
      postData = IOUtils.toString((InputStream) httpServletRequest.getInputStream(), "UTF-8");
    } catch (IOException ex) {
      postData = "";
      Logger.getLogger(RequestUtil.class.getName()).log(Level.SEVERE, null, ex);
    }

    Logger.getLogger(RequestUtil.class.getName()).log(Level.INFO, "[postData] {0}", postData);
    return postData;
  }

  private String getParameter(String name, boolean allowNull, int maxLength, String regexName) {
    String orig = requestParamMap.get(name);
    String clean = null;
    try {
      clean = ESAPI.validator().getValidInput("HTTP parameter name: " + name, orig, regexName, maxLength, allowNull);
    } catch (ValidationException e) {
      throw new IntrusionException("Intrusion detected", "Throwing sorry/error page, " + e.getMessage());
    }
    return clean;
  }
}
