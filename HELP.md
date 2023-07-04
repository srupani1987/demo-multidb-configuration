# Read Me First
The following commands are used to run the mariadb and mysql containers in docker:

* docker run --name mysqltest -e MYSQL_ROOT_PASSWORD=password -e MYSQL_USER=user -e MYSQL_DATABASE=test -e MYSQL_PASSWORD=password -p 3308:3306 -d mysql:latest


*  docker run --detach --name mariadbtest --env MARIADB_USER=user --env MARIADB_PASSWORD=password --env MARIADB_ROOT_PASSWORD=password -e MARIADB_DATABASE=test -p 3309:3306 mariadb:latest
