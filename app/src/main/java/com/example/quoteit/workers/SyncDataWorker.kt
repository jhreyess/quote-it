package com.example.quoteit.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.quoteit.ui.QuoteItApp
import java.lang.Exception

class SyncDataWorker(ctx: Context,params: WorkerParameters) : CoroutineWorker(ctx, params){

    private val postsRepo = (applicationContext as QuoteItApp).postsRepository

    override suspend fun doWork(): Result {
        return try{
            makeNotification("Synced started", applicationContext)
            val posts = postsRepo.getUnsyncedPosts()
            // Sync posts and also update feed
            if(posts.isNotEmpty()) { postsRepo.syncPosts(posts) }
            postsRepo.getPosts(fetchFromRemote = true, appendContent = false)
            Result.success()
        }catch (e: Exception){
            e.printStackTrace()
            makeNotification("Synced failed", applicationContext)
            Result.failure()
        }
    }
}