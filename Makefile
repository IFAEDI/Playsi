PORT=9001

all:
	@git pull
	@play clean compile stage
	(./target/start -Dhttp.port=$(PORT) -Dconfig.file=`pwd`/conf/application.prod.conf &) > log

stop:
	kill `cat RUNNING_PID`
