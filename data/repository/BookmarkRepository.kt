package com.prepcommerce.app.data.repository

import com.prepcommerce.app.data.local.dao.UserDataDao
import com.prepcommerce.app.data.local.entities.BookmarkEntity
import com.prepcommerce.app.data.local.entities.ReportedQuestionEntity

class BookmarkRepository(private val dao: UserDataDao) {
    fun allBookmarks() = dao.getAllBookmarks()
    suspend fun toggle(questionId: String, subject: String) {
        if (dao.getBookmark(questionId) != null) dao.deleteBookmark(questionId)
        else dao.insertBookmark(BookmarkEntity(questionId, subject, System.currentTimeMillis()))
    }
    suspend fun isBookmarked(questionId: String) = dao.getBookmark(questionId) != null
    suspend fun report(questionId: String) = dao.insertReport(ReportedQuestionEntity(questionId = questionId, timestamp = System.currentTimeMillis()))
}
