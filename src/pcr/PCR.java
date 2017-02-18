/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcr;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author User
 */
public class PCR {

    private static List<Object> dataTab;
    private static List<Object> databaseTab;
    private final static String KEY_STRING = "Abc12345Dfg12345";
    private static Connection conn = null;
    
    private static String encryptionFunction(String input) throws InvalidKeyException, NoSuchAlgorithmException, 
            IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException{
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
       
        byte[] encryptedData = cipher.doFinal(input.getBytes());
        
        return DatatypeConverter.printBase64Binary(encryptedData);
    }
    
    private static String decryptFunction(String input) throws InvalidKeyException, NoSuchAlgorithmException, 
            NoSuchPaddingException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException{
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        
        byte[] decryptedData = cipher.doFinal(DatatypeConverter.parseBase64Binary(input));
        
        return DatatypeConverter.printBase64Binary(decryptedData);
    }
    
    private static Cipher getCipher(int cipherMode) throws InvalidKeyException, NoSuchAlgorithmException, 
            NoSuchPaddingException, UnsupportedEncodingException{
        String encryptionAlgorithm = "AES";
        SecretKeySpec keySpecification = new SecretKeySpec(KEY_STRING.getBytes("UTF-8"), encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);

       return cipher;
    }

    private static void selectFROMDb() throws SQLException, Exception{
        Statement stmt = conn.createStatement();
        String sqlPrepare = "CREATE VIEW Catalogues AS SELECT ShipTo, Product, Time, units, sales, cost\n" +
                            "FROM units_fact_table WHERE Channel='CAT'";
        stmt.execute(sqlPrepare);
        conn.commit();
        
        String sql = "SELECT ShipTo, Time, units, sales, cost FROM Catalogues";
        ResultSet rs = stmt.executeQuery(sql);
        databaseTab = new ArrayList<>();
        
        while(rs.next()){
            Object obj = new Object();
            obj.setParam1(decryptFunction(rs.getString("ShipTo")));
            obj.setParam2(decryptFunction(rs.getString("Time")));
            obj.setParam3(decryptFunction(rs.getString("units")));
            obj.setParam4(decryptFunction(rs.getString("sales")));
            obj.setParam5(decryptFunction(rs.getString("cost")));
            dataTab.add(obj);
        }
        rs.close();
    }
    
    public static void main(String[] args) {
        Properties connectionProps = new Properties();
        String url = "jdbc:mysql://localhost:3306/DBTEST";

        connectionProps.put("user", "admin");
        connectionProps.put("password", "admin");

        
        try{
            conn = DriverManager.getConnection(url, connectionProps);
            selectFROMDb();
            
            conn.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
            return;
            }
    }  
}
