# ChessPA

## Description

A Chess game implementation developed as a school project for Advanced Programming class. This project demonstrates object-oriented programming principles and software engineering best practices such as design patterns in Java.

## Features

- Complete chess game logic and rules

- Piece movement validation

- Check and checkmate detection

- Board state management

- Player vs Player gameplay

- Move history tracking

- Standard algebraic notation support

## Technologies

- Java

- JavaFX

## Project Structure

src/pt/isec/pa/chess/
├── Game logic
├── Piece management
├── Board representation
└── User interface

## Prerequisites
- **JDK 21** (LTS)
- **JavaFX 21 SDK**
- **IntelliJ IDEA** (recommended)

## Installation

### 1. Install JDK 21

**Windows:**
- Download from: https://corretto.aws/downloads/latest/amazon-corretto-21-x64-windows-jdk.zip
- Extract to a folder (e.g., `C:\jdk-21`)

**Mac:**
```bash
brew install openjdk@21
```

**Linux:**
```bash
sudo apt-get install openjdk-21-jdk
```

### 2. Download JavaFX 21 SDK

1. Go to: https://gluonhq.com/products/javafx/
2. Download **JavaFX 21 SDK** for your operating system
3. Extract to a folder (e.g., `C:\javafx-sdk-21` or `~/javafx-sdk-21`)
4. **Remember this path** - you'll need it in the next step

### 3. Clone the Repository

```bash
git clone https://github.com/miguelrealinho/ChessPA.git
cd ChessPA
```

### 4. Configure IntelliJ for JavaFX

#### Step A: Set JDK 21

1. Open the project in IntelliJ
2. Go to `File > Project Structure`
3. Go to **Project** tab
4. Under **SDK**, click the dropdown and select **Add SDK > JDK**
5. Navigate to your JDK 21 installation folder
6. Click **Open** and confirm

#### Step B: Add JavaFX 21 Library

1. In `File > Project Structure`, go to **Libraries** tab
2. Click the `+` button → select **Java**
3. Navigate to your JavaFX 21 SDK folder (the root folder you extracted)
4. Click **Open**
5. Name it `javafx-21`
6. Click **Apply**

#### Step C: Add Library to Module

1. In `File > Project Structure`, go to **Modules** tab
2. Select **PAChess** module
3. Go to **Dependencies** tab
4. Click `+` button → select **Library**
5. Choose `javafx-21` from the list
6. Click **Apply** and **OK**

### 5. Verify Configuration

1. Go to `File > Project Structure > Modules > PAChess`
2. Verify **Language level** is set to **21**
3. Click **Apply**

## Compilation and Execution

### Using IntelliJ (Recommended)
1. Click the green **Run** button (top right)
2. Select the main class if prompted
3. The chess game GUI should open

### Using Command Line

```bash
# Compile
javac --module-path /path/to/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml -d out src/pt/isec/pa/chess/*.java

# Run
java --module-path /path/to/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml -cp out pt.isec.pa.chess.Main
```

## Compilation and Execution

### Using IntelliJ IDEA (Recommended)
1. Open the project in IntelliJ IDEA
2. Click the green Run button
3. Follow the on-screen prompts

### Using Command Line
```bash
javac src/pt/isec/pa/chess/*.java
java -cp src pt.isec.pa.chess.Main
```


## Notes
This is an educational project showcasing OOP principles, design patterns, and software architecture in Java. It is not a production grade application.
