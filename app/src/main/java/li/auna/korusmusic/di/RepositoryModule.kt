package li.auna.korusmusic.di

import org.koin.dsl.module
import li.auna.korusmusic.data.repository.AlbumRepositoryImpl
import li.auna.korusmusic.data.repository.ArtistRepositoryImpl
import li.auna.korusmusic.data.repository.AuthRepositoryImpl
import li.auna.korusmusic.data.repository.LyricsRepositoryImpl
import li.auna.korusmusic.data.repository.PlaylistRepositoryImpl
import li.auna.korusmusic.data.repository.SongRepositoryImpl
import li.auna.korusmusic.domain.repository.AlbumRepository
import li.auna.korusmusic.domain.repository.ArtistRepository
import li.auna.korusmusic.domain.repository.AuthRepository
import li.auna.korusmusic.domain.repository.LyricsRepository
import li.auna.korusmusic.domain.repository.PlaylistRepository
import li.auna.korusmusic.domain.repository.SongRepository

val repositoryModule = module {
    
    single<AuthRepository> { 
        AuthRepositoryImpl(get(), get(), get()) 
    }
    
    single<SongRepository> { 
        SongRepositoryImpl(get(), get(), get()) 
    }
    
    single<AlbumRepository> { 
        AlbumRepositoryImpl(get(), get(), get()) 
    }
    
    single<ArtistRepository> { 
        ArtistRepositoryImpl(get(), get(), get()) 
    }
    
    single<PlaylistRepository> { 
        PlaylistRepositoryImpl(get(), get(), get()) 
    }
    
    single<LyricsRepository> { 
        LyricsRepositoryImpl(get(), get(), get()) 
    }
}