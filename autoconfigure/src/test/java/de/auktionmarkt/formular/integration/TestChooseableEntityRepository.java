package de.auktionmarkt.formular.integration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestChooseableEntityRepository extends JpaRepository<TestChoosableEntity, Integer> {
}
