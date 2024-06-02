# Audio Library
 
This is a Java-based command-line application for managing an audio library. The application supports user registration, login, playlist creation, song addition, exporting playlists to CSV or JSON formats and saving changes in an audit.

## Features

**User authetication**
- Register as a new user (First registered user gets admin privileges)
- Login into an existing account

**Authenticated Users**
- View all songs
- View all playlists
- View songs in a specified playlist
- Create a new playlist
- Add songs to playlist
- Export playlist to CSV or JSON file

**Admin Users**
- All features available for the authenticated users
- Creating new songs
- View all users
- Promote user to admin
- View audit of a user

**Technologies Used
- **Java**: Programming language
- **MySQL**: Database management system
- **JDBC**: Java Database Connectivity for interacting with MySQL

**Usage
- Upon starting, the application will be in anonymous mode. You can either register a new user or login with an existing account.
- The first registered user will be assigned the admin role by default.
- Admin users have additional capabilities such as promoting users, adding songs, and auditing user commands.
- Authenticated users can create playlists, add songs to playlists, count songs in a playlist, and export playlists.
- To export a playlist, choose the export option in the authenticated user menu and specify the desired format (CSV or JSON).