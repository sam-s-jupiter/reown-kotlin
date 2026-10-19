import org.gradle.api.JavaVersion

const val KEY_PUBLISH_VERSION = "PUBLISH_VERSION"
const val KEY_PUBLISH_ARTIFACT_ID = "PUBLISH_ARTIFACT_ID"
const val KEY_PUBLISH_GROUP = "PUBLISH_GROUP"
const val KEY_SDK_NAME = "SDK_NAME"

const val DEFAULT_PUBLISH_GROUP = "com.reown"

//Latest versions
// Aster internal release: upstream 1.6.13 + scopedProperties deserialization fix
// + nullable image_id in explorer/modal DTOs + session extend sets expiry to now + 7d
// + extended expiry stored only after the wallet accepts the extend.
// Published to https://apx-nexus.toolsapple.net/repository/android via :publishToApollo.
// Bump the .N suffix on every release that needs to ship.
const val BOM_VERSION = "1.6.13-aster.4"
const val FOUNDATION_VERSION = "1.6.13-aster.4"
const val CORE_VERSION = "1.6.13-aster.4"
const val SIGN_VERSION = "1.6.13-aster.4"
const val NOTIFY_VERSION = "1.6.13-aster.4"
const val WALLETKIT_VERSION = "1.6.13-aster.4"
const val APPKIT_VERSION = "1.6.13-aster.4"
const val MODAL_CORE_VERSION = "1.6.13-aster.4"
const val POS_VERSION = "1.2.0"
const val PAY_VERSION = "1.0.13"

//Artifact ids
const val ANDROID_BOM = "android-bom"
const val FOUNDATION = "foundation"
const val ANDROID_CORE = "android-core"
const val SIGN = "sign"
const val NOTIFY = "notify"
const val WALLETKIT = "walletkit"
const val APPKIT = "appkit"
const val MODAL_CORE = "modal-core"
const val POS = "pos"

const val PAY = "pay"

val jvmVersion = JavaVersion.VERSION_11
const val MIN_SDK: Int = 23
const val TARGET_SDK: Int = 35
const val COMPILE_SDK: Int = 36
val SAMPLE_VERSION_CODE = BOM_VERSION.replace(Regex("[^0-9]"), "").toInt()
const val SAMPLE_VERSION_NAME = BOM_VERSION