package com.example.travelsidequests.repository

import com.example.travelsidequests.data.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {}
