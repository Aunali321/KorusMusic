package li.auna.korusmusic.di

import org.koin.dsl.module
import li.auna.korusmusic.data.repository.AuthRepositoryImpl
import li.auna.korusmusic.data.repository.SongRepositoryImpl
import li.auna.korusmusic.domain.repository.AuthRepository
import li.auna.korusmusic.domain.repository.SongRepository

val repositoryModule = module {
    
    single<AuthRepository> { 
        AuthRepositoryImpl(get(), get(), get()) 
    }
    
    single<SongRepository> { 
        SongRepositoryImpl(get(), get(), get()) 
    }
    
    // TODO: Add other repository implementations
    // single<AlbumRepository> { AlbumRepositoryImpl(get(), get(), get()) }
    // single<ArtistRepository> { ArtistRepositoryImpl(get(), get(), get()) }
    // single<PlaylistRepository> { PlaylistRepositoryImpl(get(), get(), get()) }
}