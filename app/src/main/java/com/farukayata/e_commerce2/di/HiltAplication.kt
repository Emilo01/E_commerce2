package com.farukayata.e_commerce2.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
//hilt kurulumuna burda başladık maifestten devam ettik ardından bağımlılıkları
//kotrol edicez ProductsRepository
class HiltAplication : Application() {
}