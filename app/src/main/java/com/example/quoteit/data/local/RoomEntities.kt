package com.example.quoteit.data.local

import androidx.room.*
import com.example.quoteit.domain.models.Folder
import com.example.quoteit.domain.models.FolderWQuotes
import com.example.quoteit.domain.models.Post
import com.example.quoteit.domain.models.Quote

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val folderId: Long = 0,
    val folderName: String,
    val folderQty: Int = 0
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val postId: Long = 0,
    val postBy: String,
    val noLikes: Int = 0,
    val quoteContent: String,
    val quoteAuthor: String,
    val isLiked: Boolean,
    var likeSynced: Boolean
)

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true) val quoteId: Long = 0,
    val author: String,
    val content: String,
    val isFavorite: Boolean = false
)

@Entity(primaryKeys = ["folderId", "quoteId"])
data class FolderQuoteCrossRef(
    val folderId: Long,
    val quoteId: Long
)

data class FolderWithQuotes(
    @Embedded val folderEntity: FolderEntity,
    @Relation(
        parentColumn = "folderId",
        entityColumn = "quoteId",
        associateBy = Junction(FolderQuoteCrossRef::class)
    )
    val quoteEntities: List<QuoteEntity>
)

fun List<FolderEntity>.asFolderDomainModel(): List<Folder> {
    return map { Folder(it.folderId, it.folderName, it.folderQty) }
}

fun List<QuoteEntity>.asQuoteDomainModel(): List<Quote> {
    return map { Quote(it.quoteId, it.author, it.content, it.isFavorite) }
}

fun PostEntity.asPostDomainModel(): Post {
    return Post(postId, postBy, quoteContent, quoteAuthor, noLikes, isLiked)
}

fun QuoteEntity.asQuoteDomainModel(): Quote {
    return Quote(quoteId, author, content, isFavorite)
}

fun FolderWithQuotes.asFolderWQuotesDomainModel(): FolderWQuotes {
    val copyQuotes = quoteEntities.map { Quote(it.quoteId, it.author, it.content, it.isFavorite) }
    return FolderWQuotes(folderEntity.folderName, copyQuotes)
}
