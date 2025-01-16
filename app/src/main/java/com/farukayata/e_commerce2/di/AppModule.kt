package com.farukayata.e_commerce2.di

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