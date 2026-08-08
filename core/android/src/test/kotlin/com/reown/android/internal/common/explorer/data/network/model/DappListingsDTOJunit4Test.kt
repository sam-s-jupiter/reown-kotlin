package com.reown.android.internal.common.explorer.data.network.model

import androidx.core.net.toUri
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class DappListingsDTOJunit4Test {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Test
    fun `serialize listing of all dapps from explorer api`() {
        val json = this.javaClass.classLoader!!.getResourceAsStream("DappListingsSample.json").readBytes().toString(Charsets.UTF_8)
        val adapter = moshi.adapter(DappListingsDTO::class.java)
        val dappListingsDTO = adapter.fromJson(json)

        assert(dappListingsDTO != null)
        assert(dappListingsDTO!!.listings.isNotEmpty())
        assert(dappListingsDTO.listings.size == dappListingsDTO.count)
    }

    @Test
    fun `parse listing when image_id is null`() {
        val adapter = moshi.adapter(DappListingsDTO::class.java)
        val dappListingsDTO = adapter.fromJson(dappListingsJson(imageIdField = """"image_id": null,"""))

        assert(dappListingsDTO != null)
        assert(dappListingsDTO!!.listings.values.first().imageId == null)
    }

    @Test
    fun `parse listing when image_id is missing`() {
        val adapter = moshi.adapter(DappListingsDTO::class.java)
        val dappListingsDTO = adapter.fromJson(dappListingsJson(imageIdField = ""))

        assert(dappListingsDTO != null)
        assert(dappListingsDTO!!.listings.values.first().imageId == null)
    }

    private fun dappListingsJson(imageIdField: String) = """
        {
          "listings": {
            "listing1": {
              "id": "listing1",
              "name": "Test Dapp",
              "description": null,
              "homepage": "https://test.io/",
              "chains": ["eip155:1"],
              "versions": ["1"],
              "sdks": ["sign_v1"],
              "app_type": "dapp",
              $imageIdField
              "image_url": {"sm": "", "md": "", "lg": ""},
              "app": {"browser": "", "ios": "", "android": "", "mac": "", "windows": "", "linux": "", "chrome": null, "firefox": null, "safari": null, "edge": null, "opera": null},
              "injected": null,
              "mobile": {"native": "", "universal": ""},
              "desktop": {"native": "", "universal": ""},
              "supported_standards": [],
              "metadata": {"shortName": "Test", "colors": {"primary": "", "secondary": ""}},
              "updatedAt": "2021-07-30T17:48:12.565532+00:00"
            }
          },
          "count": 1,
          "total": 1
        }
    """.trimIndent()

    @Test
    fun `Uri compareTo of the same uri with no paths`() {
        val uri1 = "https://gm.walletconnect.com".toUri()
        val uri2 = "https://gm.walletconnect.com".toUri()
        assert(uri1.compareTo(uri2) == 0)
    }

    @Test
    fun `Uri compareTo of the same uri with differing paths`() {
        val uri1 = "https://gm.walletconnect.com/path1".toUri()
        val uri2 = "https://gm.walletconnect.com/path2".toUri()
        assert(uri1.compareTo(uri2) != 0)
    }

    @Test
    fun `Comparing uri host of the same uri with differing paths`() {
        val uri1 = "https://gm.walletconnect.com/path1".toUri().host
        val uri2 = "https://gm.walletconnect.com/path2".toUri().host
        assertNotNull(uri1)
        assertNotNull(uri2)
        assert(uri1!!.contains(uri2.toString()))
    }

    @Test
    fun `Uri compareTo of the different uris`() {
        val uri1 = "https://gm.walletconnect.com".toUri()
        val uri2 = "https://pancakeswap.com".toUri()
        assert(uri1.compareTo(uri2) != 0)
    }
}