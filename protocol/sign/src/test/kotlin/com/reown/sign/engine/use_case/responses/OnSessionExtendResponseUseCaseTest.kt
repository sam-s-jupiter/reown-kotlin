package com.reown.sign.engine.use_case.responses

import com.reown.android.internal.common.JsonRpcResponse
import com.reown.android.internal.common.model.Expiry
import com.reown.android.internal.common.model.WCResponse
import com.reown.foundation.common.model.PublicKey
import com.reown.foundation.common.model.Topic
import com.reown.foundation.util.Logger
import com.reown.sign.common.model.vo.clientsync.session.params.SignParams
import com.reown.sign.common.model.vo.sequence.SessionVO
import com.reown.sign.storage.sequence.SessionStorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class OnSessionExtendResponseUseCaseTest {
    private val sessionStorageRepository = mockk<SessionStorageRepository>(relaxed = true)
    private val logger = mockk<Logger>(relaxed = true)
    private val useCase = OnSessionExtendResponseUseCase(sessionStorageRepository, logger)

    private val topic = Topic("topic")
    private val storedExpiry = 1_800_000_000L
    private val requestedExpiry = storedExpiry + 4 * 24 * 60 * 60

    @Before
    fun setUp() {
        every { sessionStorageRepository.isSessionValid(topic) } returns true
        every { sessionStorageRepository.getSessionWithoutMetadataByTopic(topic) } returns session(storedExpiry)
    }

    @Test
    fun `stores the requested expiry once the wallet accepts the extend`() = runTest {
        useCase(response(JsonRpcResponse.JsonRpcResult(id = 1L, result = true)), SignParams.ExtendParams(requestedExpiry))

        verify(exactly = 1) { sessionStorageRepository.extendSession(topic, requestedExpiry) }
    }

    @Test
    fun `keeps the previous expiry when the wallet rejects the extend`() = runTest {
        val rejected = JsonRpcResponse.JsonRpcError(id = 1L, error = JsonRpcResponse.Error(code = 3005, message = "Unauthorized extend request"))

        useCase(response(rejected), SignParams.ExtendParams(requestedExpiry))

        verify(exactly = 0) { sessionStorageRepository.extendSession(any(), any()) }
    }

    @Test
    fun `a late response to an older extend does not shorten the stored expiry`() = runTest {
        useCase(response(JsonRpcResponse.JsonRpcResult(id = 1L, result = true)), SignParams.ExtendParams(storedExpiry - 60))

        verify(exactly = 0) { sessionStorageRepository.extendSession(any(), any()) }
    }

    @Test
    fun `ignores an accepted extend for a session that is gone`() = runTest {
        every { sessionStorageRepository.isSessionValid(topic) } returns false

        useCase(response(JsonRpcResponse.JsonRpcResult(id = 1L, result = true)), SignParams.ExtendParams(requestedExpiry))

        verify(exactly = 0) { sessionStorageRepository.extendSession(any(), any()) }
    }

    private fun response(response: JsonRpcResponse) =
        WCResponse(topic, "wc_sessionExtend", response, SignParams.ExtendParams(requestedExpiry))

    private fun session(expiry: Long) = SessionVO(
        topic = topic,
        expiry = Expiry(expiry),
        relayProtocol = "relayProtocol",
        relayData = "relayData",
        controllerKey = PublicKey(""),
        selfPublicKey = PublicKey(""),
        sessionNamespaces = emptyMap(),
        requiredNamespaces = emptyMap(),
        optionalNamespaces = emptyMap(),
        isAcknowledged = true,
        pairingTopic = "pairingTopic",
        transportType = null
    )
}
