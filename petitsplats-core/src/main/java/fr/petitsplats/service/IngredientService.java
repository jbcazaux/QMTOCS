package fr.petitsplats.service;

import java.util.List;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.petitsplats.dao.IngredientDAO;
import fr.petitsplats.domain.Ingredient;
import fr.petitsplats.exception.FunctionnalException;

@Transactional(rollbackFor = FunctionnalException.class)
@Service
public class IngredientService {

    @Setter
    @Autowired
    private IngredientDAO ingredientDAO;

    public List<Ingredient> findAll() {
        return ingredientDAO.findAll();
    }

    public Ingredient findByLabel(String label) {
        return ingredientDAO.findByLabel(label);
    }
}
