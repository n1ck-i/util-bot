.PHONY: all
all: build run


build:
	mvn clean install

run:
	TOKEN="$(token)" java -jar ./target/util-bot-0.1.jar
