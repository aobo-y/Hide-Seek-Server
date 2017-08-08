<%-- 
    Document   : index
    Created on : Oct 6, 2016, 3:35:23 PM
    Author     : aceni
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body bgcolor="black" style="color:white; letter-spacing:0.1px;">
    <form action="QueryGenerator" method="get">
            Query
            </br>
            <textarea rows="10" cols="30" name="input"></textarea>
            <input type="submit" value="Submit"/>
            </br>
          
            Id
            </br>
            <textarea rows="10" cols="30" name="id"></textarea>
       
            
            numcover
            </br>
            <textarea rows="10" cols="30" name="numcover"></textarea>  
    </form>
    </body>
</html>
