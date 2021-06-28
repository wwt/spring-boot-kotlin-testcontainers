package com.example.demo

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeInRange
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class PersonRepositoryTests {

    companion object {
        @Container
        private val postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:latest")

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
        }
    }

    @Autowired
    private lateinit var testObject: PersonRepository

    @Test
    fun `when record is saved then the id is populated`() {
        val tom = testObject.save(Person(name = "Tom Smith", preferredName = "Tom"))
        tom.id shouldBeInRange (1..Long.MAX_VALUE)
    }

    @Test
    fun `when multiple records with the same preferred name then all are found`() {
        testObject.save(Person(name = "Tom Smith", preferredName = "Tom"))
        testObject.save(Person(name = "Mark Smith", preferredName = "Tom"))
        testObject.save(Person(name = "Thomas Doe", preferredName = "Tom"))
        testObject.save(Person(name = "Tommy Jones", preferredName = "Tom"))

        val actual = testObject.findByPreferredName("Tom")

        actual shouldHaveSize 4
    }
}
