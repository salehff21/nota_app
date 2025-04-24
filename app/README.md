# 📝 Task Management App

A lightweight and offline Android app built using **Kotlin** and **SQLite** to help users manage and organize their daily tasks with ease.

---

## 📱 Features

- Add new tasks with a title, optional description, and due date.
- Mark tasks as **completed** or **pending**.
- Edit and delete tasks anytime.
- Store all tasks **locally using SQLite** – no internet required.
- Modern and clean **Material Design UI**.
- Supports **Dark Mode** for comfortable night use.

---

## 🧰 Tech Stack

| Component         | Technology                |
|------------------|---------------------------|
| Language          | Kotlin                    |
| Database          | SQLite (SQLiteOpenHelper) |
| IDE               | Android Studio            |
| UI                | XML + Material Components |
| List Display      | RecyclerView              |

---

## 📂 Project Structure

```plaintext
app/
├── java/
│   └── com.example.taskmanager/
│       ├── MainActivity.kt
│       ├── TaskAdapter.kt
│       ├── TaskDatabaseHelper.kt
│       └── TaskModel.kt
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   └── item_task.xml
│   └── values/
│       ├── colors.xml
│       ├── styles.xml
│       └── strings.xml
