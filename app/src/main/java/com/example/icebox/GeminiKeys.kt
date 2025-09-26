package com.example.icebox

object GeminiKeys {
    /**
     * 저장소에 포함된 플레이스홀더 키입니다. 실제 서비스에서는 반드시 교체하세요.
     */
    const val PLACEHOLDER_API_KEY: String = "YOUR_GEMINI_API_KEY"

    /**
     * 앱에 기본으로 포함할 Gemini API 키.
     * 실제 배포 시에는 안전한 저장소나 서버에서 불러오는 방식으로 교체하세요.
     */
    const val DEFAULT_API_KEY: String = PLACEHOLDER_API_KEY

    fun isPlaceholder(apiKey: String): Boolean {
        return apiKey.isBlank() || apiKey == PLACEHOLDER_API_KEY
    }
}