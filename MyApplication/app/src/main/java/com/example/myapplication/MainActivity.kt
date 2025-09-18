class AddIngredientActivity : AppCompatActivity() {

    private lateinit var ingredientDao: IngredientDao
    private lateinit var fridgeItemDao: FridgeItemDao

    private lateinit var spinnerCategory: Spinner
    private lateinit var gridIngredients: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ingredient)

        // DB 초기화
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "fridge-database"
        ).build()

        ingredientDao = db.ingredientDao()
        fridgeItemDao = db.fridgeItemDao()

        spinnerCategory = findViewById(R.id.spinnerCategory)
        gridIngredients = findViewById(R.id.gridIngredients)

        setupSpinner()
    }

    private fun setupSpinner() {
        lifecycleScope.launch {
            val categories = ingredientDao.getAllCategories()
            val adapter = ArrayAdapter(this@AddIngredientActivity, android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinnerCategory.adapter = adapter

            spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedCategory = categories[position]
                    loadIngredients(selectedCategory)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun loadIngredients(category: String) {
        lifecycleScope.launch {
            val ingredients = ingredientDao.getIngredientsByCategory(category)

            // 기존 버튼 제거
            gridIngredients.removeAllViews()

            // 버튼 생성해서 GridLayout에 추가
            ingredients.forEach { ingredient ->
                val button = Button(this@AddIngredientActivity).apply {
                    text = ingredient.name
                    textSize = 14f
                    setPadding(8, 8, 8, 8)
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = ViewGroup.LayoutParams.WRAP_CONTENT
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        setMargins(8, 8, 8, 8)
                    }
                    setOnClickListener {
                        insertToFridge(ingredient.id)
                    }
                }
                gridIngredients.addView(button)
            }
        }
    }

    private fun insertToFridge(ingredientId: Int) {
        lifecycleScope.launch {
            val item = FridgeItem(
                ingredientId = ingredientId,
                quantity = "1개", // 기본값
                addDate = System.currentTimeMillis(),
                expiryDate = null // 임시 null
            )
            fridgeItemDao.insert(item)
            Toast.makeText(this@AddIngredientActivity, "재료가 냉장고에 추가됐어요!", Toast.LENGTH_SHORT).show()
        }
    }
}