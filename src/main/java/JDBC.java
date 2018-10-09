import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.*;
import edu.virginia.cs.object.Query;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aceni
 */
public class JDBC {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/chrome?verifyServerCertificate=false&useSSL=true";
    static final String USER = "root";
    static final String PASS = "example";


    public static void registerUser(String uid) {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO Users VALUES (?,?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, uid);
                statement.setString(2, "");
                statement.executeUpdate();
            }
            stmt.close();
            conn.close();
        } catch (SQLException se) {
           se.printStackTrace();
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
           try {
              if (stmt != null)
                stmt.close();
           } catch (SQLException se2) {

           }
           try {
              if (conn != null)
                conn.close();
           } catch (SQLException se) {
              se.printStackTrace();
           }
        }
    }

    public static String getProfile(String uid) {
        Connection conn = null;
        Statement stmt = null;
        String profile = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT profile FROM Users WHERE userID = ?";
            ResultSet rs;
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, uid);
            rs = statement.executeQuery();

            while (rs.next()) {
                profile = rs.getString("profile");
            }

            if (profile.contains(",")) {
                profile = profile.replace(",", "\t");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
           se.printStackTrace();
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
           try {
              if (stmt != null)
                stmt.close();
           } catch (SQLException se2) {

           }
           try {
              if (conn != null)
                conn.close();
           } catch (SQLException se) {
              se.printStackTrace();
           }
        }
        return profile;
    }

    public static QueryData getPreviousCoverQueryData(String uid) {
        Connection conn = null;
        Statement stmt = null;
        QueryData qd = new QueryData();
        Query uq = new Query();
        Query pq = new Query();
        ArrayList<Query> previousQueries = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            String sql;
            ResultSet rs;
            sql = "SELECT * FROM Queries WHERE actionID = (SELECT MAX(actionID) FROM Queries WHERE userID = ?) AND userID = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, uid);
            statement.setString(2, uid);
            rs = statement.executeQuery();

            if (!rs.next()) {
                // no previous action
                return null;
            } else {
                int session = rs.getInt("sessionID");
                int action = rs.getInt("actionID");
                String time = rs.getString("time");
                do {
                    if (rs.getInt("tag") == 1) {
                        // java queries
                        Query tmp = new Query();
                        tmp.setQueryText(rs.getString("query"));
                        tmp.setQueryLength(-1);
                        tmp.setQueryTopic(rs.getString("topic"));
                        tmp.setQueryTopicNo(rs.getInt("topicNo"));
                        tmp.setBucketNo(-1);
                        previousQueries.add(tmp);
                    } else if (rs.getInt("tag") == 2) {
                        // python query
                        pq.setQueryText(rs.getString("query"));
                        pq.setQueryLength(-1);
                        pq.setQueryTopic(rs.getString("topic"));
                        pq.setQueryTopicNo(rs.getInt("topicNo"));
                        pq.setBucketNo(-1);
                    } else {
                        // user queries
                        uq.setQueryText(rs.getString("query"));
                        uq.setQueryLength(-1);
                        uq.setQueryTopic(rs.getString("topic"));
                        uq.setQueryTopicNo(rs.getInt("topicNo"));
                        uq.setBucketNo(-1);
                    }
                } while (rs.next());
                qd = new QueryData(previousQueries, uq, pq, session, action, time);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
           se.printStackTrace();
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
           try {
              if (stmt != null)
                stmt.close();
           } catch (SQLException se2) {

           }
           try {
              if (conn != null)
                conn.close();
           } catch (SQLException se) {
              se.printStackTrace();
           }
        }
        return qd;
    }

    public static void saveClick(String uid, String url, String title, String query, int tag, int idx, String time) {
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO Clicks VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, uid);
                statement.setString(2, url);
                statement.setString(3, title);
                statement.setString(4, query);
                statement.setInt(5, tag);
                statement.setInt(6, idx);
                statement.setString(7, time);
                statement.executeUpdate();
            }
            stmt.close();
            conn.close();
        }catch(SQLException se){
           se.printStackTrace();
        }catch(Exception e){
           e.printStackTrace();
        }finally{
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){

           }
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }
        }
    }

    public static void saveQuery(Query q, int a, int s, int t, String uid, String time) {
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO Queries VALUES (?,?,?,?,?,?,?,?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, a);
                statement.setInt(2, s);
                statement.setString(3, q.getQueryText());
                statement.setString(4, q.getQueryTopic());
                statement.setInt(5, q.getQueryTopicNo());
                statement.setInt(6, t);
                statement.setString(7, uid);
                statement.setString(8, time);
                statement.executeUpdate();
            }
            stmt.close();
            conn.close();
        }catch(SQLException se){
           se.printStackTrace();
        }catch(Exception e){
           e.printStackTrace();
        }finally{
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){

           }
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }
        }
    }

    public static void saveProfile(String uid, String profile) {
        Connection conn = null;
        Statement stmt = null;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            String sql;
            sql = "UPDATE Users SET profile = ? WHERE userID = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, profile);
                statement.setString(2, uid);
                statement.executeUpdate();
            }
            stmt.close();
            conn.close();
        }catch(SQLException se){
           se.printStackTrace();
        }catch(Exception e){
           e.printStackTrace();
        }finally{
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){

           }
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }
        }
    }

    public static void main(String[] argv) throws Exception {

    }

    // get one cover query from python program
    public static String getCover(String query) throws Exception {

        String USER_AGENT = "Mozilla/5.0";
        String q = URLEncoder.encode(query, "UTF-8");
        String urlString = "http://localhost:8000/cover/?query=" + q;

        URL url = new URL(urlString);
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            // Reading response from input Stream
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String output;
            StringBuffer response = new StringBuffer();
            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            in.close();
            System.out.println("~~~~~PYTHON SUCCESS~~~~~");
            System.out.println("    " + "python query: " + response.toString());
            return response.toString();
        } catch(java.net.SocketTimeoutException e) {
            return null;
        } catch (java.io.IOException e) {
            return null;
        }
    }
}
