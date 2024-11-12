package ma.jway.rms.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ma.jway.rms.dto.models.Category;
import ma.jway.rms.dto.responses.CategoryResponse;
import ma.jway.rms.repositories.CategoryRepository;

@RestController
@RequestMapping(value = "/categories")
public class CategoriesController {
    private final CategoryRepository categoryRepository;

    public CategoriesController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("")
    public List<CategoryResponse> getItems() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoryResponses = new ArrayList<>();

        for (Category category : categories) {
            CategoryResponse categoryResponse = new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getReference(),
                    category.getDescription(),
                    String.format("%s/%s", category.getIcon().getPath(), category.getIcon().getFilename()));

            categoryResponses.add(categoryResponse);
        }

        return categoryResponses;
    }
}
