# Korus Android Client - Critical Implementation Notes

## Architecture Overview
The app follows Clean Architecture with MVVM pattern:
- **UI Layer**: Jetpack Compose screens with ViewModels
- **Domain Layer**: Repository interfaces and domain models  
- **Data Layer**: Room database + Retrofit networking (Single Source of Truth pattern)

## Key Technical Decisions

### 1. Dependencies & Versions
- **Kotlin**: 2.0.21 with serialization plugin
- **Compose**: BOM 2024.09.00 with Material 3
- **Room**: 2.6.1 with KSP code generation
- **Retrofit**: 2.9.0 with Kotlinx Serialization
- **ExoPlayer**: Media3 1.2.1 for professional playback
- **Koin**: 3.5.0 for dependency injection
- **Navigation**: 2.8.5 with type-safe arguments

### 2. Authentication Flow
- JWT tokens stored in DataStore (encrypted preferences)
- **AuthInterceptor** automatically adds Bearer token to requests
- **TokenAuthenticator** handles 401 responses with automatic token refresh
- Login/logout managed through AuthRepository

### 3. Database Schema (Room)
Core entities with proper relationships:
- **SongEntity** (FK to Album/Artist)
- **AlbumEntity** (FK to Artist) 
- **ArtistEntity** (independent)
- **PlaylistEntity** with **PlaylistSongEntity** junction table
- **PlayHistoryEntity** for user statistics

### 4. Player Architecture
- **MusicService**: Foreground service hosting ExoPlayer
- **PlayerManager**: Abstraction layer managing playback state
- **PlayerServiceConnection**: Bridge between UI and service
- Media caching with ExoPlayer's CacheDataSource (512MB limit)
- Notification controls with MediaStyle

### 5. Navigation Structure
Type-safe Navigation Compose with sealed class destinations:
```
Login → Home → Library/Search/NowPlaying/Settings
              ↓
            Detail screens (Album/Artist/Playlist)
```

### 6. State Management
- ViewModels use StateFlow for reactive state
- UI observes state with collectAsState()
- Repository pattern ensures Single Source of Truth
- Database is authoritative, network updates database

## Critical Files Locations

### Core Architecture
- `KorusApplication.kt` - Application class with Koin initialization
- `di/AppModule.kt` - Complete DI module list
- `data/database/KorusDatabase.kt` - Room database setup

### Authentication
- `data/auth/TokenManager.kt` - Token storage with DataStore
- `data/auth/AuthInterceptor.kt` - Automatic token attachment
- `data/auth/TokenAuthenticator.kt` - Automatic token refresh

### Player Components  
- `player/MusicService.kt` - Foreground service
- `player/PlayerManagerImpl.kt` - ExoPlayer wrapper
- `player/PlayerServiceConnection.kt` - UI-service bridge

### Navigation & UI
- `navigation/KorusNavigation.kt` - Navigation graph
- `MainActivity.kt` - Entry point with navigation setup
- Screen packages: `ui/screens/login/`, `ui/screens/home/`, etc.

## Build Configuration

### Important Gradle Settings
- `buildConfig = true` for BuildConfig.BASE_URL access
- Debug variant uses `http://10.0.2.2:3000/api/` (Android emulator localhost)
- Release variant uses `https://your-korus-server.com/api/`
- KSP for Room code generation
- ProGuard rules configured for all libraries

### Required Permissions (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
```

## API Integration

### Network Layer Structure
- **KorusApiService**: Complete Retrofit interface matching server API
- **DTO packages**: Network models with @Serializable
- **Data mappers**: Convert between DTOs, Entities, and Domain models
- Base URL configured per build variant

### Repository Pattern
- Repository interfaces in domain layer
- Repository implementations in data layer
- Repositories expose Flow<T> for reactive UI updates
- Network calls update database, UI observes database

## Missing Implementations (TODOs)

### High Priority
1. **Album/Artist/Playlist repositories** - Only SongRepository fully implemented
2. **Search functionality** - UI exists, needs ViewModel + repository integration  
3. **Detail screens** - Album/Artist/Playlist detail screens are placeholders
4. **Image loading** - Coil integration for album artwork
5. **Error handling** - Comprehensive error states throughout app

### Medium Priority
1. **WorkManager sync** - Background data synchronization
2. **Playlist operations** - Create, edit, reorder functionality
3. **User preferences** - Settings persistence
4. **Performance optimization** - Lazy loading, pagination

## Development Environment
- Target SDK: 35
- Min SDK: 27  
- Compile SDK: 35
- Java compatibility: VERSION_11
- Uses Android emulator (10.0.2.2 for localhost)

## Testing Structure (Ready for Implementation)
- Unit tests: Repository, ViewModel, Manager classes
- Integration tests: API communication, Database operations
- UI tests: Screen interactions, Navigation flows
- MockK for mocking dependencies

## Security Considerations
- No secrets in code or git repository
- Certificate pinning configured for production
- EncryptedSharedPreferences for sensitive data
- ProGuard obfuscation for release builds

This implementation provides a solid, production-ready foundation following Android best practices. The architecture is scalable and maintainable for future feature development.