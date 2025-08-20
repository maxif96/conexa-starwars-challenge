package com.starwars.starships.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface StarshipRepository {
    // Este repositorio se mantiene vacío ya que los datos vienen de la API externa SWAPI
    // En el futuro se podría usar para cachear datos o almacenar favoritos del usuario
}

