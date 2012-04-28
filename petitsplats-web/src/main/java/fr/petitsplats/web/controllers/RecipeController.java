package fr.petitsplats.web.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public Recipe getRecipe(@PathVariable int id, HttpServletResponse response) {

        Recipe recipe = recipeService.findById(id);
        if (recipe == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }

        return recipe;
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

    @RequestMapping(value = "/{id}.jpg", method = RequestMethod.POST)
    public String attachPicture(@RequestParam("file") MultipartFile file,
            @PathVariable Integer id) throws IOException, ViolationException {
        if (!file.isEmpty()) {
            RecipePicture rp = new RecipePicture();
            rp.setId(id);
            rp.setImage(file.getBytes());
            recipeService.createRecipePicture(rp);
            return "redirect:/createRecipe.html";
        } else {
            throw new ViolationException(null);
        }
    }

    @RequestMapping(value = "/{id}.jpg", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getPicture(@PathVariable int id) {

        RecipePicture image = recipeService.findPictureById(id);
        if (image == null) {
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }

        byte[] imageAsBytes = image.getImage();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.IMAGE_JPEG);
        responseHeaders.set("Content-Disposition", "attachment");
        responseHeaders.setContentLength(imageAsBytes.length);
        return new ResponseEntity<byte[]>(imageAsBytes, responseHeaders,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateRecipe(@RequestBody Recipe recipe,
            @PathVariable Integer id, HttpServletResponse response)
            throws ViolationException, MethodNotAllowedException {

        if (recipe.getId() != id || recipe.getId() == 0
                || recipe.getId() == null) {
            throw new MethodNotAllowedException();
        }

        recipeService.updateRecipe(recipe);

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

}
