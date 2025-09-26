package com.example.icebox

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.icebox.databinding.ActivityRecipeChatBinding
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val GEMINI_MODEL_NAME = "gemini-2.5-flash"

class RecipeChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeChatBinding
    private var fridgeItems: List<FridgeItemWithName> = emptyList()
    private val chatBuilder = StringBuilder()

    private var generativeModel: GenerativeModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.recipeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.recipeToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        appendMessage(false, getString(R.string.recipe_chat_intro))
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
            val userMessage = getString(R.string.recipe_chat_user_prompt)
            appendMessage(true, userMessage)
            setLoading(true)
            try {
                val model = ensureGenerativeModel()
                val answer = if (model != null) {
                    val prompt = buildPrompt(items)
                    val response = withContext(Dispatchers.IO) {
                        model.generateContent(prompt)
                    }
                    response.text?.trim().orEmpty()
                } else {
                    ""
                }

                if (answer.isBlank()) {
                    if (model == null) {
                        Snackbar.make(
                            binding.root,
                            R.string.recipe_chat_missing_api_key_fallback,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    appendMessage(false, buildFallbackRecipe(items))
                } else {
                    appendMessage(false, answer)
                }
            } catch (exception: Exception) {
                appendMessage(false, buildFallbackRecipe(items))
                Snackbar.make(binding.root, R.string.recipe_chat_error_generic, Snackbar.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun ensureGenerativeModel(): GenerativeModel? {
        generativeModel?.let { return it }

        val apiKey = BuildConfig.GEMINI_API_KEY.ifBlank { GeminiKeys.DEFAULT_API_KEY }
        if (apiKey.isBlank()) {
            return null
        }

        return GenerativeModel(
            modelName = GEMINI_MODEL_NAME,
            apiKey = apiKey
        ).also { createdModel ->
            generativeModel = createdModel
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

    private fun buildPrompt(items: List<FridgeItemWithName>): String {
        val ingredientList = items.joinToString(separator = "\n") {
            "- ${it.name} (${it.quantity}개, 유통기한 ${it.expiryDate})"
        }
        return buildString {
            appendLine("당신은 한국어를 사용하는 셰프 AI입니다.")
            appendLine("아래 재료만을 중심으로 만들 수 있는 가정식 요리 한 가지를 추천해주세요.")
            appendLine("굳이 아래 재료들만 통해서 만들지 않아도 됍니다.")
            appendLine("아래 재료들을 통해서 어떤 음식이 제일 괜찮을지 한 메뉴를 정해서 ")
            appendLine("답변은 반드시 다음 형식을 지키세요:")
            appendLine("레시피 이름: [요리 이름]")
            appendLine("1단계. ...")
            appendLine("2단계. ...")
            appendLine("3단계. ...")
            appendLine("4단계. ...")
            appendLine("5단계. ...")
            appendLine("반드시 다섯 단계(1단계부터 5단계까지)로만 설명하지 않아도 되고 각 단계는 한두 문장으로 명확하게 작성하세요.")
            appendLine("재료 목록:")
            append(ingredientList)
        }
    }

    private fun appendMessage(isUser: Boolean, message: String) {
        if (message.isBlank()) return
        if (chatBuilder.isNotEmpty()) {
            chatBuilder.append("\n\n")
        }
        val prefix = if (isUser) getString(R.string.recipe_chat_user_name) else getString(R.string.recipe_chat_assistant_name)
        chatBuilder.append(prefix).append("\n").append(message.trim())
        binding.chatHistory.text = chatBuilder.toString()
        binding.chatScroll.post {
            binding.chatScroll.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressIndicator.isVisible = isLoading
        binding.generateButton.isEnabled = !isLoading
    }
}