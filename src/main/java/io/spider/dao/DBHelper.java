package io.spider.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBHelper {
	public static final String url = "jdbc:mysql://127.0.0.1/dataspider";  
    public static final String name = "com.mysql.jdbc.Driver";  
    public static final String user = "root";  
    public static final String password = "123456";
  
    public Connection conn = null;  
    public PreparedStatement pst = null;
    
    public static DBHelper getInstance() {
    	return new DBHelper();
    }
  
    public DBHelper() {  
        try {  
            Class.forName(name);//指定连接类型  
            this.conn = DriverManager.getConnection(url, user, password);//获取连接  
//            pst = conn.prepareStatement(sql);//准备执行语句 
//            pst.execute();
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    
    public void execute(String sql, Object... prams) {
    	try {
			pst = conn.prepareStatement(sql);//准备执行语句 
			for (int i = 0; prams != null && i < prams.length; i++) {
				pst.setObject(i+1, prams[i]);
			}
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void close() {  
        try {  
            this.conn.close();  
            this.pst.close();  
        } catch (SQLException e) {
            e.printStackTrace();  
        }  
    }
}
