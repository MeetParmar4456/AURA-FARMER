# Aura Farmer 🌾

Aura Farmer is a command-line RPG focused on grinding and resource management. Your goal is to accumulate 'Auras'—the game's currency—by hustling through a variety of activities. From high-risk, high-reward heists to strategic PvP battles, every command is a step towards becoming the wealthiest player.

---

## ✨ Key Features

* **Earn Auras**: Engage in various activities like `dig`, `hunt`, `crime`, and `work` to earn Auras.
* **Career System**: Get a job, work shifts to earn a steady income, and climb the ladder.
* **The Casino**: Test your luck with coinflip, slots, twinroll, and a strategic mines game.
* **Marketplace**: Use the `shop` to buy essential tools and the `sell` command to offload items for profit.
* **PvP Arena**: Challenge other players to an "Aura Rush" to see who can earn the most Auras in a set time. The winner takes their earnings; the loser's balance is reset to zero.

## 🔒 Security and Privacy

We take the security of our users and the integrity of our project seriously.

* **User Privacy**: Your security is a priority. All user passwords are **salted and hashed** using a strong cryptographic algorithm before being stored in the database. We never store passwords in plain text, ensuring your account credentials remain protected.

* **Developer IP Protection**: The executable version of this game, available in the Releases, is packaged using third-party software to protect the underlying source code. This prevents reverse-engineering and safeguards the developer's intellectual property.

## 🛠️ Technologies Used

* **Language:** Java
* **Database:** MySQL, applying core Database Management System (DBMS) concepts for data persistence.
* **Core Concepts:** Utilizes fundamental Data Structures and showcases a layered architecture with Object-Oriented Programming (OOP) principles.
---

## 🚀 Getting Started

This project offers two ways to play: a simple executable for gamers and a source code setup for developers.

### 🎮 Easy Installation (Recommended for Players)

For the quickest way to start playing, you can use the pre-packaged executable.

1.  Navigate to the **[Releases Page](https://github.com/MeetParmar4456/AURA-FARMER/releases)** of this repository.
2.  Download the latest release `.zip` file (e.g., `Aura-Farmer-v1.0.zip`).
3.  Unzip the folder to your desired location.
4.  Run the `Aura Farmer.exe` file to start the game!

> **❗ Important Note:** This game requires a live connection to the online server database to function. If the server is offline, the executable will not work.

### 💻 Installation from Source (For Developers)

Follow these instructions to get a copy of the project up and running on your local machine.

#### Prerequisites

You will need to have the following installed on your machine:
* Java Development Kit (JDK)
* MySQL Connector/J library (e.g., `mysql-connector-j-8.1.0.jar`)

#### Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/MeetParmar4456/AURA-FARMER.git](https://github.com/MeetParmar4456/AURA-FARMER.git)
    ```

2.  **Navigate to the source directory:**
    ```bash
    cd AURA-FARMER/aurafarmer_final/src/main/java
    ```

3.  **Compile the project**:
    Run the following command from the `java` directory. Make sure to replace `path/to/` with the actual path to your MySQL connector `.jar` file.
    ```bash
    javac -cp ".:path/to/mysql-connector-j-8.1.0.jar" com/aurafarmer/Main.java com/aurafarmer/model/*.java com/aurafarmer/service/*.java com/aurafarmer/util/*.java com/aurafarmer/commands/*.java com/aurafarmer/menus/*.java com/aurafarmer/db/*.java
    ```

4.  **Run the application**:
    From the same directory, run the game using this command:
    ```bash
    java -cp ".:path/to/mysql-connector-j-8.1.0.jar" com.aurafarmer.Main
    ```

---




