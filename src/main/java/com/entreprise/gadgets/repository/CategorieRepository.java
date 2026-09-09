package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Categorie;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieRepository extends JpaRepository<Categorie, Integer> {
}
