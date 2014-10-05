/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.web.filter;

import com.google.json.JsonSanitizer;
import com.tcs.web.filter.RequestUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.owasp.esapi.errors.IntrusionException;

/**
 *
 * @author Chakravarthi
 */
@WebFilter(filterName = "SecureRequestFilter", urlPatterns = {"/*"},
        initParams = @WebInitParam(name = "intrusionErrorPage", value = "/WEB-INF/intrusionError.jsp"))
public class SecureRequestFilter implements Filter {

  private static final boolean debug = true;
  private FilterConfig filterConfig = null;

  public SecureRequestFilter() {
  }

  public void doBeforeProcessing(ServletRequest request, ServletResponse response)
          throws IOException, ServletException {
    if (debug) {
      log("SecureRequestFilter:DoBeforeProcessing");
    }
  }

  public void doAfterProcessing(ServletRequest request, ServletResponse response)
          throws IOException, ServletException {
    if (debug) {
      log("SecureRequestFilter:DoAfterProcessing");
    }
  }

  /**
   *
   * @param request The servlet request we are processing
   * @param response The servlet response we are creating
   * @param chain The filter chain we are processing
   *
   * @exception IOException if an input/output error occurs
   * @exception ServletException if a servlet error occurs
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
          FilterChain chain)
          throws IOException, ServletException {

    if (debug) {
      log("SecureRequestFilter:doFilter()");
    }

    doBeforeProcessing(request, response);

    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    //System.out.println("getContextPath=> " + httpServletRequest.getRequestURI());

    request.setAttribute("validationDone", "true");
    CustomSecurityWrapperRequest customSecurityWrapperRequest = new CustomSecurityWrapperRequest(httpServletRequest);
    customSecurityWrapperRequest.setAllowableContentRoot("WEB-INF");

    //Handle for POST JSON data
    if ("POST".equalsIgnoreCase(customSecurityWrapperRequest.getMethod())) {
      String postData = IOUtils.toString((InputStream) customSecurityWrapperRequest.getInputStream(), "UTF-8");
      log("[postData] " + postData);
      if (customSecurityWrapperRequest.getContentType().startsWith("application/json")) {
        //Sanitize JSON and check for any potential XSS
        String wellFormedJson = JsonSanitizer.sanitize(postData);
        List<String> jsonAttributeValues = extractJsonValues(wellFormedJson);

        RequestUtil requestUtil = new RequestUtil(customSecurityWrapperRequest);
        for (String jsonAttributeValue : jsonAttributeValues) {
          requestUtil.validateHTTPParameterValue(jsonAttributeValue, null);
        }
      }
    }

    Throwable problem = null;
    try {
      chain.doFilter(customSecurityWrapperRequest, response);
    } catch (IOException ioe) {
      problem = ioe;
    } catch (ServletException se) {
      problem = se;
    } catch (IntrusionException ie) {
      log("Intrusion detected, throwing sorry/error page");
      problem = ie;
    }

    doAfterProcessing(customSecurityWrapperRequest, response);

    if (problem != null) {
      if (problem instanceof ServletException) {
        throw (ServletException) problem;
      }
      if (problem instanceof IOException) {
        throw (IOException) problem;
      }
      RequestDispatcher requestDispatcher = customSecurityWrapperRequest.getRequestDispatcher(
              filterConfig.getInitParameter("intrusionErrorPage"));
      requestDispatcher.forward(customSecurityWrapperRequest, response);
      //sendProcessingError(problem, response);
    }
  }

  /**
   * Return the filter configuration object for this filter.
   *
   * @return
   */
  public FilterConfig getFilterConfig() {
    return (this.filterConfig);
  }

  /**
   * Set the filter configuration object for this filter.
   *
   * @param filterConfig The filter configuration object
   */
  public void setFilterConfig(FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
  }

  /**
   * Destroy method for this filter
   */
  @Override
  public void destroy() {
  }

  /**
   * Init method for this filter
   *
   * @param filterConfig
   */
  @Override
  public void init(FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
    if (filterConfig != null) {
      if (debug) {
        log("SecureRequestFilter:Initializing filter");
      }
    }
  }

  /**
   * Return a String representation of this object.
   */
  @Override
  public String toString() {
    if (filterConfig == null) {
      return ("SecureRequestFilter()");
    }
    StringBuilder sb = new StringBuilder("SecureRequestFilter(");
    sb.append(filterConfig);
    sb.append(")");
    return (sb.toString());
  }

  public void log(String msg) {
    filterConfig.getServletContext().log(msg);
  }

  private List<String> extractJsonValues(String jsonString) {
    List<String> valueList = new ArrayList<String>();

    int jsonLength = jsonString.length();
    boolean mark = false;
    boolean unmark = false;
    StringBuilder valueBuilder = new StringBuilder();
    for (int i = 0; i < jsonLength; i++) {
      char ch = jsonString.charAt(i);
      switch (ch) {
        case ':':
          mark = true;
          valueBuilder.setLength(0);
          break;
        case ',':
          if (mark) {
            unmark = true;
          }
          break;
        case '}':
          if (mark) {
            unmark = true;
          }
          break;
        default:
          break;
      }

      if (mark) {
        valueBuilder.append(ch);
      }

      if (mark && unmark) {
        valueList.add(valueBuilder.toString().replace(",", "").replace(":", "").replace("{", "").replace("}", "").replace("[", "").replace("]", ""));
        valueBuilder.setLength(0);
        mark = false;
        unmark = false;
      }
    }

    log("[valueList] " + valueList);
    return valueList;
  }
}
