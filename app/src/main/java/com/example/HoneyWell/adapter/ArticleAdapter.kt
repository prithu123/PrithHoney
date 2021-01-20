package com.example.HoneyWell.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.HoneyWell.R
import com.example.HoneyWell.model.Hit
import kotlinx.android.synthetic.main.item_article.view.*

class ArticleAdapter(
    private var articleList: ArrayList<Hit>
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private val placeHolderImage = "https://pbs.twimg.com/profile_images/467502291415617536/SP8_ylk9.png"
    private lateinit var viewGroupContext: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ArticleViewHolder {
        viewGroupContext = viewGroup.context
        val itemView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_article, viewGroup, false)
        return ArticleViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    override fun onBindViewHolder(articleViewHolder: ArticleViewHolder, itemIndex: Int) {
        val article: Hit = articleList.get(itemIndex)
        setPropertiesForArticleViewHolder(articleViewHolder, article)
        articleViewHolder.cardView.setOnClickListener {
            //do something
        }
    }

    private fun setPropertiesForArticleViewHolder(articleViewHolder: ArticleViewHolder, article: Hit) {
        articleViewHolder.title.text = article?.title
        articleViewHolder.description.text = article?.author
    }



    fun setArticles(articles: ArrayList<Hit>) {
        articleList = articles
        notifyDataSetChanged()
    }

    inner class ArticleViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val cardView: CardView by lazy { view.card_view }
        val urlToImage: ImageView by lazy { view.article_urlToImage }
        val title: TextView by lazy { view.article_title }
        val description: TextView by lazy { view.article_description }
    }
}