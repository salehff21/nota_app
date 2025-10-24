
<h1 align="center">📝 nota_app — Task Management (Android, Kotlin + SQLite)</h1>

<p align="center">
Lightweight, offline-first task manager built with <b>Kotlin</b>, <b>SQLite</b>, and <b>Material Design</b>.
Create, edit, complete, and delete tasks with optional description and due date. Works fully offline.
</p>

<p align="center">
  <img alt="Android" src="https://img.shields.io/badge/Android-Studio-3DDC84?logo=android&logoColor=white">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-1.x-7F52FF?logo=kotlin&logoColor=white">
  <img alt="SQLite" src="https://img.shields.io/badge/SQLite-OpenHelper-003B57?logo=sqlite&logoColor=white">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-lightgrey">
</p>


```markdown

## ✨ Features
- Add tasks with **title**, optional **description**, and **due date**  
- Mark tasks as **completed** or **pending**  
- Edit and delete tasks anytime  
- Stores all data **locally** with SQLite (no internet required)  
- **Dark Mode** support  
- Clean **Material Design** UI  
- Task list with **RecyclerView**

---

## 🧰 Tech Stack
- **Language:** Kotlin  
- **UI:** XML layouts, Material Components  
- **Database:** SQLite with `SQLiteOpenHelper`  
- **IDE:** Android Studio  
- **Components:** RecyclerView, Adapter pattern

---

## 📁 Project Structure
```

app/
├─ java/
│  └─ com.example.taskmanager/
│     ├─ MainActivity.kt
│     ├─ TaskAdapter.kt
│     ├─ TaskModel.kt
│     └─ TaskDatabaseHelper.kt
└─ res/
├─ layout/
│  ├─ activity_main.xml
│  └─ item_task.xml
├─ values/
│  ├─ colors.xml
│  ├─ styles.xml
│  └─ strings.xml
└─ mipmap-*/ app icons

```

---

## 🗄️ Database Schema (example)
`tasks` table:
| column | type | notes |
|---|---|---|
| id | INTEGER PRIMARY KEY AUTOINCREMENT | |
| title | TEXT NOT NULL | |
| description | TEXT | optional |
| due_date | TEXT | ISO-8601 string |
| is_completed | INTEGER | 0 = false, 1 = true |
| created_at | TEXT | ISO-8601 |

---

## ▶️ Getting Started
1. Open the project in **Android Studio**.  
2. Sync Gradle and build.  
3. Run on an emulator or a physical device (API 24+ recommended).

---

## 🔧 Key Classes
- `TaskDatabaseHelper` — creates/updates the SQLite DB and exposes CRUD methods.  
- `TaskModel` — data model for a task.  
- `TaskAdapter` — `RecyclerView.Adapter` for rendering task items.  
- `MainActivity` — list screen + add/edit logic.

---

## 🖼️ Screenshots
> Add your images here (place files in `/art` and link below).
<p align="center">
  <img src="art/home_light.png" width="32%">
  <img src="art/home_dark.png"  width="32%">
  <img src="art/editor.png"     width="32%">
</p>



## ✅ Roadmap
- [ ] Search and filters  
- [ ] Notifications for due dates  
- [ ] Export/Import to JSON  
- [ ] Jetpack Compose version



## 📝 License
This project is licensed under the **MIT License** — see `LICENSE` for details.



## 🤝 Contributing
Pull requests are welcome. For major changes, open an issue first to discuss what you’d like to change.
```
 
