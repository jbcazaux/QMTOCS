package fr.petitsplats.web.controllers;

import java.util.List;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.petitsplats.domain.Ingredient;
import fr.petitsplats.service.IngredientService;

@Controller
public class IngredientController extends AbstractController {

    @Autowired
    @Setter
    private IngredientService ingredientService;

    @RequestMapping(value = "/ingredients", method = RequestMethod.GET)
    @ResponseBody
    public List<Ingredient> findAll() {

        return ingredientService.findAll();
    }

}
