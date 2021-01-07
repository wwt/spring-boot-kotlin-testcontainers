package com.example.demo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface PersonRepository: JpaRepository<Person, Long> {
    fun findByPreferredName(preferredName: String): List<Person>
}