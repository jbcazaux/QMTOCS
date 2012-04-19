package fr.petitsplats.web.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import fr.petitsplats.MethodNotAllowedException;
import fr.petitsplats.domain.Recipe;
import fr.petitsplats.domain.RecipePicture;
import fr.petitsplats.exception.ViolationException;
import fr.petitsplats.service.RecipeService;

@Controller
@RequestMapping(value = "/recipe")
public class RecipeController extends AbstractController {

    @Autowired
    @Setter
    private RecipeService recipeService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Recipe getRecipe(@PathVariable int id) {

        return recipeService.findById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public int createRecipe(@RequestBody Recipe recipe,
            HttpServletResponse response) throws ViolationException,
            MethodNotAllowedException {

        if (recipe.getId() != null && recipe.getId() != 0) {
            throw new MethodNotAllowedException();
        }

        int id = recipeService.createRecipe(recipe);

        response.setStatus(HttpServletResponse.SC_OK);
        return id;
    }

    @RequestMapping(value = "/img", method = RequestMethod.POST)
    public void attachPicture(@RequestParam("file") MultipartFile file)
            throws IOException, ViolationException {
        if (!file.isEmpty()) {
            RecipePicture rp = new RecipePicture();
            rp.setId(4);
            rp.setImage(file.getBytes());
            recipeService.createRecipePicture(rp);
        } else {
            throw new ViolationException(null);
        }
    }

    @RequestMapping(value = "/img/{id}", method = RequestMethod.GET)
    @ResponseBody
    public byte[] getPicture(@PathVariable int id, HttpServletResponse response) {
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader("Content-Disposition", "attachment");
        return recipeService.findPictureById(id).getImage();
    }
}
