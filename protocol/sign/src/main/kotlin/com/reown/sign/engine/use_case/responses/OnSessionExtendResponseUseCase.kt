package com.reown.sign.engine.use_case.responses

import com.reown.android.internal.common.JsonRpcResponse
import com.reown.android.internal.common.model.WCResponse
import com.reown.foundation.util.Logger
import com.reown.sign.common.model.vo.clientsync.session.params.SignParams
import com.reown.sign.storage.sequence.SessionStorageRepository
import kotlinx.coroutines.supervisorScope

/**
 * Stores the expiry a dApp-sent wc_sessionExtend asked for once the wallet accepts it.
 * A rejected extend (Swift WalletKit rejects every dApp extend) leaves the session on its previous expiry.
 */
internal class OnSessionExtendResponseUseCase(
    private val sessionStorageRepository: SessionStorageRepository,
    private val logger: Logger,
) {

    suspend operator fun invoke(wcResponse: WCResponse, params: SignParams.ExtendParams) = supervisorScope {
        try {
            val topic = wcResponse.topic
            when (val response = wcResponse.response) {
                is JsonRpcResponse.JsonRpcResult -> {
                    if (!sessionStorageRepository.isSessionValid(topic)) {
                        logger.error("Session extend accepted for a session that is gone on topic: $topic")
                        return@supervisorScope
                    }
                    val currentExpiry = sessionStorageRepository.getSessionWithoutMetadataByTopic(topic).expiry.seconds
                    // a late response to an older extend must not shorten a newer one
                    if (params.expiry > currentExpiry) {
                        sessionStorageRepository.extendSession(topic, params.expiry)
                    }
                    logger.log("Session extend accepted by the wallet on topic: $topic")
                }

                is JsonRpcResponse.JsonRpcError ->
                    logger.error("Session extend rejected by the wallet: ${response.errorMessage} on topic: $topic")
            }
        } catch (e: Exception) {
            logger.error("Session extend response handling failed: $e")
        }
    }
}
