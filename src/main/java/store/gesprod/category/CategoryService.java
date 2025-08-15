package store.gesprod.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // Liste des catégories
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Récupère une catégorie par son ID
    public CategoryDTO getCategoryByID(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Catégorie non existante !"));
        return categoryMapper.toDTO(category);
    }

    // Créer une nouvelle catégorie
    public Category createCategory(Category category) {
        // Vérifie si la catégorie existe déjà
        if (categoryRepository.existsByNomCategoryIgnoreCase(category.getNomCategory())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cette catégorie existe déjà !");
        }

        // Crée une nouvelle instance indépendante
        Category newCategory = new Category();
        newCategory.setNomCategory(category.getNomCategory());

        return categoryRepository.save(newCategory);
    }

    // Alternative avec Builder
    public Category createCategoryWithBuilder(CategoryDTO categoryDTO) {
        return categoryRepository.save(
                Category.builder()
                        .nomCategory(categoryDTO.getNomCategory())
                        .build()
        );
    }

    /**
     public CategoryDTO createCategory(CategoryDTO categoryDTO) {
     // Vérifie si la catégorie existe déjà
     if (categoryRepository.existsDistinctByNomCategory(categoryDTO.getNomCategory())) {
     throw new IllegalArgumentException("Cette catégorie existe déjà !");
     }

     Category category = categoryMapper.toEntity(categoryDTO);
     Category savedCategory = categoryRepository.save(category);
     return categoryMapper.toDTO(savedCategory);
     }
     */

    /* Mettre à jour les informations sur une catégorie existante */
    public CategoryDTO updateCategory(UUID id, CategoryDTO updatedCategoryDTO) {
        return categoryRepository.findById(id).map(existingCategory -> {
            existingCategory.setNomCategory(updatedCategoryDTO.getNomCategory());

            Category updatedCategory = categoryMapper.toEntity(updatedCategoryDTO);
            return categoryMapper.toDTO(updatedCategory);
        }).orElseThrow(() -> new IllegalArgumentException("Catégorie non existante !"));
    }

    /* Supprimer une catégorie existante en BD */
    public void deleteCategory(UUID id) {
        // Si la catégorie à supprimer n'existe pas, il faut afficher un message
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Catégorie non existante !");
        }
        categoryRepository.deleteById(id);
    }
}
