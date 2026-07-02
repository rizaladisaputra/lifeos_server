# Rencana Refactoring All Modules ke Bounded Context First (Standar DDD Internasional)

Untuk mematuhi standar internasional Domain-Driven Design (DDD) modern secara konsisten, kami akan menyusun ulang seluruh modul di backend `lifeos_be` menggunakan pendekatan **Bounded Context First (Package-by-Feature)**. 

Seluruh Bounded Context akan berada langsung di bawah package `com.lifeos.be` (menghilangkan folder `com.lifeos.be.domain` tingkat atas), dan dibagi menjadi sub-package `domain`, `application`, dan `infrastructure`.

---

## Desain Paket Baru untuk Semua Modul

Struktur direktori baru di bawah `src/main/java/com/lifeos/be/` akan seragam sebagai berikut:

```text
com.lifeos.be/
│
├── core/                           # SHARED KERNEL & UTILITIES
│   ├── config/                     # SecurityConfig, CorsConfig
│   ├── security/                   # JwtService, JwtFilter, UserDetails
│   └── service/                    # FcmService
│
├── planner/                        # BOUNDED CONTEXT: PLANNER
│   ├── domain/
│   │   ├── model/ (Activity.java)
│   │   ├── repository/ (ActivityRepository.java)
│   │   └── service/ (PlannerDomainService.java)
│   ├── application/
│   │   ├── service/ (ActivityService.java)
│   │   └── dto/ (ActivityDto.java)
│   └── infrastructure/
│       ├── persistence/ (ActivityJpaEntity.java, SpringDataActivityRepository.java, ActivityRepositoryImpl.java)
│       ├── config/ (PlannerConfiguration.java)
│       └── web/ (ActivityController.java)
│
├── habit/                          # BOUNDED CONTEXT: HABIT
│   ├── domain/
│   │   ├── model/ (Habit.java, HabitLog.java)
│   │   └── repository/ (HabitRepository.java, HabitLogRepository.java)
│   ├── application/
│   │   ├── service/ (HabitService.java)
│   │   └── dto/ (HabitDto.java)
│   └── infrastructure/
│       └── persistence/ (HabitJpaEntity.java, SpringDataHabitRepository.java, etc.)
│       └── web/ (HabitController.java)
│
├── prayer/                         # BOUNDED CONTEXT: PRAYER
│   ├── domain/
│   │   ├── model/ (Prayer.java)
│   │   └── repository/ (PrayerRepository.java)
│   ├── application/
│   │   ├── service/ (PrayerService.java)
│   │   └── dto/ (PrayerDto.java)
│   └── infrastructure/
│       └── web/ (PrayerController.java)
│
├── goal/                           # BOUNDED CONTEXT: WEEKLY GOAL
│   ├── domain/
│   │   ├── model/ (WeeklyGoal.java)
│   │   └── repository/ (WeeklyGoalRepository.java)
│   ├── application/
│   │   ├── service/ (WeeklyGoalService.java)
│   │   └── dto/ (WeeklyGoalDto.java)
│   └── infrastructure/
│       └── web/ (WeeklyGoalController.java)
│
├── auth/                           # BOUNDED CONTEXT: AUTHENTICATION
│   ├── domain/                     # (Tidak memerlukan entitas domain murni jika sudah diwakili User)
│   ├── application/
│   │   ├── service/ (AuthService.java)
│   │   └── dto/ (SignupRequest.java, LoginRequest.java, AuthResponse.java)
│   └── infrastructure/
│       └── web/ (AuthController.java)
│
└── gamification/                   # BOUNDED CONTEXT: USER STATS & XP
    ├── domain/
    │   ├── model/ (User.java)      # Rich Domain Model
    │   └── repository/ (UserRepository.java)
    ├── application/
    │   └── dto/ (FCM Token & XP Requests)
    └── infrastructure/
        └── web/ (UserController.java)
```

---

## Struktur Folder Test (`src/test/java/`)

Untuk testing, struktur folder akan **meniru secara persis (mirroring)** struktur folder source code utama agar mempermudah navigasi kelas testing:

```text
src/test/java/
└── com/
    └── lifeos/
        └── be/
            ├── LifeosBeApplicationTests.java         # Test Spring context load di root
            │
            ├── gamification/
            │   └── domain/
            │       └── model/
            │           └── UserTest.java              # Menguji unit User Rich Domain
            │
            └── planner/
                └── application/
                    └── service/
                        └── ActivityServiceTest.java   # Menguji unit Activity Application Service
```

---

## Proposed Changes

### [Backend Server]

#### [NEW] Reorganisasi & Refactor ke Package Baru
- Memindahkan semua Bounded Context (`planner`, `habit`, `prayer`, `goal`, `auth`, `gamification`) langsung ke bawah `com.lifeos.be`.
- Membagi internal modul tersebut menjadi sub-package `domain`, `application`, dan `infrastructure`.
- Mengubah deklarasi `package` di setiap file dan memperbarui seluruh `import` di seluruh proyek agar sesuai.

#### [DELETE]
- Menghapus folder `com.lifeos.be.domain` lama.

---

## Verification Plan

### Automated Tests
- Menyesuaikan paket penulisan test case (`UserTest.java`, `ActivityServiceTest.java`) ke package struktur baru.
- Jalankan test suite:
  ```bash
  .\gradlew.bat test
  ```
