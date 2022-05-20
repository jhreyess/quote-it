package com.quoteit.android.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.quoteit.android.ui.QuoteItApp
import kotlinx.coroutines.flow.collect

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