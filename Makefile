PORT=9001
PLAY_SI_REPO=https://github.com/BenjBouv/Playsi.git

all:
	@git pull
	@play clean compile stage
	(./target/start -Dhttp.port=$(PORT) -Dconfig.file=`pwd`/conf/application.prod.conf &) > log

