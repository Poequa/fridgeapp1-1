package com.example.icebox

import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.icebox.databinding.ActivityRecipeChatBinding
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val GEMINI_MODEL_NAME = "gemini-2.5-flash"

class RecipeChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeChatBinding
    private var fridgeItems: List<FridgeItemWithName> = emptyList()
    private val chatBuilder = SpannableStringBuilder()
    private var hasGeneratedRecipe = false
    private var lastRecipeName: String? = null

    private var generativeModel: GenerativeModel? = null
    private var cachedApiKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.recipeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.recipeToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val bottomNav = binding.root.findViewById<BottomNavigationView>(R.id.bottom_nav)
        setupBottomNavigation(bottomNav, R.id.nav_recipe)

        binding.ingredientsList.text = getString(R.string.recipe_chat_loading_ingredients)

        binding.generateButton.setOnClickListener {
            requestRecipe()
        }

        loadIngredients()
    }

    private fun loadIngredients() {
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(applicationContext)
                    .fridgeDao()
                    .getAllFridgeItemsWithName()
            }
            fridgeItems = items
            updateIngredientList(items)
        }
    }

    private fun updateIngredientList(items: List<FridgeItemWithName>) {
        if (items.isEmpty()) {
            binding.ingredientsList.text = getString(R.string.recipe_chat_empty_fridge)
        } else {
            val text = items.joinToString(separator = "\n") { item ->
                val quantity = item.quantity
                val quantityPart = if (quantity > 0) " x$quantity" else ""
                "• ${item.name}$quantityPart (유통기한 ${item.expiryDate})"
            }
            binding.ingredientsList.text = text
        }
    }

    private fun requestRecipe() {
        val items = fridgeItems
        if (items.isEmpty()) {
            Snackbar.make(binding.root, R.string.recipe_chat_need_ingredients, Snackbar.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            setLoading(true)
            try {
                val model = ensureGenerativeModel() ?: run {
                    appendMessage(false, getString(R.string.recipe_chat_missing_api_key_body))
                    return@launch
                }

                appendMessage(true, getString(
                    if (hasGeneratedRecipe) R.string.recipe_chat_user_prompt_another else R.string.recipe_chat_user_prompt
                ))

                val prompt = buildPrompt(items, lastRecipeName)
                val response = withContext(Dispatchers.IO) {
                    model.generateContent(prompt)
                }
                val answer = response.text?.trim().orEmpty()
                val finalAnswer = if (answer.isBlank()) {
                    Snackbar.make(
                        binding.root,
                        R.string.recipe_chat_empty_response_message,
                        Snackbar.LENGTH_LONG
                    ).show()
                    buildFallbackRecipe(items)
                } else {
                    answer
                }
                appendMessage(false, finalAnswer)
                lastRecipeName = extractRecipeName(finalAnswer) ?: lastRecipeName
                if (!hasGeneratedRecipe && finalAnswer.isNotBlank()) {
                    hasGeneratedRecipe = true
                    binding.generateButton.text = getString(R.string.recipe_chat_action_next)
                }
            } catch (exception: Exception) {
                handleRecipeFailure(exception, items)
            } finally {
                setLoading(false)
            }
        }
    }

    private fun ensureGenerativeModel(): GenerativeModel? {
        val apiKey = resolveApiKey()
        if (apiKey.isBlank()) {
            showMissingApiKeySnackbar()
            return null
        }

        if (generativeModel != null && cachedApiKey == apiKey) {
            return generativeModel
        }

        return GenerativeModel(
            modelName = GEMINI_MODEL_NAME,
            apiKey = apiKey
        ).also { createdModel ->
            generativeModel = createdModel
            cachedApiKey = apiKey
        }
    }

    private fun resolveApiKey(): String {
        val savedKey = GeminiKeyStore.getSavedKey(this)
        if (savedKey.isNotBlank()) {
            return savedKey
        }

        val buildConfigKey = BuildConfig.GEMINI_API_KEY
        if (!GeminiKeys.isPlaceholder(buildConfigKey)) {
            return buildConfigKey
        }

        val bundledKey = GeminiKeys.DEFAULT_API_KEY
        return if (GeminiKeys.isPlaceholder(bundledKey)) "" else bundledKey
    }

    private fun showMissingApiKeySnackbar() {
        Snackbar.make(
            binding.root,
            R.string.recipe_chat_missing_api_key_snackbar,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.gemini_api_settings_action) {
            startActivity(Intent(this, GeminiApiSettingsActivity::class.java))
        }.show()
    }

    private fun handleRecipeFailure(exception: Exception, items: List<FridgeItemWithName>) {
        Log.e(TAG, "Gemini request failed", exception)
        val message = when {
            exception.message?.contains("PERMISSION_DENIED", true) == true ->
                getString(R.string.recipe_chat_error_unauthorized)
            exception.message?.contains("API key", true) == true ->
                getString(R.string.recipe_chat_error_unauthorized)
            else -> getString(R.string.recipe_chat_error_generic_message)
        }

        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        val fallback = buildFallbackRecipe(items)
        appendMessage(false, fallback)
        lastRecipeName = extractRecipeName(fallback) ?: lastRecipeName
        if (!hasGeneratedRecipe && fallback.isNotBlank()) {
            hasGeneratedRecipe = true
            binding.generateButton.text = getString(R.string.recipe_chat_action_next)
        }
    }

    private fun buildFallbackRecipe(items: List<FridgeItemWithName>): String {
        val ingredientNames = items.map { it.name }
        val headlineIngredients = ingredientNames.take(3)
        val recipeName = when (headlineIngredients.size) {
            0 -> getString(R.string.recipe_chat_fallback_recipe_name_generic)
            1 -> getString(R.string.recipe_chat_fallback_recipe_name_single, headlineIngredients.first())
            2 -> getString(
                R.string.recipe_chat_fallback_recipe_name_double,
                headlineIngredients[0],
                headlineIngredients[1]
            )
            else -> getString(
                R.string.recipe_chat_fallback_recipe_name_multi,
                headlineIngredients[0],
                headlineIngredients[1],
                headlineIngredients[2]
            )
        }

        val preparedList = if (ingredientNames.isEmpty()) {
            getString(R.string.recipe_chat_fallback_no_ingredient_hint)
        } else {
            ingredientNames.joinToString(separator = ", ")
        }

        return buildString {
            appendLine(getString(R.string.recipe_chat_fallback_notice))
            appendLine("레시피 이름: $recipeName")
            appendLine(
                getString(
                    R.string.recipe_chat_fallback_step1,
                    preparedList
                )
            )
            appendLine(getString(R.string.recipe_chat_fallback_step2))
            appendLine(getString(R.string.recipe_chat_fallback_step3))
            appendLine(getString(R.string.recipe_chat_fallback_step4))
            appendLine(getString(R.string.recipe_chat_fallback_step5))
        }
    }

    private fun buildPrompt(items: List<FridgeItemWithName>, excludedRecipe: String?): String {
        val sortedByExpiry = items.sortedBy { it.normalizedExpiryDate() }
        val ingredientList = sortedByExpiry.joinToString(separator = "\n") {
            "- ${it.name} (${it.quantity}개, 유통기한 ${it.expiryDate})"
        }

        val priorityList = sortedByExpiry.take(5).joinToString(separator = "\n") { item ->
            "• ${item.name} (유통기한 ${item.expiryDate})"
        }

        val proteinCandidates = sortedByExpiry.filter { it.isProteinCandidate() }
        val proteinGuidance = if (proteinCandidates.size > 1) {
            proteinCandidates.joinToString(separator = ", ") { it.name }
        } else {
            null
        }
        return buildString {
            appendLine("당신은 한국어를 사용하는 전문 셰프 AI입니다.")
            appendLine("아래 재료를 활용해 가장 대중적인 요리 한 가지를 추천해주세요.")
            appendLine("추천할 요리는 반드시 실제로 존재하는 한국, 일본, 서양, 중국 요리 중 하나여야 합니다.")
            appendLine("재료 이름을 단순히 나열해서 만든 요리명(예: '토마토 버섯 소고기 볶음')은 절대 사용하지 마세요.")
            appendLine("예: '소고기 토마토 파스타', '버섯전골', '돼지고기 김치찌개', '오야코동' 처럼 널리 알려진 요리명을 사용하세요.")
            appendLine("유통기한이 임박한 재료를 우선 활용하되, 조합이 어색하면 과감히 제외해도 됩니다.")
            appendLine("가능하다면 한 가지 단백질(육류/해산물/달걀 등)과 그에 맞는 채소나 곁들임 재료만 선택하세요.")
            if (priorityList.isNotBlank()) {
                appendLine("우선 사용 권장 재료 (유통기한 임박 순):")
                appendLine(priorityList)
            }
            if (!excludedRecipe.isNullOrBlank()) {
                appendLine("이전에 추천한 '${excludedRecipe.trim()}'은(는) 제외하고 새로운 요리를 추천해주세요.")
            }
            appendLine("답변은 반드시 다음 형식을 지키세요:")
            appendLine("레시피 이름: [실제 존재하는 요리 이름]")
            appendLine("[1단계] ...")
            appendLine("[2단계] ...")
            appendLine("[3단계] ...")
            appendLine("[4단계] ...")
            appendLine("[5단계] ...")
            appendLine("각 단계는 한두 문장으로 명확하게 작성하세요. 단계는 3~6단계 사이로 자유롭게 조정 가능합니다.")
            appendLine("특히 다음 단백질 중에서 한 가지만 선택하세요: ${proteinGuidance ?: "제공된 단백질 중 하나"}")
            appendLine("재료 목록:")
            append(ingredientList)
        }
    }

    private fun FridgeItemWithName.normalizedExpiryDate(): String {
        val raw = expiryDate
        val normalized = raw.takeIf { EXPIRY_REGEX.matches(it) }
        return normalized ?: "9999-12-31"
    }

    private fun FridgeItemWithName.isProteinCandidate(): Boolean {
        val nameLower = name.lowercase()
        val keywords = listOf(
            "고기",
            "돼지",
            "돼지고기",
            "소고기",
            "소",
            "닭",
            "닭고기",
            "계육",
            "오리",
            "양고기",
            "삼겹",
            "목살",
            "해산물",
            "생선",
            "새우",
            "오징어",
            "문어",
            "홍합",
            "조개"
        )
        return keywords.any { keyword -> nameLower.contains(keyword) } || category.contains("육", ignoreCase = true) || category.contains("해", ignoreCase = true)
    }

    companion object {
        private val EXPIRY_REGEX = Regex("\\d{4}-\\d{2}-\\d{2}")
        private const val TAG = "RecipeChat"
    }

    private fun appendMessage(isUser: Boolean, message: String) {
        if (message.isBlank()) return
        if (chatBuilder.isNotEmpty()) {
            chatBuilder.append("\n\n")
        }
        val prefix = if (isUser) {
            getString(R.string.recipe_chat_user_label)
        } else {
            getString(R.string.recipe_chat_assistant_label)
        }
        val prefixStart = chatBuilder.length
        chatBuilder.append(prefix)
        chatBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            prefixStart,
            chatBuilder.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        chatBuilder.append("\n")
        val messageStart = chatBuilder.length
        chatBuilder.append(message.trim())
        if (!isUser) {
            styleRecipeResult(messageStart, chatBuilder.length)
        }
        binding.chatHistory.text = chatBuilder
        binding.chatScroll.post {
            binding.chatScroll.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressIndicator.isVisible = isLoading
        binding.generateButton.isEnabled = !isLoading
    }

    private fun styleRecipeResult(messageStart: Int, messageEnd: Int) {
        if (messageEnd <= messageStart) return
        val accentColor = ContextCompat.getColor(this, R.color.mint_primary)
        val messageText = chatBuilder.subSequence(messageStart, messageEnd).toString()
        val regex = Regex("레시피 이름\\s*[:：]\\s*(.+)")
        val match = regex.find(messageText)
        if (match != null) {
            val lineStart = messageStart + match.range.first
            val lineEnd = messageStart + match.range.last + 1
            chatBuilder.setSpan(
                StyleSpan(Typeface.BOLD),
                lineStart,
                lineEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            chatBuilder.setSpan(
                RelativeSizeSpan(1.08f),
                lineStart,
                lineEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            chatBuilder.setSpan(
                ForegroundColorSpan(accentColor),
                lineStart,
                lineEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        // 강조용으로 단계 머리표도 굵게 처리
        val stepRegex = Regex("\\[[0-9]+단계]", RegexOption.MULTILINE)
        stepRegex.findAll(messageText).forEach { result ->
            val spanStart = messageStart + result.range.first
            val spanEnd = messageStart + result.range.last + 1
            chatBuilder.setSpan(
                StyleSpan(Typeface.BOLD),
                spanStart,
                spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun extractRecipeName(answer: String): String? {
        val regex = Regex("레시피 이름\\s*[:：]\\s*(.+)")
        return regex.find(answer)?.groupValues?.get(1)?.trim()?.takeIf { it.isNotBlank() }
    }
}