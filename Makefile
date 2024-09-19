# Variables
JAVAC = javac
JAVA = java
JFLAGS = --module-path ~/javafx-sdk-23-2/lib --add-modules javafx.controls,javafx.fxml,javafx.web
SRC_DIR = src
BIN_DIR = bin
LIB_DIR = lib
MAIN_CLASS = YoutubeVideoPlayer
JARS = $(LIB_DIR)/jl1.0.1.jar

# Default target: Compile and run
all: compile run

# Compile the Java source files
compile:
	$(JAVAC) $(JFLAGS) -cp "$(JARS)" -d $(BIN_DIR) $(SRC_DIR)/*.java

# Run the project
run:
	$(JAVA) $(JFLAGS) -cp "$(BIN_DIR):$(JARS)" $(MAIN_CLASS)

# Clean the compiled files
clean:
	rm -rf $(BIN_DIR)/*.class
