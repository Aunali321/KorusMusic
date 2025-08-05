package li.auna.korusmusic.di

import org.koin.dsl.module

val appModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    playerModule,
    viewModelModule
)