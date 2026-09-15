package com.prepcommerce.app.data.repository

class SamplePaperRepository(private val contentRepository: ContentRepository) {
    suspend fun papersFor(subject: String, classLevel: Int, board: String) =
        contentRepository.samplePapers(subject, classLevel, board)
    suspend fun paper(id: String) = contentRepository.samplePaper(id)
}
