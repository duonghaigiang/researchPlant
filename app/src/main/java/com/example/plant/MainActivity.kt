package com.example.plant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.plant.Species
import android.util.Log
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.checkerframework.checker.units.qual.A
import java.util.*
import java.text.SimpleDateFormat
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {
    private lateinit var username : TextView
    private val TAG = "MyActivity"
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView : NavigationView
    private lateinit var layoutspecies : LinearLayout
    private lateinit var latestArticleTitle: TextView
    private lateinit var latestArticleDescription: TextView
    private lateinit var latestArticleImage: ImageView
    private lateinit var latestArticleTimestamp: TextView
    private lateinit var latestArticleAuthor: TextView
    private lateinit var addNewsPlant : Button
    private lateinit var layoutArticles : LinearLayout
    private lateinit var scrollView3 : ScrollView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapping()

        // Lấy thông tin người dùng hiện tại từ FirebaseAuth
        val user = FirebaseAuth.getInstance()
        val currentUser = Firebase.auth.currentUser // viết tắt của kotlin

        // Hiển thị tên người dùng hiện tại
        val currentUserTextView = username
        currentUser?.let {
            currentUserTextView.text = it.displayName
        }


//        readFS(currentUser?.uid)
//        { data ->
//            if (data != null) {
//                val displayName = data["displayName"] as String?
//                if (displayName != null) {
//                    currentUserTextView.text = displayName
//                }
//            }
//            else
//            {
//            }
//
//        }

        addNewsPlant.setOnClickListener {
            val intent = Intent(this@MainActivity, AddNewSpeciesAction::class.java)
            startActivity(intent)
        }

        // toggle bar
        toggle = ActionBarDrawerToggle(this ,drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.nav_logout -> {user.signOut()
                    startActivity(Intent(this@MainActivity, LoginAction::class.java))
                }
                R.id.nav_infor -> {
                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))

                }
            }
            true
        }

        //species
        layoutspecies.setOnClickListener {
            val intent = Intent(this@MainActivity, SpeciesAction::class.java)
            startActivity(intent)
        }
        //Articles
        layoutArticles.setOnClickListener {
            val intent = Intent(this@MainActivity, ArticlesAction::class.java)
            startActivity(intent)
        }
        // Lấy bài viết mới nhất
        fetchLatestArticle()
        scrollView3.setOnClickListener {
            val intent = Intent(this@MainActivity, ArticlesAction::class.java)
            startActivity(intent)
        }



    }

    // Phương thức xử lý khi chọn một item trong menu option
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
        {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun mapping()
    {

        addNewsPlant = findViewById(R.id.addNewsPlant)

        scrollView3 = findViewById(R.id.scrollView3)
        latestArticleTitle = findViewById(R.id.articleTitle)
        latestArticleAuthor = findViewById(R.id.articleAuthor)
        latestArticleDescription = findViewById(R.id.articleDescription)
        latestArticleImage = findViewById(R.id.articleImage)
        latestArticleTimestamp = findViewById(R.id.articleTimestamp)
        username = findViewById(R.id.username)
        layoutArticles = findViewById(R.id.layoutArticles)
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
        layoutspecies = findViewById(R.id.layoutspecies)
    }
    private fun fetchLatestArticle() {
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()
        db.collection("articles")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("title") ?: ""
                    val description = document.getString("description") ?: ""
                    val imageUrl = document.getString("imgURL") ?: ""
                    val createdAt = document.getTimestamp("createdAt")?.toDate()

                    val authorId = document.getString("uid") ?: ""

                    // fetch author details
                    db.collection("users").document(authorId)
                        .get()
                        .addOnSuccessListener { userDocument ->
                            val author = userDocument.getString("displayName") ?: ""
                            latestArticleAuthor.text = "Author :" +author
                        }
                    latestArticleTitle.text = "Title :" +title
                    latestArticleDescription.text = "Description :" +description
                    Glide.with(this)
                        .load(imageUrl)
                        .into(latestArticleImage)
                    if (createdAt != null) {
                        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        latestArticleTimestamp.text = format.format(createdAt)
                    }
                }
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.d("SpeciesAction", "Error fetching species: ", exception)
                progressBar.visibility = View.GONE
            }
    }
    private fun readFS(uid :String?,callback: (Map<String, Any>?) -> Unit)
    {
       if(uid != null)
       {
           val db = FirebaseFirestore.getInstance()
           db.collection("users").document(uid)
               .get()
               .addOnSuccessListener { documentSnapshot ->
                   if (documentSnapshot.exists()) {
                       // Dữ liệu tài liệu được lưu trong documentSnapshot
                       val data = documentSnapshot.data

                   } else {
                       // Tài liệu không tồn tại
                   }
               }
               .addOnFailureListener { exception ->
                   // Xử lý lỗi
               }
       }

    }
}