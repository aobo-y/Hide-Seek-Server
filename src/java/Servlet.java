/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//import edu.virginia.cs.model.GenerateCoverQuery;
import edu.virginia.cs.model.LanguageModel;
import edu.virginia.cs.model.LoadLanguageModel;
import edu.virginia.cs.utility.FileReader;
import edu.virginia.cs.utility.Util;
import edu.virginia.cs.model.IntentAwarePrivacy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
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
import org.json.simple.JSONArray;
import java.util.Random;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author aceni
 */
@WebServlet(urlPatterns = {"/QueryGenerator"})
public class Servlet extends HttpServlet {
   
    IntentAwarePrivacy IAP;
    
        @Override
	public void init() {
            //do initialization
            //notice the path string 
            //String path = System.getenv("QG_PATH");
            String path = "/Users/Lucius/Documents/data";
            String ReferencePath = path + "/data/Reference-Model";
            String dmozPath = path + "/lucene-DMOZ-index";
            String logPath = path + "/user-search-log/";
            String docPath = path + "/data/ODP-doc-content.xml";
            
            HashMap<String, Double> refModel = Util.loadRefModel(ReferencePath);
            LoadLanguageModel llm = new LoadLanguageModel(refModel, false, false);
            llm.loadModels(docPath, 4);
            ArrayList<LanguageModel> langModels = llm.getLanguageModels();
            IntentAwarePrivacy IAP = new IntentAwarePrivacy(langModels, refModel, dmozPath);
            IAP.setTokenizer(false, false);
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
        //GET request for cover query generatigon
        if(request.getMethod().equals("GET")){
//            //get input from client
//            String input = request.getParameter("query");
//            if("".equals(input) || input == null) {
//                PrintWriter out = response.getWriter();
//                JSONObject obj = new JSONObject();
//                obj.put("output1","null"); 
//                out.print(obj.toJSONString());
//                out.flush();
//                return;
//            }
//            //get time now or use the client input time
//            //String time = request.getParameter("time");
//            Date dNow = new Date( );
//            SimpleDateFormat ft = 
//            new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
//            String time = ft.format(dNow);
//            System.out.println("Time: " + time);
//            //get id from client
//            uid = request.getParameter("id");
//            System.out.println("ID: " + uid);
//            //get number of cover query from client
//            int numcover = Integer.parseInt(request.getParameter("numcover"));
//
//            //process input and generate result
//            //use Java server to generate n-1 cover queries
//            ArrayList<String> coverQueries = GQ.generateNCoverQueries(input, time, uid, numcover - 1);
//            //File f = new File("/Users/Lucius/Documents/data/user-search-log/" + uid + ".txt");
//            //File f = new File("/home/ubuntu/java_servlet/data/user-search-log/" + uid + ".txt");
//            File f = new File("/home/ubuntu/data/user-search-log/" + uid + ".txt");
//            String log = FileReader.GetLastLine(f);
//            System.out.println(log);
//            String[] splits = log.split("<>");
//            System.out.println(splits.length);
//            for (int i = 0; i < splits.length; i++) {
//                System.out.println(i + ": " + splits[i]);
//            }
//            String input_topic = splits[3];
//            //store the original query and cover queries into db by JDBC
//            //Input: uid, time, query, tag (1 = real, 0 = cover)
//            JDBC.saveQuery(uid, time, input, 1, input_topic);
//            //use Python server to generate 1 cover query
//            //param: time, previous cover query generated by python server
//            String cover = "null";
//            //check if the user exists:
//            if (JDBC.checkUser(uid)!=false) {
//                //user exists, get last query and time 
//                //check if within 1 hour and return last query if yes
//                //otherwise returl "null"
//                cover=JDBC.getPrevious(uid, time);
//            } 
//            
//            String covQuery = "null";
//            try {
//                //send GET request to get cover query from python server
//                covQuery = JDBC.getCover(cover);
//            } catch (Exception ex) {
//                Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//            //store cover into database and give tag 2
//            JDBC.saveQuery(uid, time, covQuery, 2, "notopic");
//            //coverQueries.add(covQuery);
//            
//            //print json according to the length of cover Queries
//            PrintWriter out = response.getWriter();
//            JSONObject obj = new JSONObject();
//            int count = 6;
//            String cover_query_topic = "";
//            if(!coverQueries.isEmpty()) {
//                obj.put("db","success");
//                obj.put("input", splits[3]);
//                for (String s: coverQueries) {
//                    cover_query_topic = splits[count];
//                    JDBC.saveQuery(uid, time, s, 0, cover_query_topic);
//                    obj.put(s, cover_query_topic);
//                    count += 3;
//                }
//            } else {
//                obj.put("output1","null"); 
//            }
//            //write input's topic
////            obj.put("input", getRandomTopic(topics));
//            //write python query's topic
//            obj.put("notopic", covQuery);
//            
//            //System.out.println("total count: " + coverQueries.size());
//            //System.out.println(coverQueries);
//            //System.out.println(obj.toJSONString());
//            out.print(obj.toJSONString());
//            out.flush();

        } else if (request.getMethod().equals("POST")) {
            //get parameters from POST request
            //uid is already store locally
            //timestamp is generated here
            Date dNow = new Date();
            SimpleDateFormat ft = 
            new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
            String time = ft.format(dNow);
            
            String action = request.getParameter("action");
            if (action.equals("R")) {
                // 注册用户
                String uid = request.getParameter("uid");
                JDBC.registerUser(uid);
            } else if (action.equals("U")) {
                // 重排序，根据传过来的snippet，返回顺序
                String[] jsonData = request.getParameterValues("json");
                String uid = request.getParameter("uid");
                String userProfile = JDBC.getProfile(uid);
                Integer[] arr = IAP.reRankDocuments(jsonData, userProfile);
                int[] intArray = Arrays.stream(arr).mapToInt(Integer::intValue).toArray();
                PrintWriter out = response.getWriter();
                JSONObject jo = new JSONObject();
                for (int i = 0; i < intArray.length; i++) {
                    jo.put(i, intArray[i]);
                }
                // JSONArray 没法用，所以发回去的是比较复杂的数据结构，用JS再解析吧，反正数据小
                out.print(jo.toJSONString());
                out.flush();
            } else if (action.equals("UC")) {
                // 存储用户点击，修改user profile
            } else if (action.equals("SC")) {
                // 存储模拟点击
            } else if (action.equals("Q")) {
                // 存储query，返回cover queries以及相关信息，修改user profile
            }

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
            
            System.out.println(uid + time + query + url + clickIndex);

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
