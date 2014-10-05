/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.web.servlet;

import com.tcs.web.filter.RequestUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.reference.DefaultEncoder;

/**
 *
 * @author Chakravarthi
 */
@WebServlet(name = "Registration3", urlPatterns = {"/portal2/Registration3"})
public class Registration3 extends HttpServlet {

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
   * methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");
    /* TODO output your page here. You may use following sample code. */
    List<String> codecs = new ArrayList<String>();
    codecs.add("org.owasp.esapi.codecs.HTMLEntityCodec");
    //codecs.add("org.owasp.esapi.codecs.PercentCodec");
    //codecs.add("org.owasp.esapi.codecs.JavaScriptCodec");
    DefaultEncoder defaultEncoder = new DefaultEncoder(codecs);

    RequestUtil requestUtil = new RequestUtil(request);

    request.setAttribute("nameEncoded", defaultEncoder.encodeForHTML(requestUtil.getParameter("name")));
    request.setAttribute("name", requestUtil.getParameter("name"));
    request.setAttribute("occupation", requestUtil.getParameter("occupation"));
    request.setAttribute("age", requestUtil.getParameter("age"));
    request.setAttribute("address", requestUtil.getParameter("address"));

    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/registration3.jsp");
    requestDispatcher.forward(request, response);
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>

}
