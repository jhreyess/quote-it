package com.example.quoteit.data

import com.example.quoteit.models.Folder

object Datasource {
    val folders: List<Folder> = listOf(
        Folder("Mis favoritos", 13),
        Folder("Hechos por mí", 6),
        Folder("Amor", 2),
        Folder("Energía", 5),
        Folder("Estados de ánimo", 3),
    )
}