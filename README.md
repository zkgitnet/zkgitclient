# ZK Git Client

## General

The **ZK Git Client** runs in the background on the user’s computer. It handles the **ZK encryption process**, including client-side compression, encryption, and communication with the remote server. It also manages user account operations.

## Main Menu

At the top of the application, a status page displays the current connectivity and login status.

### Menu Options

1. **Login to the account**  
   Required credentials:
   - **Account Number:** `29946 08306 62169 28080 67896`
   - **Username:** `VibrantRobot`
   - **TOTP Secret:** `3TS6ENPHQLKD4MQ2Y3WUHOQJ` *(Import into an authenticator app)*
   - **Password:** `ScnjH&gh!Gt8zT1Ks7*B`

2. **Logout of the account**  
   Securely erases all keys from memory.

3. **Display current status**  
   Shows application connectivity and login status.

4. **View account information**  
   Displays data such as maximum storage and user limits.

5. **Change port**  
   Sets the port the application listens on for connections from the ZK Git Helper.

6. **List users**  
   Shows all users linked to the account.

7. **Create a new user**

8. **Toggle user/admin privileges**

9. **Change password**

10. **Delete a user**

11. **(Not part of test implementation)**

12. **(Not part of test implementation)**

13. **List all repositories**

14. **Delete a repository from the remote server**

15. **Export encryption keys**  
    Creates a backup of your encryption keys.

**E. Exit**  
Exits the application and securely deletes keys from memory.

## Run the Application

```java -jar jar/zkgitclient.jar```
Use menu point 1) to login to the account before repository data can be pushed, pulled or cloned.
 
## Compile from Source
 
```mvn package```
```java -jar target/zkgitclient.jar```
Use menu point 1) to login to the account before repository data can be pushed, pulled or cloned.
 
## Configurations
Configuration options are found in the class AppConfig.java.

In particular, the Log Level can be changed.
