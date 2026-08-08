package com.reown.android.internal.common.modal.data.network.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Test

internal class WalletDTOTest {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(WalletDTO::class.java)

    @Test
    fun `parse wallet when image_id is null`() {
        val walletDTO = adapter.fromJson(walletJson(imageIdField = """"image_id": null,"""))

        assert(walletDTO != null)
        assert(walletDTO!!.imageId == null)
    }

    @Test
    fun `parse wallet when image_id is missing`() {
        val walletDTO = adapter.fromJson(walletJson(imageIdField = ""))

        assert(walletDTO != null)
        assert(walletDTO!!.imageId == null)
    }

    @Test
    fun `parse wallet when image_id is present`() {
        val walletDTO = adapter.fromJson(walletJson(imageIdField = """"image_id": "abc-123","""))

        assert(walletDTO != null)
        assert(walletDTO!!.imageId == "abc-123")
    }

    private fun walletJson(imageIdField: String) = """
        {
          "id": "wallet1",
          "name": "Test Wallet",
          "homepage": "https://test.io/",
          $imageIdField
          "order": "1",
          "mobile_link": null,
          "desktop_link": null,
          "webapp_link": null,
          "app_store": null,
          "play_store": null,
          "link_mode": null
        }
    """.trimIndent()
}
