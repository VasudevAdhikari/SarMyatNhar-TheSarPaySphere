package com.application.sarmyatnhar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.application.sarmyatnhar.data.dao.AuthorProfileDao
import com.application.sarmyatnhar.data.dao.BookApprovalDao
import com.application.sarmyatnhar.data.dao.BookDao
import com.application.sarmyatnhar.data.dao.BookPurchaseDao
import com.application.sarmyatnhar.data.dao.ConversionRequestDao
import com.application.sarmyatnhar.data.dao.LibraryDao
import com.application.sarmyatnhar.data.dao.NotificationDao
import com.application.sarmyatnhar.data.dao.PaymentMethodDao
import com.application.sarmyatnhar.data.dao.PaymentRequestDao
import com.application.sarmyatnhar.data.dao.RatingDao
import com.application.sarmyatnhar.data.entity.AuthorProfile
import com.application.sarmyatnhar.data.entity.Book
import com.application.sarmyatnhar.data.entity.BookApproval
import com.application.sarmyatnhar.data.entity.BookPurchase
import com.application.sarmyatnhar.data.entity.ConversionRequest
import com.application.sarmyatnhar.data.entity.Notification
import com.application.sarmyatnhar.data.entity.PaymentMethod
import com.application.sarmyatnhar.data.entity.PaymentRequest
import com.application.sarmyatnhar.data.entity.Rating
import com.application.sarmyatnhar.data.entity.User
import com.application.sarmyatnhar.data.dao.UserDao
import com.application.sarmyatnhar.data.dao.WalletDao
import com.application.sarmyatnhar.data.entity.Wallet
import com.application.sarmyatnhar.data.entity.Library

@Database(
    entities = [
        User::class, AuthorProfile::class, PaymentMethod::class, Book::class,
        BookApproval::class, Wallet::class, BookPurchase::class,
        PaymentRequest::class, ConversionRequest::class, Notification::class, Rating::class, Library::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun authorProfileDao(): AuthorProfileDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun bookDao(): BookDao
    abstract fun bookApprovalDao(): BookApprovalDao
    abstract fun walletDao(): WalletDao
    abstract fun bookPurchaseDao(): BookPurchaseDao
    abstract fun paymentRequestDao(): PaymentRequestDao
    abstract fun conversionRequestDao(): ConversionRequestDao
    abstract fun notificationDao(): NotificationDao
    abstract fun ratingDao(): RatingDao
    abstract fun libraryDao(): LibraryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
