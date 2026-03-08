package org.cpimtn.marxist.android.data.source.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

/**
 * FTS4 virtual table mirroring searchable fields from PostEntity.
 *
 * Why FTS4 over FTS5:
 *  - Room has first-class @Fts4 support
 *  - FTS4 is sufficient for prefix matching + ranking
 *  - Smaller index size for offline-first apps
 *
 * The contentEntity links this to PostEntity so Room
 * keeps the FTS index in sync automatically.
 *
 * Tokenizer: unicode61 handles Tamil script correctly —
 * splits on Unicode word boundaries, not just ASCII spaces.
 */
@Fts4(
    contentEntity = PostEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
)
@Entity(tableName = "posts_fts")
data class PostFts(
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,
)