.PHONY: all
all: build run

host = localhost
user = ubot_user
pass = ubot_password


build:
	DB_HOST=$(host) DB_USER=$(user) DB_PASS=$(pass) mvn clean install

run:
	DB_HOST=$(host) DB_USER=$(user) DB_PASS=$(pass) java -jar ./target/util-bot-0.1.jar
