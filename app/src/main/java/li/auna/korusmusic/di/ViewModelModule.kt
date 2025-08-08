package li.auna.korusmusic.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import li.auna.korusmusic.ui.screens.albumdetail.AlbumDetailViewModel
import li.auna.korusmusic.ui.screens.artistdetail.ArtistDetailViewModel
import li.auna.korusmusic.ui.screens.home.HomeViewModel
import li.auna.korusmusic.ui.screens.library.LibraryViewModel
import li.auna.korusmusic.ui.screens.login.LoginViewModel
import li.auna.korusmusic.ui.screens.nowplaying.NowPlayingViewModel
import li.auna.korusmusic.ui.screens.playlistdetail.PlaylistDetailViewModel
import li.auna.korusmusic.ui.screens.search.SearchViewModel
import li.auna.korusmusic.ui.screens.settings.SettingsViewModel

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { LibraryViewModel(get(), get(), get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get(), get()) }
    viewModel { NowPlayingViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
    
    // Detail ViewModels with SavedStateHandle
    viewModel {
        AlbumDetailViewModel(get(), get(), get(), get())
    }
    viewModel {
        ArtistDetailViewModel(get(), get(), get(), get(), get())
    }
    viewModel {
        PlaylistDetailViewModel(get(), get(), get(), get())
    }
}