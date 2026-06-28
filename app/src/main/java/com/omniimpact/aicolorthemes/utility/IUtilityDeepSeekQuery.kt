package com.omniimpact.aicolorthemes.utility

import com.omniimpact.aicolorthemes.model.IDeepSeekQuery
import kotlinx.coroutines.flow.Flow

/**
 * Interface for UtilityDeepSeekQuery to enable easy mocking.
 */
interface IUtilityDeepSeekQuery {
	/**
	 * Sends an AI query to the DeepSeek API and streams the result states via a Flow.
	 *
	 * @param query The query object containing the system and user prompts.
	 * @param T The type of the query and result model.
	 * @return A Flow of IDeepSeekResult representing Loading, Success, or Failure.
	 */
	fun <T : IDeepSeekQuery> send(query: T): Flow<IDeepSeekResult<T>>
}
