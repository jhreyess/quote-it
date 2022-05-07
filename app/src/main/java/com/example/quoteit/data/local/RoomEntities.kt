package com.example.quoteit.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quoteit.domain.models.Folder
import com.example.quoteit.domain.models.FolderWQuotes
import com.example.quoteit.domain.models.Quote

@Entity(tableName = "folder")
data class DatabaseFolder(
    @PrimaryKey(autoGenerate = true) val folderId: Long = 0,
    val folderName: String,
    val folderQty: Int = 0
)

@Entity(tableName = "quote")
data class DatabaseQuote(
    @PrimaryKey(autoGenerate = true) val quoteId: Long = 0,
    val author: String,
    val content: String
)

@Entity(primaryKeys = ["folderId", "quoteId"])
data class FolderQuoteCrossRef(
    val folderId: Long,
    val quoteId: Long
)

data class FolderWithQuotes(
    @Embedded val folder: DatabaseFolder,
    @Relation(
        parentColumn = "folderId",
        entityColumn = "quoteId",
        associateBy = Junction(FolderQuoteCrossRef::class)
    )
    val quotes: List<DatabaseQuote>
)

fun List<DatabaseFolder>.asDomainModel(): List<Folder> {
    return map { Folder(it.folderId, it.folderName, it.folderQty) }
}

fun FolderWithQuotes.asDomainModel(): FolderWQuotes {
    val copyQuotes = quotes.map { Quote(it.author, it.content) }
    return FolderWQuotes(folder.folderName, copyQuotes)
}
