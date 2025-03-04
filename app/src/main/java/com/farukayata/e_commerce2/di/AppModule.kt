package com.farukayata.e_commerce2.di


import com.farukayata.e_commerce2.data.datasource.ProductsDataSource
import com.farukayata.e_commerce2.data.repo.OrderRepository
import com.farukayata.e_commerce2.network.ApiClient
import com.farukayata.e_commerce2.network.ApiService
import com.farukayata.e_commerce2.data.repo.ProductsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context

@Module //bağımlılık sağlayıcı modülü
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiClient.apiService
    }

    @Provides
    @Singleton
    fun provideProductsDataSource(apiService: ApiService): ProductsDataSource {
        return ProductsDataSource(apiService)
    }

//    @Provides
//    @Singleton
//    fun provideProductsRepository(apiService: ApiService): ProductsRepository {
//        return ProductsRepository(apiService)
//    }

    @Provides
    @Singleton
    fun provideProductsRepository(pds: ProductsDataSource): ProductsRepository {
        return ProductsRepository(pds)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    // FirebaseFirestore sağlama
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }//Firestore tabanlı veri işlemleri (add, get, delete) yapmak için kullanılır


    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    // Uygulama genelinde Context sağlama
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideOrderRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): OrderRepository {
        return OrderRepository(firestore, auth)
    }
}
