package org.cpimtn.marxist.android.data.database.mapper

import android.text.Html
import org.cpimtn.marxist.android.data.database.entity.PostEntity
import org.cpimtn.marxist.android.domain.model.Post
import org.cpimtn.marxist.network.model.PostDTO

/**
 * Decodes HTML entities (e.g. &#8220;, &#8211;, &quot;) so title and excerpt display correctly.
 */
internal fun decodeHtmlEntities(html: String): String =
    Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString().trim()


/** Compiled once; used for stripping HTML from excerpt. */
private val HTML_TAG_REGEX = Regex("<[^>]+>")

/**
 * Maps DTO → Entity (for persistence). Single responsibility: mapping from network shape to DB shape.
 * Title and excerpt are decoded so entities like &#8220; and &#8211; render as “ and –.
 */
fun PostDTO.toEntity(): PostEntity = PostEntity(
    id = id,
    date = date,
    slug = slug,
    title = decodeHtmlEntities(title.rendered.replace(HTML_TAG_REGEX, "").trim()),
    excerpt = decodeHtmlEntities(excerpt.rendered.replace(HTML_TAG_REGEX, "").trim()),
    tagsId = tags ?: emptyList(),
    categoriesId = categories ?: emptyList()
)

/**
 * Maps Entity → Domain (for consumers). Single responsibility: mapping from DB shape to domain model.
 */
fun PostEntity.toDomain(): Post = Post(
    id = id,
    date = date,
    slug = slug,
    title = title,
    excerpt = excerpt,
    tags = tagsId,
    categories = categoriesId
)
