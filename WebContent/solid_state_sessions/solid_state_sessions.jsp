<%@page     contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.io.*""%>
    
http://www.unifiedds.com/?p=72

CREATE TABLE session
(
id CHAR(32) NOT NULL,
data BLOB,
valid_session CHAR(1) NOT NULL,
max_inactive INT NOT NULL,
last_access BIGINT NOT NULL,
PRIMARY KEY (id)
);

if you have the MySQL Connector/J driver installed in lib, move it to common/lib.

add in server.xml file in Tomcat’s conf directory
<Context path="/mcb" docBase="mcb" debug="0" reloadable="true">
<Manager className="org.apache.catalina.session.PersistentManager"
debug="0" saveOnRestart="true" minIdleSwap="900" maxIdleSwap="1200"
maxIdleBackup="600">
<Store className="org.apache.catalina.session.JDBCStore"
driverName="com.mysql.jdbc.Driver" connectionURL=
"jdbc:mysql://hostname/tomcat?user=root&amp;password=pass"
sessionTable="session"
sessionIdCol="id"
sessionDataCol="data"
sessionValidCol="valid_session"
sessionMaxInactiveCol="max_inactive"
sessionLastAccessedCol="last_access"
/>
</Manager>
</Context>

consider url encoding.

