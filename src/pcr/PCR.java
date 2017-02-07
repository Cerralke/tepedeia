/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 *
 * @author User
 */
public class PCR {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    Connection conn = null;
    Properties connectionProps = new Properties();
    String url = "jdbc:mysql://localhost:3306/DBTEST";
   
    connectionProps.put("user", "admin");
    connectionProps.put("password", "admin");

    try{
        conn = DriverManager.getConnection(url, connectionProps);
    }catch(Exception e){
        System.out.println("No connection");
        return;
    }
    System.out.println("Connected to database");
    }
    
}
