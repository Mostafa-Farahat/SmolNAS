# SmolNAS
- SmolNAS is a Java Servlet application that uses the Tomcat Servlet container and the HTTP protocol
- The goal of SmolNAS is to provide a simple interface for storing and retrieving files from a server
- SmolNAS supports multiple user accounts protected by a password stored in its database as a
SHA256 digest for increased security.
- Each user is not permitted to read or write in the directories of other users as long as they are not 
logged in as that user 
- SmolNAS was built for the purpose of being hosted on a local network

# How to setup
SmolNAS can be easily containerized to make the process of deployment easier

1. firstly set up the username and password of the database user under which you will be creating tables. The entries are in the file located in `SmolNAS/web/META-INF/context.xml`

```xml
<Resource name="jdbc/SmolNASdb" auth="Container" type="javax.sql.DataSource"
              username="<your username>" password="<your password>" driverClassName="com.mysql.jdbc.Driver"
              url="jdbc:mysql://SmolNASdb:3306/SmolNAS"/>
```

2. in the file `SmolNAS/web/directoryTemplate.html` change the host address from `localhost` to the ip of your home server on your network
```html
<script>
<!--    TO DO:replace localhost with an actual IP-->
        function download(path){
            location.href = "http://localhost:8080/SmolNAS/download?path="+window.location.pathname+"/"+path;
        }
        function del(path){
            location.href = "http://localhost:8080/SmolNAS/delete?path="+window.location.pathname+"/"+path;
        }
</script>
```

3. Create the required containers
```shell
# Create a network for SmolNAS and the mysql database
docker network create <network name>

#Build the docker file in the root of this repo
docker build -t <image name> .

#run SmolNAS container and mySQL container in the created network
docker run --name SmolNASdb -e MYSQL_ROOT_PASSWORD=<root password> -v <location on host>:/var/lib/mysql --network=<network name> mysql
docker run --name SmolNAS --network=<network name> -v <location on host>:/opt/NAS_DATA -p 8080:8080 <image name>
```
4. after the above steps, access the mySQL container (SmolNASdb) and set up a database called **SmolNAS** with tables defines by the following create statements

```sql
-- creates table that stores usernames and their password as a SHA256 digest
CREATE TABLE userData(
    userName varchar(32) PRIMARY KEY,
    password char(64) NOT NULL
);
```
```sql
--creates table that stores sessionIDs associated with the users
CREATE TABLE sessions(
    sessionID char(36) PRIMARY KEY,
    userName char(32),
    FOREIGN KEY (userName) REFERENCES userData(userName)
);
```
> [!NOTE]
> The contianer name **SmolNASdb** and database name **SmolNAS** should not be changed during the setup as SmolNAS's source code uses these names to communicate with the db


