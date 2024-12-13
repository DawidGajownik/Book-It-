package d.gajownik.bookit.service

import org.springframework.data.jpa.repository.JpaRepository

interface ServiceRepository: JpaRepository<Service, Long> {
}