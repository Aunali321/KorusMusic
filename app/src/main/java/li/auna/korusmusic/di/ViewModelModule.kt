package li.auna.korusmusic.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import li.auna.korusmusic.ui.screens.home.HomeViewModel
import li.auna.korusmusic.ui.screens.library.LibraryViewModel
import li.auna.korusmusic.ui.screens.login.LoginViewModel

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { LibraryViewModel(get()) }
}