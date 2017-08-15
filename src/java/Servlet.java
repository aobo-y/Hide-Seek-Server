/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.virginia.cs.model.GenerateCoverQuery;
import edu.virginia.cs.model.LanguageModel;
import edu.virginia.cs.model.LoadLanguageModel;
import edu.virginia.cs.user.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import java.util.Random;

/**
 *
 * @author aceni
 */
@WebServlet(urlPatterns = {"/QueryGenerator"})
public class Servlet extends HttpServlet {

    public static String[] topics = {"Arts", "Business", "Computers", 
                                    "Games", "Health", "Home",
                                    "News", "Recreation", "Reference",
                                    "Regional", "Science", "Shopping",
                                    "Society", "Sports", "Kids & Teens Directory",
                                    "World"};
    
    public static String getRandomTopic(String[] arr) {
        int idx = new Random().nextInt(arr.length);
        return arr[idx];
    }
    
    ArrayList<LanguageModel> langModels;
    GenerateCoverQuery GQ;
    String uid;
    
        @Override
	public void init() {
            //do initialization
            //notice the path string 
            String path = System.getenv("QG_PATH");
            //System.out.println(path);
            //String path = "/if15/zn8ae/QueryGenerator";
            String ReferencePath = path + "/data/Reference-Model";
            String lmPath = path + "/data/language_models/";
            String dmozPath = path + "/lucene-DMOZ-index";
            String logPath = path + "/user-search-log/";
            String docPath = path + "/data/ODP-doc-content.xml";
            
            
            LoadLanguageModel llm = new LoadLanguageModel(ReferencePath, false, false);
            llm.loadModels(docPath, 4);
            langModels = llm.getLanguageModels();
            GQ = new GenerateCoverQuery(langModels, dmozPath, logPath);
            GQ.setParameter(false, false);
	}
    
        
        
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
        
        //GET request for cover query generation
        if(request.getMethod().equals("GET")){
            //get input from client
            String input = request.getParameter("query");
            System.out.println("Query is: " + input);
            
            if("".equals(input) || input == null) {
                PrintWriter out = response.getWriter();
                JSONObject obj = new JSONObject();
                obj.put("output1","null"); 
                out.print(obj.toJSONString());
                out.flush();
                return;
            }

            //get time now or use the client input time
            //String time = request.getParameter("time");
            Date dNow = new Date( );
            SimpleDateFormat ft = 
            new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
            String time = ft.format(dNow);
            System.out.println("Time: " + time);
            //get id from client
            uid = request.getParameter("id");
            System.out.println("ID: " + uid);
             

            //get number of cover query from client
            int numcover = Integer.parseInt(request.getParameter("numcover"));

            //process input and generate result
            //use Java server to generate n-1 cover queries
            ArrayList<Query> coverQueries = GQ.generateNCoverQueries(input, time, uid, numcover - 1);
            System.out.println("GENERATED Qs from Java server: " + coverQueries + ", total count: " + coverQueries.size());

            //store the original query and cover queries into db by JDBC
            //Input: uid, time, query, tag (1 = real, 0 = cover)
            JDBC.saveQuery(uid, time, input, 1);
            for(String q: coverQueries) {
                JDBC.saveQuery(uid, time, q, 0);
            }
            
            //use Python server to generate 1 cover query
            //param: time, previous cover query generated by python server
            String cover = "null";
            //check if the user exists:
            if (JDBC.checkUser(uid)!=false) {
                //user exists, get last query and time 
                //check if within 1 hour and return last query if yes
                //otherwise returl "null"
                cover=JDBC.getPrevious(uid, time);
            } 
            
            String covQuery = "null";
            try {
                //send GET request to get cover query from python server
                covQuery = JDBC.getCover(cover);
            } catch (Exception ex) {
                Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //store cover into database and give tag 2
            JDBC.saveQuery(uid, time, covQuery, 2);
            coverQueries.add(covQuery);
            
            //print json according to the length of cover Queries
            PrintWriter out = response.getWriter();
            JSONObject obj = new JSONObject();
            int count = 1;
//            String ctStr = "output";
//            String key;
            if(!coverQueries.isEmpty()) {
                obj.put("db","success");
                for(String s:coverQueries) {
                    obj.put(s, getRandomTopic(topics)); 
                    count++;
                }
            } else {
                obj.put("output1","null"); 
            }
            //write input's topic
            obj.put("input", getRandomTopic(topics));
            
            System.out.println("total count: " + coverQueries.size());
            System.out.println(coverQueries);
            System.out.println(obj.toJSONString());
            out.print(obj.toJSONString());
            out.flush();

        } else if (request.getMethod().equals("POST")) {
            //get parameters from POST request
            
            //uid is already store locally
            //timestamp is generated here
            Date dNow = new Date( );
            SimpleDateFormat ft = 
            new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
            String time = ft.format(dNow);

            //query
            String query = request.getParameter("query");
            if(query == "" || query == null) {
                PrintWriter out = response.getWriter();
                JSONObject obj = new JSONObject();
                obj.put("output1","null"); 
                out.print(obj.toJSONString());
                out.flush();
                return;
            }

            //url
            String url = request.getParameter("url");

            //click           
            String click = request.getParameter("click");
            int clickIndex = Integer.valueOf(click);         
            
            //content
            String content = request.getParameter("content"); 
            
            uid = request.getParameter("id");
            System.out.println("INSIDE POST" + uid);

            //save Clicks table
            try {
                JDBC.saveClick(uid, time, query, url, clickIndex);
                //save URLs table
                JDBC.saveURL(url, content, query);  
            } catch(Exception e) {
                PrintWriter out = response.getWriter();
                JSONObject obj = new JSONObject(); 
                obj.put("db","error");
                out.print(obj.toJSONString());
                out.flush();
            }
            PrintWriter out = response.getWriter();
            JSONObject obj = new JSONObject(); 
            obj.put("db","success");
            out.print(obj.toJSONString());
            out.flush();
        }      
        
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
        System.out.println("Get");
        response.addHeader("Access-Control-Allow-Origin", "*");
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
        System.out.println("POST");
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
