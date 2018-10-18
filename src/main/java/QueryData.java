import edu.virginia.cs.object.Query;
import java.util.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lucius
 */
public class QueryData {
    private ArrayList<Query> coverQueryList;
    private Query userQuery;
    private Query pythonQuery;
    private int sessionID;
    private int actionID;
    private String time;

    public QueryData() {
    };

    public QueryData(ArrayList<Query> q, Query uq, Query pq, int s, int a, String t) {
        coverQueryList = q;
        userQuery = uq;
        pythonQuery = pq;
        sessionID = s;
        actionID = a;
        time = t;
    }

    public ArrayList<Query> getCoverQueryList() {
        return coverQueryList;
    }

    public void setCoverQueryList(ArrayList<Query> coverQueryList) {
        this.coverQueryList = coverQueryList;
    }

    public Query getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(Query userQuery) {
        this.userQuery = userQuery;
    }

    public Query getPythonQuery() {
        return pythonQuery;
    }

    public void setPythonQuery(Query pythonQuery) {
        this.pythonQuery = pythonQuery;
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public int getActionID() {
        return actionID;
    }

    public void setActionID(int actionID) {
        this.actionID = actionID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
