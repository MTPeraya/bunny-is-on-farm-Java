# Bunny is on Farm (2026 Java Edition)

Welcome to the 2026 native Java port of the classic 2025 Python Pygame release, **Bunny is on Farm**! <br>
[2025 Final project Github Repo](https://github.com/Mint-Peraya/Bunny-is-on-farm.git)

*This is a reversion of the Computer Programming 2 final project, with a heavy focus on Object-Oriented Programming (OOP) paradigms.*

This project reconstructs the original Python simulation game using pure `Java Swing` and `AWT`, vastly improving the underlying code architecture, event-handling mechanics, and quality of life elements.

## 🌟 Improvements Over the 2025 Final Project

While the 2025 Python version laid out an extensive foundation for farming and dungeon exploration, this 2026 Java port introduces drastic structural and mechanical enhancements:

### 1. Architectural Overhaul (MVC & State Management)
- **State Machine Integration**: Instead of a massive monolithic game loop spanning a single file, the Java version isolates game modes into a clean hierarchical `GameState` layout (`FarmState`, `DungeonState`, `MazeState`).
- **Object-Oriented Entities**: Game properties are heavily encapsulated. Entities like `Tile.java` and `Plant.java` manage their own calendars and interactions cleanly, moving away from tightly-coupled scripts.

### 2. Enhanced I/O & Rendering
- **Robust Asset Loading**: In Python, image loads were often duplicated manually. Here, `Config.java` utilizes `javax.imageio.ImageIO` to traverse the `assets` repository exactly once upon launch, significantly improving memory management and decreasing frame drops.
- **Dynamic Framerate Controls**: Standardized the double-buffering logic rendering at a strict 60 FPS instead of floating time limits. 
- **Event-Driven Interactions**: Movement is polled for smooth sub-pixel gliding, whilst interactions like *digging, chopping, and opening portals* are strictly **Event-Driven** (`keyPressed` triggers), entirely fixing input lag where the Spacebar would occasionally be eaten by the previous loop.

### 3. Quality of Life Mechanical Improvements
- **Resource Rebalancing**: The previous implementation forced players to repeatedly spam inputs with little feedback. Now, resources drop with a yield multiplier (trees and stones can yield multiple items) and display real-time interactive UI notification bubbles directly below the health-bar.
- **Auto-Loot Visualizations**: The `Inventory` system features real-world item popups, eliminating the need to frequently switch into the `Python Tkinter` window to see gathered materials.

## 🛠 Compilation and Usage

Since this is a native Java application natively uncoupled from heavy IDE wrappers, you simply need a valid JDK installation:

1. **Install Java**: (`brew install openjdk` on Mac)
2. **Compile the App**: `javac *.java`
3. **Run the App**: `java Main`

**Enjoy Farming!**