package com.example.quoteit.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.quoteit.data.local.PostEntity
import com.example.quoteit.domain.models.Post
import com.example.quoteit.ui.QuoteItApp
import kotlinx.coroutines.flow.collect
import java.lang.Exception

class SyncDataWorker(ctx: Context,params: WorkerParameters) : CoroutineWorker(ctx, params){


    override suspend fun doWork(): Result {
        return try{
            syncPosts()
            Result.success()
        }catch (e: Exception){
            e.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun syncPosts(){
        val postsRepo = (applicationContext as QuoteItApp).postsRepository
        val unsynced = postsRepo.getUnsyncedPosts()

        // Sync posts and also update feed
        if(unsynced.isNotEmpty()) { postsRepo.syncPosts(unsynced) }
        postsRepo.getPosts(fetchFromRemote = true, appendContent = false).collect()
    }
}