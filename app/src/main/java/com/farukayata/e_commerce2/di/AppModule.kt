package com.farukayata.e_commerce2.di


import com.farukayata.e_commerce2.data.datasource.ProductsDataSource
import com.farukayata.e_commerce2.network.ApiClient
import com.farukayata.e_commerce2.network.ApiService
import com.farukayata.e_commerce2.data.repo.ProductsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
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
    }
}













/*
import com.farukayata.e_commerce2.data.datasource.ProductsDataSource
import com.farukayata.e_commerce2.data.repo.ProductsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideProductsDataSource() : ProductsDataSource {
        return ProductsDataSource()
    }

    @Provides
    @Singleton
    fun provideProductsRepository(pds : ProductsDataSource) : ProductsRepository {
        return ProductsRepository(pds)
    }
}

 */