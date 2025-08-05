package li.auna.korusmusic.data.mapper

import li.auna.korusmusic.data.database.entities.*
import li.auna.korusmusic.data.network.dto.*
import li.auna.korusmusic.domain.model.*

// Network DTOs to Domain Models
fun SongDto.toDomainModel(artist: Artist, album: Album): Song = Song(
    id = id,
    title = title,
    albumId = albumId,
    artistId = artistId,
    trackNumber = trackNumber,
    discNumber = discNumber,
    duration = duration,
    filePath = filePath,
    fileSize = fileSize,
    fileModified = fileModified,
    bitrate = bitrate,
    format = format,
    dateAdded = dateAdded,
    artist = artist,
    album = album
)

fun AlbumDto.toDomainModel(artist: Artist? = null, albumArtist: Artist? = null): Album = Album(
    id = id,
    name = name,
    artistId = artistId,
    albumArtistId = albumArtistId,
    year = year,
    musicbrainzId = musicbrainzId,
    coverPath = coverPath,
    dateAdded = dateAdded,
    artist = artist,
    albumArtist = albumArtist,
    songCount = songCount,
    duration = duration
)

fun ArtistDto.toDomainModel(): Artist = Artist(
    id = id,
    name = name,
    sortName = sortName,
    musicbrainzId = musicbrainzId,
    albumCount = albumCount,
    songCount = songCount
)

fun PlaylistDto.toDomainModel(songs: List<Song> = emptyList()): Playlist = Playlist(
    id = id,
    name = name,
    description = description,
    userId = userId,
    isPublic = isPublic,
    createdAt = createdAt,
    updatedAt = updatedAt,
    songs = songs,
    songCount = songCount,
    duration = duration
)

fun UserDto.toDomainModel(): User = User(
    id = id,
    username = username,
    email = email,
    role = role,
    createdAt = createdAt,
    lastLogin = lastLogin
)

fun PlayHistoryDto.toDomainModel(song: Song): PlayHistory = PlayHistory(
    id = id,
    songId = songId,
    playedAt = playedAt,
    durationPlayed = durationPlayed,
    completed = completed,
    song = song
)

// Network DTOs to Database Entities
fun SongDto.toEntity(): SongEntity = SongEntity(
    id = id,
    title = title,
    albumId = albumId,
    artistId = artistId,
    trackNumber = trackNumber,
    discNumber = discNumber,
    duration = duration,
    filePath = filePath,
    fileSize = fileSize,
    fileModified = fileModified,
    bitrate = bitrate,
    format = format,
    dateAdded = dateAdded
)

fun AlbumDto.toEntity(): AlbumEntity = AlbumEntity(
    id = id,
    name = name,
    artistId = artistId,
    albumArtistId = albumArtistId,
    year = year,
    musicbrainzId = musicbrainzId,
    coverPath = coverPath,
    dateAdded = dateAdded,
    songCount = songCount,
    duration = duration
)

fun ArtistDto.toEntity(): ArtistEntity = ArtistEntity(
    id = id,
    name = name,
    sortName = sortName,
    musicbrainzId = musicbrainzId,
    albumCount = albumCount,
    songCount = songCount
)

fun PlaylistDto.toEntity(): PlaylistEntity = PlaylistEntity(
    id = id,
    name = name,
    description = description,
    userId = userId,
    isPublic = isPublic,
    createdAt = createdAt,
    updatedAt = updatedAt,
    songCount = songCount,
    duration = duration
)

fun PlayHistoryDto.toEntity(): PlayHistoryEntity = PlayHistoryEntity(
    id = id,
    songId = songId,
    playedAt = playedAt,
    durationPlayed = durationPlayed,
    completed = completed
)

// Database Entities to Domain Models
fun SongEntity.toDomainModel(artist: Artist, album: Album): Song = Song(
    id = id,
    title = title,
    albumId = albumId,
    artistId = artistId,
    trackNumber = trackNumber,
    discNumber = discNumber,
    duration = duration,
    filePath = filePath,
    fileSize = fileSize,
    fileModified = fileModified,
    bitrate = bitrate,
    format = format,
    dateAdded = dateAdded,
    artist = artist,
    album = album,
    isLiked = isLiked,
    playCount = playCount,
    lastPlayed = lastPlayed
)

fun AlbumEntity.toDomainModel(artist: Artist? = null, albumArtist: Artist? = null): Album = Album(
    id = id,
    name = name,
    artistId = artistId,
    albumArtistId = albumArtistId,
    year = year,
    musicbrainzId = musicbrainzId,
    coverPath = coverPath,
    dateAdded = dateAdded,
    artist = artist,
    albumArtist = albumArtist,
    songCount = songCount,
    duration = duration,
    isLiked = isLiked
)

fun ArtistEntity.toDomainModel(): Artist = Artist(
    id = id,
    name = name,
    sortName = sortName,
    musicbrainzId = musicbrainzId,
    albumCount = albumCount,
    songCount = songCount,
    isFollowed = isFollowed
)

fun PlaylistEntity.toDomainModel(songs: List<Song> = emptyList()): Playlist = Playlist(
    id = id,
    name = name,
    description = description,
    userId = userId,
    isPublic = isPublic,
    createdAt = createdAt,
    updatedAt = updatedAt,
    songs = songs,
    songCount = songCount,
    duration = duration
)

fun PlayHistoryEntity.toDomainModel(song: Song): PlayHistory = PlayHistory(
    id = id,
    songId = songId,
    playedAt = playedAt,
    durationPlayed = durationPlayed,
    completed = completed,
    song = song
)