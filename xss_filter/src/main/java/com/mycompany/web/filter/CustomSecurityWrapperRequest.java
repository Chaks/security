/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.web.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.filters.SecurityWrapperRequest;

/**
 *
 * Credits to
 * http://stackoverflow.com/questions/10210645/http-servlet-request-lose-params-from-post-body-after-read-it-once
 * for MultiReadHttpServletRequest
 *
 * @author Chakravarthi
 */
public class CustomSecurityWrapperRequest extends SecurityWrapperRequest {

  private ByteArrayOutputStream cachedBytes;

  public CustomSecurityWrapperRequest(HttpServletRequest request) {
    super(request);
  }

  @Override
  public String getParameter(String name, boolean allowNull, int maxLength, String regexName) {
    String orig = getHttpServletRequest().getParameter(name);
    String clean = null;
    try {
      clean = ESAPI.validator().getValidInput("HTTP parameter name: " + name, orig, regexName, maxLength, allowNull);
    } catch (ValidationException e) {
      throw new IntrusionException("Intrusion detected", "Throwing sorry/error page, " + e.getMessage());
    }
    return clean;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (cachedBytes == null) {
      cacheInputStream();
    }
    return new CachedServletInputStream();
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  private HttpServletRequest getHttpServletRequest() {
    return (HttpServletRequest) super.getRequest();
  }

  private void cacheInputStream() throws IOException {
    /* Cache the inputstream in order to read it multiple times. For 
     * convenience, I use apache.commons IOUtils
     */
    cachedBytes = new ByteArrayOutputStream();
    IOUtils.copy(super.getInputStream(), cachedBytes);
  }

  /* An inputstream which reads the cached request body */
  public class CachedServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream input;

    public CachedServletInputStream() {
      /* create a new input stream from the cached request body */
      input = new ByteArrayInputStream(cachedBytes.toByteArray());
    }

    @Override
    public int read() throws IOException {
      return input.read();
    }
  }
}
