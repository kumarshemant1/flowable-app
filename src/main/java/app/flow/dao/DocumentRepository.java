package app.flow.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.flow.entity.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

	
}
