# Travel Seat and Reservation System

## How to run in IntelliJ IDEA
1. Unzip, then in IntelliJ: **File > Open** and choose the `pom.xml` inside this folder > **Open as Project**.
   (Open the folder that directly contains `pom.xml` and `src` - not a parent folder.)
2. Wait for the bottom progress bar. If a popup says "Maven project needs to be imported", click **Load Maven Changes**
   (or open the Maven tool window and click the circular **Reload** icon). You need internet the first time so the SQLite driver can download.
3. **File > Project Structure > Project**: set SDK to **JDK 17 or newer** (IntelliJ can download one: Add SDK > Download JDK) and Language level to 17+.
4. Open `src/main/java/Main.java` and click the green arrow next to `main` > **Run 'Main.main()'**.
5. Login: username `admin`, password `admin123`.

The database file `travel.db` is created/used in the project folder automatically.

## If it still does not run
- "release version 17 not supported" / "invalid source release" -> your JDK is older than 17 (step 3).
- "No suitable driver" / red `org.sqlite` -> Maven did not finish downloading; click Reload in the Maven tool window.
- "Cannot resolve symbol" everywhere -> right-click `src/main/java` > Mark Directory as > **Sources Root**.

---
## Deliverable 8 - Audit Log (checklist mapping)

| # | Event | Code location | Log format |
|---|-------|---------------|-----------|
| 1 | Login / Logout | `ui/Authentication.java` -> `login()` / `logout()` | `User juan (ID: 3) logged in.` |
| 2 | Account approval / role-status edit | `dao/UserDao.java` -> `approveUser()` / `updateUserRoleAndStatus()` | `Admin [1] approved account for User ID 12` / `Admin [1] changed role of User 5 to TEACHER` |
| 3 | Core business process (seat booking) | `dao/ReservationDao.java` -> `processBooking()` | `Processed booking #5: Seat 12 on Trip #1 (Cebu City -> Manila) for Juan - Total: P1500.00` |
| 4 | Lifecycle / state change | `dao/ReservationDao.java` -> `updateStatus()` | `Updated Reservation #5 status from BOOKED to CANCELLED` |

Other logged actions: `USER_REGISTERED`, `TRIP_CREATED`, `LOGS_CLEARED`.

Files: `dao/DatabaseInitializer.java` (creates `activity_logs` with `user_id ... REFERENCES users(id) ON DELETE CASCADE`),
`dao/ActivityLogDao.java` (`logAction`, `getAllLogs`, `getLogsByUserId`, `clearAllLogs` - all SQL via PreparedStatement),
`ui/AdminMenu.java` -> **6. View System Audit Logs** (view all / filter by User ID / clear with YES confirmation).

## Try it (for the screenshots)
1. Run `Main`, login `admin` / `admin123`.
2. Register a student (option 1), login as admin, **3. Approve Pending Accounts**, **2. Update User Role and Status**.
3. Login as the student: **2. Book a Seat**, then **4. Cancel a Reservation**, then Logout.
4. Login as admin > **6. View System Audit Logs** > View All (Screenshot 1). Try typing letters at a prompt to show input safety.
5. Open `travel.db` in Letos / DB Browser for SQLite > table `activity_logs` (Screenshot 2).
