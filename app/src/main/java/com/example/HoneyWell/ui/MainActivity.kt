package com.example.HoneyWell.ui

import android.app.SearchManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.example.HoneyWell.R
import com.example.HoneyWell.adapter.ArticleAdapter
import com.example.HoneyWell.model.*
import com.example.HoneyWell.news_api.TopHeadlinesEndpoint

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val ENDPOINT_URL by lazy { "https://hn.algolia.com/api/v1/" }
    private lateinit var topHeadlinesEndpoint: TopHeadlinesEndpoint
    private lateinit var newsApiConfig: String
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleList: ArrayList<Hit>
    private lateinit var userKeyWordInput: String
    // RxJava related fields
    private lateinit var honeyWellOb: Observable<HoneyWellNewsData>
    private lateinit var compositeDisposable: CompositeDisposable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Network request
        val retrofit: Retrofit = generateRetrofitBuilder()
        topHeadlinesEndpoint = retrofit.create(TopHeadlinesEndpoint::class.java)
     //   newsApiConfig = resources.getString(R.string.api_key)
        swipe_refresh.setOnRefreshListener(this)
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        articleList = ArrayList()
        articleAdapter = ArticleAdapter(articleList)
        //When the app is launched of course the user input is empty.
        userKeyWordInput = ""
        //CompositeDisposable is needed to avoid memory leaks
        compositeDisposable = CompositeDisposable()
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = articleAdapter
    }

    override fun onStart() {
        super.onStart()
        checkUserKeywordInput()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onRefresh() {
        checkUserKeywordInput()
    }

    private fun checkUserKeywordInput() {
        if (userKeyWordInput.isEmpty()) {
            queryTopHeadlines()
        } else {
            getKeyWordQuery(userKeyWordInput)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.menu_main, menu)
            //Creates input field for the user search
            setUpSearchMenuItem(menu)
        }
        return true
    }

    private fun setUpSearchMenuItem(menu: Menu) {
        val searchManager: SearchManager = (getSystemService(Context.SEARCH_SERVICE)) as SearchManager
        val searchView: SearchView = ((menu.findItem(R.id.action_search)?.actionView)) as SearchView
        val searchMenuItem: MenuItem = menu.findItem(R.id.action_search)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Type any keyword to search..."
        searchView.setOnQueryTextListener(onQueryTextListenerCallback())
        searchMenuItem.icon.setVisible(false, false)
    }

    //Gets immediately triggered when user clicks on search icon and enters something
    private fun onQueryTextListenerCallback(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(userInput: String?): Boolean {
                return checkQueryText(userInput)
            }

            override fun onQueryTextChange(userInput: String?): Boolean {
                return checkQueryText(userInput)
            }
        }
    }

    private fun checkQueryText(userInput: String?): Boolean {
        if (userInput != null && userInput.length > 1) {
            userKeyWordInput = userInput
            getKeyWordQuery(userInput)
        } else if (userInput != null && userInput == "") {
            userKeyWordInput = ""
            queryTopHeadlines()
        }
        return false
    }


    private fun getKeyWordQuery(userKeywordInput: String) {
//        swipe_refresh.isRefreshing = true
//        if (userKeywordInput != null && userKeywordInput.isNotEmpty()) {
//            topHeadlinesObservable = topHeadlinesEndpoint.getUserSearchInput(newsApiConfig, userKeywordInput)
//            subscribeObservableOfArticle()
//        } else {
//            queryTopHeadlines()
//        }
    }

    private fun queryTopHeadlines() {
        swipe_refresh.isRefreshing = true
        honeyWellOb = topHeadlinesEndpoint.getHoneyWellNews()
        subscribeObservableOfArticle()
    }

    private fun subscribeObservableOfArticle() {
        articleList.clear()
        compositeDisposable.add(
            honeyWellOb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable.fromIterable(it.hits)
                }
                .subscribeWith(createArticleObserver())
        )
    }

    private fun createArticleObserver(): DisposableObserver<Hit> {
        return object : DisposableObserver<Hit>() {
            override fun onNext(t: Hit) {
                if (!articleList.contains(t)) {
                    articleList.add(t)
                }
            }

            override fun onComplete() {
                showArticlesOnRecyclerView()
            }

            override fun onError(e: Throwable) {
                Log.e("createArticleObserver", "Article error: ${e.message}")
            }
        }
    }

    private fun showArticlesOnRecyclerView() {
        if (articleList.size > 0) {
            empty_text.visibility = View.GONE
            retry_fetch_button.visibility = View.GONE
            recycler_view.visibility = View.VISIBLE
            articleAdapter.setArticles(articleList)
        } else {
            recycler_view.visibility = View.GONE
            empty_text.visibility = View.VISIBLE
            retry_fetch_button.visibility = View.VISIBLE
            retry_fetch_button.setOnClickListener { checkUserKeywordInput() }
        }
        swipe_refresh.isRefreshing = false
    }

    private fun generateRetrofitBuilder(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(ENDPOINT_URL)
            .addConverterFactory(GsonConverterFactory.create())
            //Add RxJava2CallAdapterFactory as a Call adapter when building your Retrofit instance
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}
