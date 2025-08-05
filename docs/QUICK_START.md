# Korus Android Client - Quick Start Guide

## Current State
✅ **Fully functional foundation** with authentication, music playback, and core UI screens  
✅ **62.5% complete** - Phases 1-5 of 8 implemented  
✅ **Production-ready architecture** with Clean Architecture + MVVM

## To Continue Development

### 1. Understand the Architecture
- Read `DESIGN.md` for comprehensive architecture overview
- Check `IMPLEMENTATION_NOTES.md` for critical technical details  
- Review `PROGRESS.md` for current status and next priorities

### 2. Key Entry Points
- **MainActivity.kt** - App entry point with navigation
- **KorusNavigation.kt** - Complete navigation graph
- **di/AppModule.kt** - Dependency injection setup
- **KorusDatabase.kt** - Database schema and setup

### 3. Current Functionality
- **Login/Authentication** - Full JWT implementation with auto-refresh
- **Music Playback** - Professional ExoPlayer service with notifications
- **Library Browsing** - Songs list with basic UI (albums/artists/playlists are placeholders)
- **Home Screen** - Recently played/added sections with mini player
- **Settings** - Basic settings with logout functionality

### 4. Next Development Priorities

#### Immediate (Phase 6 - Advanced Features)
1. **Complete missing repositories** (`AlbumRepository`, `ArtistRepository`, `PlaylistRepository`)
2. **Implement search functionality** - Connect SearchScreen to API
3. **Add album artwork loading** - Integrate Coil for image loading
4. **Create detail screens** - Album, Artist, Playlist detail views

#### Code locations for next work:
```
data/repository/          # Add missing repository implementations
ui/screens/search/        # Add SearchViewModel and API integration  
ui/screens/detail/        # Create album/artist/playlist detail screens
ui/components/            # Add image loading components with Coil
```

### 5. Testing the App
- **Debug build** connects to `http://10.0.2.2:3000/api/` (Android emulator)
- Ensure Korus server is running locally on port 3000
- App supports login → library browsing → music playback flow

### 6. Common Development Tasks

#### Adding a new screen:
1. Create screen in `ui/screens/[name]/`
2. Create ViewModel in same package  
3. Add to `viewModelModule` in DI
4. Add route to `KorusDestination.kt`
5. Add composable to `KorusNavigation.kt`

#### Adding a new repository:
1. Create interface in `domain/repository/`
2. Create implementation in `data/repository/`
3. Add to `repositoryModule` in DI
4. Inject into ViewModels as needed

### 7. Build & Run
- **Debug**: Uses localhost API, full logging enabled
- **Release**: Production API, minified with ProGuard
- All dependencies configured, should build without issues

## Architecture Strengths
- ✅ **Single Source of Truth** - Room database is authoritative
- ✅ **Reactive UI** - StateFlow + Compose state management  
- ✅ **Professional Playback** - Background service with media controls
- ✅ **Secure Auth** - JWT with automatic refresh and encrypted storage
- ✅ **Clean Architecture** - Testable, maintainable, scalable
- ✅ **Modern Stack** - Latest Android libraries and best practices

The foundation is solid and ready for feature development!