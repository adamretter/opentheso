/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mom.trd.opentheso.filters;

import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mom.trd.opentheso.SelectedBeans.CurrentUser2;
import mom.trd.opentheso.SelectedBeans.SelectedCandidat;
import mom.trd.opentheso.SelectedBeans.Connexion;

public class loginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        CurrentUser2 session = (CurrentUser2) req.getSession().getAttribute("currentUser");
        String url = req.getRequestURI();
       
        Connexion conn=(Connexion)request.getServletContext().getAttribute("poolConnexion");
         HikariDataSource hds;
         
        
       if(url.contains("install.xhtml") || url.contains("no_connection")){
         chain.doFilter(request, response);
         return;
       }
       else{
            if(conn!=null){
                hds=(HikariDataSource)conn.getPoolConnexion();
                if(hds==null){
                  resp.sendRedirect("./admin/no_connection.xhtml");
                  return;
                }
            }
            if(conn==null){
                resp.sendRedirect("./admin/no_connection.xhtml");
                return;
            }
       }
       /* if(req.getRequestedSessionId()==null || !req.isRequestedSessionIdValid()){
            req.getSession(true);
            resp.sendRedirect("/index.xhtml");
       
        }*/
        if(session != null) {
//            resp.sendRedirect("./install.xhtml");
            if(session.getConnect().getPoolConnexion() == null) {
                return;
            }
        }
        
        if(url.contains("deco.xhtml")) {
            CurrentUser2 temp = (CurrentUser2)req.getSession().getAttribute("currentUser");
            
            temp.setUser(null);
            temp.setIsLogged(false);
            req.getSession().setAttribute("currentUser", temp);
            
            ((SelectedCandidat)req.getSession().getAttribute("selectedCandidat")).reInit();
            
            resp.sendRedirect("./index.xhtml");//req.getServletContext().getContextPath());
        }
        else if(session == null || !session.isLogged()) {
            
            if(url.contains("conf.xhtml") || url.contains("gestCandidat.xhtml") || url.contains("edition.xhtml") || url.contains("statistic.xhtml")) {
                resp.sendRedirect(req.getServletContext().getContextPath());
            } else {
               
                try {
                    chain.doFilter(request, response);
                } catch (IOException | ServletException e ) {
                    System.out.println("Je passe par le catch");
                    System.out.println(e.toString());
                }
                 

               
            }
        } else {
            if(url.contains("connect.xhtml")) {
                resp.sendRedirect(req.getServletContext().getContextPath());
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
        
    }
    
}
