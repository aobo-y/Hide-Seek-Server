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
import java.text.SimpleDateFormat;
import java.util.Date;
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
    // JDBC driver name and database URL
    // static final String DB_URL = "jdbc:mysql://hcdm.cs.virginia.edu/";
    static final String DB_URL = "jdbc:mysql://localhost:3306/";
    //  Database credentials
    static final String USER = "puxuan";
    static final String PASS = "astro611";
   
    public static boolean checkUser(String uid) {
        Connection conn = null;
        Statement stmt = null;
        boolean result = false;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "use ChromeExtension;";
            stmt.executeQuery(sql);
            
            //Insert Record
            sql = "SELECT * from query where uid ='" + uid + "'";   
            System.out.println("Checking records...");
            System.out.println();
            ResultSet rs = stmt.executeQuery(sql);
            //STEP 5: Extract data from result set
            while(rs.next()){
               //Retrieve by column name
               return true;
            }
            rs.close();

            //STEP 6: Clean-up environment
            stmt.close();
            conn.close();
        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }
        }
      
        return result;
    }
    
    public static void registerUser(String uid) {
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO chrome.Users VALUES (?,?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, uid);
                statement.setString(2, "");
                statement.executeUpdate();
            }
            
            System.out.println("Inserted records into the table...");
            stmt.close();
            conn.close();
        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
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
    
    public static String getPrevious(String uid, String time) {
        Connection conn = null;
        Statement stmt = null;
        String result = "null";
        String query = null, maxtime = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "use ChromeExtension;";
            stmt.executeQuery(sql);
            
            //Insert Record
            sql = "SELECT query, max(time) from query where uid ='" + uid + "'";   
            System.out.println("Checking records...");
            System.out.println();
            ResultSet rs = stmt.executeQuery(sql);
            //STEP 5: Extract data from result set
           
            while(rs.next()){
               //Retrieve by column name
               query = rs.getString("query");
               maxtime = rs.getString("max(time)");
            } 
            rs.close();

            //determine what to return 
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date1 = format.parse(time);
            Date date2 = format.parse(maxtime);
            long diff = date2.getTime() - date1.getTime(); 
            long diffMinutes = diff / (60 * 1000);
            if (diffMinutes < 60) {
                if(!query.isEmpty()) {
                    result = query;
                }
            } 
  
            //STEP 6: Clean-up environment
            stmt.close();
            conn.close();
        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }
        }
      
        return result;
    }
    
    public static void saveQuery(String uid, String time, String query, int tag, String topic) {
        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "use ChromeExtension;";
            stmt.executeQuery(sql);
            
            //Insert Record
//            if (topic.equals("notopic")) {
//                sql = "INSERT INTO query " +
//                "VALUES ('" + uid + "', '" + time + "', '" + query + "', " + tag + ", NULL)";
//            } else {
//                sql = "INSERT INTO query " +
//                "VALUES ('" + uid + "', '" + time + "', '" + query + "', " + tag + ", '" + topic + "')";
//            }
            sql = "INSERT INTO query VALUES (?,?,?,?,?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, uid);
                statement.setString(2, time);
                statement.setString(3, query);
                statement.setInt(4, tag);
                if (topic.equals("notopic")) {
                    statement.setNull(5, java.sql.Types.VARCHAR);
                } else {
                    statement.setString(5, topic);
                }
                statement.executeUpdate();
            }
//            stmt.executeUpdate(sql);      
            System.out.println("Inserted records into the table...");
            System.out.println();
            
            //STEP 6: Clean-up environment
            stmt.close();
            conn.close();
        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }
        }
    }
    
    
    public static void saveURL(String url, String content, String query) {
        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "use ChromeExtension;";
            stmt.executeQuery(sql);
            
            //Insert Record
//            sql = "INSERT INTO urls " +
//            "VALUES ('" + url + "', '" + content+ "', '" + query + "')";
//            stmt.executeUpdate(sql); 
            sql = "INSERT INTO urls VALUES (?,?,?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, url);
                statement.setString(2, content);
                statement.setString(3, query);
                statement.executeUpdate();
            }
            System.out.println("Inserted records into the table...");
            System.out.println();
            
            //STEP 6: Clean-up environment
            stmt.close();
            conn.close();
        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }
        }
    }
    
    
    public static void saveClick(String uid, String time, String query, String url, int click) {
        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "use ChromeExtension;";
            stmt.executeQuery(sql);
                  
            sql = "INSERT INTO clicks VALUES (?,?,?,?,?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, uid);
                statement.setString(2, time);
                statement.setString(3, query);
                statement.setString(4, url);
                statement.setInt(5, click);
                statement.executeUpdate();
            }
            System.out.println("Inserted records into the table...");
            System.out.println();
            
            //STEP 6: Clean-up environment
            stmt.close();
            conn.close();
        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }
        }
    }
    
    
     public static void main(String[] argv) throws Exception {
//        JDBC.saveQuery("123", "123", "123", 1);
//        JDBC.saveURL("www.google.com", "welcome to Google.", "google");
//        JDBC.saveClick("127.0.0.1", "12:17:22", "what is my name", "www.amazon.com",3);
//        boolean result = JDBC.checkUser("null");
//        System.out.println(result);

//        String result = JDBC.getCover("browser settings");
//        System.out.println(result);
//          String result = JDBC.getPrevious("213357887", "2017-05-18 06:58:20");
//          System.out.println(result);
          
    }
     
    // get one cover query from python program
    public static String getCover(String query) throws Exception {

    String USER_AGENT = "Mozilla/5.0";   
    String q = URLEncoder.encode(query, "UTF-8");
    //String urlString = "http://34.227.11.14:8000/cover/?query=" + q;
    String urlString = "http://120.77.42.144:8000/cover/?query=" + q;
    
     URL url = new URL(urlString);
     HttpURLConnection con = (HttpURLConnection) url.openConnection();

     // By default it is GET request
     con.setRequestMethod("GET");

     //add request header
     con.setRequestProperty("User-Agent", USER_AGENT);

     int responseCode = con.getResponseCode();
     System.out.println("Sending get request : "+ url);
     System.out.println("Response code : "+ responseCode);

     // Reading response from input Stream
     BufferedReader in = new BufferedReader(
             new InputStreamReader(con.getInputStream()));
     String output;
     StringBuffer response = new StringBuffer();

     while ((output = in.readLine()) != null) {
      response.append(output);
     }
     in.close();

     //printing result from response
     return response.toString();

    } 


    public void DBExample() {
            Connection conn = null;
            Statement stmt = null;
            try{
                //STEP 2: Register JDBC driver
                Class.forName("com.mysql.jdbc.Driver");

                //STEP 3: Open a connection
                System.out.println("Connecting to database...");
                conn = DriverManager.getConnection(DB_URL,USER,PASS);

                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                stmt = conn.createStatement();
                String sql;
                sql = "use ChromeExtension;";
                stmt.executeQuery(sql);

                //Create Table
                sql = "CREATE TABLE IF NOT EXISTS Registration " +
                        "(id INTEGER not NULL, " +
                        " first VARCHAR(255), " + 
                        " last VARCHAR(255), " + 
                        " age INTEGER, " + 
                        " PRIMARY KEY ( id ))"; 

                stmt.executeUpdate(sql);
                System.out.println("Created table in given database...");

                //Insert Record
                sql = "INSERT INTO Registration " +
                       "VALUES (100, 'Zara', 'Ali', 18)";
                stmt.executeUpdate(sql);
                sql = "INSERT INTO Registration " +
                             "VALUES (101, 'Mahnaz', 'Fatma', 25)";
                stmt.executeUpdate(sql);
                sql = "INSERT INTO Registration " +
                             "VALUES (102, 'Zaid', 'Khan', 30)";
                stmt.executeUpdate(sql);
                sql = "INSERT INTO Registration " +
                             "VALUES(103, 'Sumit', 'Mittal', 28)";
                stmt.executeUpdate(sql);
                System.out.println("Inserted records into the table...");

                //Delete Record
                sql = "DELETE FROM Registration " +
                       "WHERE id = 101";
                stmt.executeUpdate(sql);

                //Update Record
                sql = "UPDATE Registration " +
                       "SET age = 30 WHERE id in (100, 101)";
                stmt.executeUpdate(sql);


                //Select Record
                sql = "SELECT id, first, last, age FROM Registration";
                ResultSet rs = stmt.executeQuery(sql);
                //STEP 5: Extract data from result set
                while(rs.next()){
                   //Retrieve by column name
                   int id  = rs.getInt("id");
                   int age = rs.getInt("age");
                   String first = rs.getString("first");
                   String last = rs.getString("last");

                   //Display values
                   System.out.print("ID: " + id);
                   System.out.print(", Age: " + age);
                   System.out.print(", First: " + first);
                   System.out.println(", Last: " + last);
                }

                //STEP 6: Clean-up environment
                rs.close();
                stmt.close();
                conn.close();
            }catch(SQLException se){
               //Handle errors for JDBC
               se.printStackTrace();
            }catch(Exception e){
               //Handle errors for Class.forName
               e.printStackTrace();
            }finally{
               //finally block used to close resources
               try{
                  if(stmt!=null)
                     stmt.close();
               }catch(SQLException se2){
               }// nothing we can do
               try{
                  if(conn!=null)
                     conn.close();
               }catch(SQLException se){
                  se.printStackTrace();
               }//end finally try
            }//end try
            System.out.println("Goodbye!");
           }
    }