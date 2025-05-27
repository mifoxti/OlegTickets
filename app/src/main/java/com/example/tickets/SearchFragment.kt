package com.example.tickets

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var api: GeoNamesApi
    private lateinit var adapter: CityAdapter
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var placeholderText: TextView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: View


    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ✅ Инициализируем api до использования
        api = RetrofitInstance.api
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchEditText = view.findViewById(R.id.editSearch)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        placeholderText = view.findViewById(R.id.placeholderText)

        adapter = CityAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // ✅ Обработка изменения текста с задержкой 2 секунды
        searchEditText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { handler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    val query = s.toString().trim()
                    if (query.isNotEmpty()) {
                        performSearch(query)
                    } else {
                        adapter.updateData(emptyList())
                        placeholderText.visibility = View.VISIBLE
                        placeholderText.text = "Введите город"
                    }
                }
                handler.postDelayed(searchRunnable!!, 2000)
            }
        })

        // Восстановление предыдущего состояния (при повороте экрана)
        val previousQuery = savedInstanceState?.getString("query") ?: ""
        searchEditText.setText(previousQuery)
        if (previousQuery.isNotEmpty()) {
            performSearch(previousQuery)
        }

        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
        historyTitle = view.findViewById(R.id.historyTitle)
        clearHistoryButton = view.findViewById(R.id.clearHistoryButton)

        historyAdapter = HistoryAdapter { selectedQuery ->
            searchEditText.setText(selectedQuery)
            searchEditText.setSelection(selectedQuery.length)
            performSearch(selectedQuery)
        }

        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = historyAdapter

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val history = loadHistory()
                if (history.isNotEmpty()) {
                    showHistory(history)
                }
            } else {
                hideHistory()
            }
        }

        clearHistoryButton.setOnClickListener {
            clearHistory()
            hideHistory()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("query", searchEditText.text.toString())
    }

    private fun performSearch(query: String) {
        progressBar.visibility = View.VISIBLE
        placeholderText.visibility = View.GONE

        api.searchCities(query, "ru", "mifoxti").enqueue(object : Callback<GeoNamesResponse> {
            override fun onResponse(
                call: Call<GeoNamesResponse>,
                response: Response<GeoNamesResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val cities = response.body()?.geonames?.mapNotNull {
                        val name = it.name ?: return@mapNotNull null
                        val country = it.countryName ?: return@mapNotNull null
                        City(name, country)
                    } ?: emptyList()

                    if (cities.isEmpty()) {
                        placeholderText.visibility = View.VISIBLE
                        placeholderText.text = "Нет результатов"
                    }

                    adapter.updateData(cities)
                } else {
                    placeholderText.visibility = View.VISIBLE
                    placeholderText.text = "Ошибка при получении данных"
                }
            }

            override fun onFailure(call: Call<GeoNamesResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                placeholderText.visibility = View.VISIBLE
                placeholderText.text = "Ошибка: ${t.localizedMessage ?: "неизвестная"}"
            }
        })
        saveToHistory(query)
    }

    fun saveToHistory(query: String) {
        val prefs = requireContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
        val set = prefs.getStringSet("history", LinkedHashSet())?.toMutableList() ?: mutableListOf()

        set.remove(query) // Удаляем, если уже есть
        set.add(0, query) // Добавляем в начало
        if (set.size > 10) set.removeAt(set.size - 1)

        prefs.edit().putStringSet("history", LinkedHashSet(set)).apply()
    }

    fun loadHistory(): List<String> {
        val prefs = requireContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
        return prefs.getStringSet("history", emptySet())?.toList() ?: emptyList()
    }

    fun clearHistory() {
        val prefs = requireContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
        prefs.edit().remove("history").apply()
    }

    private fun showHistory(history: List<String>) {
        historyAdapter.updateData(history)
        historyRecyclerView.visibility = View.VISIBLE
        historyTitle.visibility = View.VISIBLE
        clearHistoryButton.visibility = View.VISIBLE
    }

    private fun hideHistory() {
        historyRecyclerView.visibility = View.GONE
        historyTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

}
