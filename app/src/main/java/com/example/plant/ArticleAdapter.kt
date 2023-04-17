package com.example.plant

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.content.Context;
import android.view.View;
import android.text.format.DateFormat

class ArticleAdapter(private val articles: List<Article>, private val context: Context) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorTextView: TextView = itemView.findViewById(R.id.articleAuthor)
        val titleTextView: TextView = itemView.findViewById(R.id.articleTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.articleDescription)
        val imageView: ImageView = itemView.findViewById(R.id.articleImage)
        val timestampTextView: TextView = itemView.findViewById(R.id.articleTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.article_item, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.authorTextView.text = article.author
        holder.titleTextView.text = article.title
        holder.descriptionTextView.text = article.description
        Glide.with(context)
            .load(article.imageUrl)
            .into(holder.imageView)
        holder.timestampTextView.text = DateFormat.format("dd/MM/yyyy", article.timestamp.toDate())
    }

    override fun getItemCount(): Int {
        return articles.size
    }
}