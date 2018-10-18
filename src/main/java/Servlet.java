
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;
import edu.virginia.cs.object.Query;
import edu.virginia.cs.model.LanguageModel;
import edu.virginia.cs.model.LoadLanguageModel;
import edu.virginia.cs.utility.Util;
import edu.virginia.cs.model.IntentAwarePrivacy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.*;

/**
 *
 * @author aceni
 */
@WebServlet(name = "QueryGeneratorServlet", urlPatterns = { "QueryGenerator" })
public class Servlet extends HttpServlet {

    IntentAwarePrivacy IAP;

    @Override
    public void init() {
        // do initialization
        // notice the path string
        // String path = System.getenv("QG_PATH");
        ServletContext context = getServletContext();
        String path = context.getRealPath("/WEB-INF/data");
        System.out.println(path);
        String ReferencePath = path + "/data/Reference-Model";
        String dmozPath = path + "/lucene-DMOZ-index";
        String docPath = path + "/data/ODP-doc-content.xml";
        String idfPath = path + "/AOL-Dictionary";

        HashMap<String, Double> refModel = Util.loadRefModel(ReferencePath);
        LoadLanguageModel llm = new LoadLanguageModel(refModel, false, false);
        llm.loadModels(docPath, 4);
        ArrayList<LanguageModel> langModels = llm.getLanguageModels();
        IAP = new IntentAwarePrivacy(langModels, refModel, dmozPath);
        IAP.setTokenizer(false, false);
        IAP.loadIDFRecord(idfPath);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        if (request.getMethod().equals("GET")) {

        } else if (request.getMethod().equals("POST")) {
            // get parameters from POST request
            // timestamp is generated here
            Date dNow = new Date();
            String time = Util.convertDateToString(dNow);

            String action = request.getParameter("action");
            if (action.equals("R")) {
                // register user
                String uid = request.getParameter("uid");
                JDBC.registerUser(uid);
            } else if (action.equals("U")) {
                System.out.println("Action is RE-RANK::::::");

                // re-rank according to sinppets received
                String[] jsonData = request.getParameterValues("json[]");
                String uid = request.getParameter("uid");
                String userProfile = JDBC.getProfile(uid);

                Integer[] arr = IAP.reRankDocuments(jsonData, userProfile);
                List<Integer> intList = Arrays.asList(arr);
                String json = new Gson().toJson(intList);
                response.getWriter().write(json);
                System.out.println("::::::RE-RANK SUCCESS");
            } else if (action.equals("UC")) {
                System.out.println("Action is USER CLICK::::::");
                String uid = request.getParameter("uid");
                String snippet = request.getParameter("snip");
                String query = request.getParameter("query");
                int idx = Integer.valueOf(request.getParameter("click"));
                String url = request.getParameter("url");
                String title = request.getParameter("content");

                // save user clicks
                JDBC.saveClick(uid, url, title, query, 1, idx, time);
                // update user profile using click
                String profile = JDBC.getProfile(uid);
                String newProfile = IAP.updateProfileUsingClick(snippet, profile);
                JDBC.saveProfile(uid, newProfile);
                System.out.println("::::::USER CLICK SUCCESS");
            } else if (action.equals("SC")) {
                System.out.println("Action is SIMULATED CLICK::::::");
                String uid = request.getParameter("uid");
                String query = request.getParameter("query");
                int idx = Integer.valueOf(request.getParameter("click"));
                String url = request.getParameter("url");
                String title = request.getParameter("content");

                // save simulated clicks
                JDBC.saveClick(uid, url, title, query, 0, idx, time);
                System.out.println("::::::SIMULATED CLICK SUCCESS");
            } else if (action.equals("Q")) {

                System.out.println("Action is QUERY::::::");

                String uid = request.getParameter("uid");
                String query = request.getParameter("query");
                int numCover = Integer.parseInt(request.getParameter("numcover")) - 1;
                Query curQuery = IAP.getQueryTopic(query);
                ArrayList<Query> coverQueries;
                int sessionNo = 0;
                int actionNo = 0;
                String sentToPython = "null";
                String pythonQuery = null;
                Query pQuery = new Query();
                Map<String, String> map = new LinkedHashMap<>();

                // get java cover queries
                if (JDBC.getPreviousCoverQueryData(uid) == null) {
                    // first query ever
                    coverQueries = IAP.getCoverQueries(curQuery, numCover);
                    //
                } else {
                    // exists action before
                    QueryData qd = JDBC.getPreviousCoverQueryData(uid);
                    actionNo = qd.getActionID() + 1;
                    Query prevQuery = qd.getUserQuery();
                    Date previousTime = Util.convertStringToDate(qd.getTime());
                    if (Util.checkSameSession_time(previousTime, dNow)) {
                        // same session
                        sessionNo = qd.getSessionID();
                        int sequentialEdited = IAP.checkSequentialEdited(curQuery, prevQuery);
                        if (sequentialEdited == 0) {
                            coverQueries = IAP.getCoverQueries(curQuery, numCover);
                        } else {
                            coverQueries = IAP.getCoverQueriesIfSeqEdited(curQuery, qd.getCoverQueryList(), numCover,
                                    sequentialEdited);
                        }
                        sentToPython = qd.getPythonQuery().getQueryText();
                    } else {
                        // different session
                        coverQueries = IAP.getCoverQueries(curQuery, numCover);
                        sessionNo = qd.getSessionID() + 1;
                    }
                }

                // get query from python program
                try {
                    pythonQuery = JDBC.getCover(sentToPython);
                    pQuery = IAP.getQueryTopic(pythonQuery);
                } catch (Exception ex) {
                    Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
                }

                // respond to users as soon as possible
                try {
                    for (Query q : coverQueries) {
                        map.put(q.getQueryText(), q.getQueryTopic());
                    }
                } catch (NullPointerException e) {
                    System.out.println("    No java queries generated");
                }
                if (null != curQuery.getQueryTopic()) {
                    map.put("input", curQuery.getQueryTopic());
                }
                if (null != pQuery.getQueryTopic()) {
                    map.put(pQuery.getQueryText(), pQuery.getQueryTopic());
                }
                String json = new Gson().toJson(map);
                response.getWriter().write(json);

                // save query to database
                if (null != curQuery.getQueryTopic()) {
                    JDBC.saveQuery(curQuery, actionNo, sessionNo, 0, uid, time);
                    for (Query q : coverQueries) {
                        JDBC.saveQuery(q, actionNo, sessionNo, 1, uid, time);
                    }
                    JDBC.saveQuery(pQuery, actionNo, sessionNo, 2, uid, time);
                }

                // update user profile using query
                String profile = JDBC.getProfile(uid);
                String newProfile = IAP.updateProfileUsingQuery(curQuery, profile);
                JDBC.saveProfile(uid, newProfile);
                System.out.println("::::::QUERY SUCCESS");
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
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
