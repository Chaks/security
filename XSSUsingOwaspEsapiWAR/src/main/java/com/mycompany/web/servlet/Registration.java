/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.web.servlet;

import com.tcs.web.filter.RequestUtil;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Chakravarthi
 */
@WebServlet(name = "Registration", urlPatterns = {"/portal/Registration"})
public class Registration extends HttpServlet {

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
    //out.println("Name: " + Sanitizers.FORMATTING.sanitize(request.getParameter("name")));
    //out.println("Name: " + ESAPI.validator().getValidSafeHTML("registration.name", request.getParameter("name"), 25, true));
    RequestUtil requestUtil = new RequestUtil(request);

    request.setAttribute("name", requestUtil.getParameter("name"));
    request.setAttribute("occupation", requestUtil.getParameter("occupation"));
    request.setAttribute("age", requestUtil.getParameter("age"));
    request.setAttribute("address", requestUtil.getParameter("address"));

    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/registration.jsp");
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
