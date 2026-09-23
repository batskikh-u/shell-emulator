SRC = $(wildcard src/*.java)
OUT = out

.PHONY: all build run clean

all: build

build:
	mkdir -p $(OUT)
	javac -d $(OUT) $(SRC)

run: build
	java -cp $(OUT) Main

clean:
	rm -rf $(OUT)