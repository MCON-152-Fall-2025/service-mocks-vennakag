package com.mcon152.recipeshare.service;

import com.mcon152.recipeshare.Recipe;
import com.mcon152.recipeshare.repository.RecipeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Assignment: Implement all TODOs using Mockito features covered in class:
 *  - @Mock, @InjectMocks, @Captor, @ExtendWith(MockitoExtension.class)
 *  - Stubbing: thenReturn / thenAnswer / thenThrow
 *  - Verifications: verify(...), times/never/atLeast..., verifyNoMoreInteractions
 *  - InOrder (where meaningful)
 *  - Void stubbing: doNothing / doThrow (use deleteById for this)
 *  - Matchers: any(), eq(), argThat()
 *  - ArgumentCaptor
 *  - (Optional) Spy demo if you introduce a small helper in tests
 *
 * NOTE: This is a pure unit test. Do NOT start a Spring context.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RecipeService (Mockito) — Assignment Skeleton")
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService; // CUT implements RecipeService

    @Captor
    private ArgumentCaptor<Recipe> recipeCaptor;

    // --- Helpers for sample data ---

    private Recipe newRecipeNoId() {
        return new Recipe(
                null,
                "Chocolate Cake",
                "Moist chocolate cake",
                "flour, eggs, cocoa",
                "mix, bake",
                8
        );
    }

    private Recipe savedRecipe(long id) {
        return new Recipe(
                id,
                "Chocolate Cake",
                "Moist chocolate cake",
                "flour, eggs, cocoa",
                "mix, bake",
                8
        );
    }

    // ------------------ addRecipe ------------------

    @Nested
    @DisplayName("addRecipe(Recipe)")
    class AddRecipe {

        @Test
        @DisplayName("returns saved entity (thenReturn) and calls repository.save once")
        void returnsSaved_andSavesOnce() {
            // TODO:
            // 1) when(recipeRepository.save(...)).thenReturn(savedRecipe(1L))
            // 2) call recipeService.addRecipe(newRecipeNoId())
            // 3) assert non-null id and fields
            // 4) verify(recipeRepository).save(any(Recipe.class)); verifyNoMoreInteractions(recipeRepository)

            //See code below as an example answer

            Recipe input = newRecipeNoId();
            Recipe saved = savedRecipe(1L);

            when(recipeRepository.save(any(Recipe.class))).thenReturn(saved);

            Recipe out = recipeService.addRecipe(input);
            assertEquals(1L, out.getId());
            assertEquals(saved, out);

            verify(recipeRepository).save(any(Recipe.class));
            verifyNoMoreInteractions(recipeRepository);
        }

        @Test
        @DisplayName("assigns ID dynamically (thenAnswer) and captures argument")
        void assignsId_thenAnswer_andCaptures() {
            // TODO:
            // 1) Use thenAnswer to return a new Recipe with id=1L, copying fields from arg
            // 2) capture the arg with ArgumentCaptor and assert title, id==null pre-save

            //See code below as an example answer

            when(recipeRepository.save(any(Recipe.class))).thenAnswer(inv -> {
                Recipe r = inv.getArgument(0);
                return new Recipe(1L, r.getTitle(), r.getDescription(),
                        r.getIngredients(), r.getInstructions(), r.getServings());
            });

            Recipe out = recipeService.addRecipe(newRecipeNoId());
            assertEquals(1L, out.getId());

            verify(recipeRepository).save(recipeCaptor.capture());
            Recipe sent = recipeCaptor.getValue();
            assertNull(sent.getId()); // before persistence
            assertEquals("Chocolate Cake", sent.getTitle());
        }

        @Test
        @DisplayName("propagates repository failure (thenThrow)")
        void propagatesRepositoryFailure() {
            // TODO:
            // when(recipeRepository.save(any())).thenThrow(new IllegalStateException("DB down"))
            // assertThrows on recipeService.addRecipe(...)
            when(recipeRepository.save(any())).thenThrow(new IllegalStateException("DB down"));
            assertThrows(IllegalStateException.class, () -> recipeService.addRecipe(newRecipeNoId()));

        }
    }

    // ------------------ getAllRecipes ------------------

    @Nested
    @DisplayName("getAllRecipes()")
    class GetAllRecipes {

        @Test
        @DisplayName("returns list from repository")
        void returnsList() {
            // TODO:
            // when(recipeRepository.findAll()).thenReturn(List.of(...))
            // assert same size/content; verify(findAll)
            Recipe recipe = newRecipeNoId();
            when(recipeRepository.findAll()).thenReturn(List.of(recipe));

            List<Recipe> recipes = recipeService.getAllRecipes();

            assertEquals(1, recipes.size());

            Recipe actual = recipes.get(0);
            assertEquals(recipe.getTitle(), actual.getTitle());
            assertEquals(recipe.getDescription(), actual.getDescription());
            assertEquals(recipe.getIngredients(), actual.getIngredients());
            assertEquals(recipe.getInstructions(), actual.getInstructions());
            assertEquals(recipe.getServings(), actual.getServings());

            verify(recipeRepository).findAll();
            verifyNoMoreInteractions(recipeRepository);

        }
    }

    // ------------------ getRecipeById ------------------

    @Nested
    @DisplayName("getRecipeById(long)")
    class GetById {

        @Test
        @DisplayName("returns Optional.present when found")
        void present() {
            // TODO: stub findById(1L)->Optional.of(savedRecipe(1L)), assert present
            Recipe stored = newRecipeNoId();
            when(recipeRepository.findById(1L)).thenReturn(Optional.of(stored));

            Optional<Recipe> result = recipeService.getRecipeById(1L);

            assertTrue(result.isPresent());

            Recipe actual = result.get();
            assertEquals(stored.getTitle(), actual.getTitle());
            assertEquals(stored.getDescription(), actual.getDescription());
            assertEquals(stored.getIngredients(), actual.getIngredients());
            assertEquals(stored.getInstructions(), actual.getInstructions());
            assertEquals(stored.getServings(), actual.getServings());

            verify(recipeRepository).findById(1L);
            verifyNoMoreInteractions(recipeRepository);
        }

        @Test
        @DisplayName("returns Optional.empty when missing")
        void empty() {
            // TODO: stub Optional.empty, assert empty
            when(recipeRepository.findById(1L)).thenReturn(Optional.empty());
            Optional<Recipe> recipe = recipeService.getRecipeById(1L);
            assertEquals(Optional.empty(), recipe);
        }
    }

    // ------------------ deleteRecipe ------------------

    @Nested
    @DisplayName("deleteRecipe(long)")
    class DeleteRecipe {

        @Test
        @DisplayName("returns true when entity existed")
        void returnsTrue_whenExists() {
            // TODO:
            // when(recipeRepository.existsById(id)).thenReturn(true)
            // doNothing().when(recipeRepository).deleteById(id)
            // assert true; verify order: existsById -> deleteById
            when(recipeRepository.existsById(1L)).thenReturn(true);
            doNothing().when(recipeRepository).deleteById(1L);
            boolean result = recipeService.deleteRecipe(1L);
            assertTrue(result);
            InOrder inOrder = inOrder(recipeRepository);

            inOrder.verify(recipeRepository).existsById(1L);
            inOrder.verify(recipeRepository).deleteById(1L);
            inOrder.verifyNoMoreInteractions();

        }

        @Test
        @DisplayName("returns false when missing (never deletes)")
        void returnsFalse_whenMissing() {
            // TODO: existsById -> false; assert false; verify deleteById never called
            when(recipeRepository.existsById(1L)).thenReturn(false);
            boolean result = recipeService.deleteRecipe(1L);
            assertFalse(result);
            verify(recipeRepository, never()).deleteById(anyLong());

        }

        @Test
        @DisplayName("propagates delete error (doThrow)")
        void propagatesDeleteError() {
            // TODO: existsById -> true; doThrow(...) on deleteById; assertThrows
            when(recipeRepository.existsById(1L)).thenReturn(true);
            doThrow(IllegalStateException.class).when(recipeRepository).deleteById(1L);
            assertThrows(IllegalStateException.class, () -> recipeService.deleteRecipe(1L));
        }
    }

    // ------------------ updateRecipe ------------------

    @Nested
    @DisplayName("updateRecipe(long, Recipe)")
    class UpdateRecipe {

        @Test
        @DisplayName("returns updated entity when exists")
        void returnsUpdated_whenExists() {
            // TODO:
            // findById -> present(existing)
            // save(...) -> updatedSaved
            // assert Optional.present & fields updated
            // capture arg and assert values
            Recipe existing = newRecipeNoId();
            Recipe update = newRecipeNoId();

            when(recipeRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(recipeRepository.save(any(Recipe.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Optional<Recipe> result = recipeService.updateRecipe(1L, update);

            assertTrue(result.isPresent());

            Recipe saved = result.get();
            assertEquals(update.getTitle(), saved.getTitle());
            assertEquals(update.getDescription(), saved.getDescription());

            verify(recipeRepository).findById(1L);
            verify(recipeRepository).save(recipeCaptor.capture());

            Recipe sentToSave = recipeCaptor.getValue();
            assertEquals(update.getTitle(), sentToSave.getTitle());
         }

        @Test
        @DisplayName("returns empty when entity missing")
        void returnsEmpty_whenMissing() {
            // TODO: findById -> empty; assert Optional.empty; verify save never called
            when(recipeRepository.findById(1L)).thenReturn(Optional.empty());
            Recipe updatedRecipe = newRecipeNoId();
            assertEquals(Optional.empty(), recipeService.updateRecipe(1L, updatedRecipe));
            verify(recipeRepository, never()).save(any());

        }
    }

    // ------------------ patchRecipe ------------------

    @Nested
    @DisplayName("patchRecipe(long, Recipe)")
    class PatchRecipe {

        @Test
        @DisplayName("applies only non-null fields (argThat)")
        void appliesNonNullFields_only() {
            // TODO:
            // findById -> present(existing)
            // provide partial with only title set
            // repository.save returns the modified entity (use thenAnswer echo)
            // verify save(argThat(...)) to ensure unchanged fields remain as-is
            Recipe existing = newRecipeNoId();

            when(recipeRepository.findById(1L))
                    .thenReturn(Optional.of(existing));

            Recipe patch = new Recipe(
                    null,
                    "Updated Title",
                    null,
                    null,
                    null,
                    null
            );
            when(recipeRepository.save(any(Recipe.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Optional<Recipe> result = recipeService.patchRecipe(1L, patch);

            assertTrue(result.isPresent());

            Recipe updated = result.get();
            assertEquals("Updated Title", updated.getTitle());
            assertEquals(existing.getDescription(), updated.getDescription());
            assertEquals(existing.getIngredients(), updated.getIngredients());
            assertEquals(existing.getInstructions(), updated.getInstructions());
            assertEquals(existing.getServings(), updated.getServings());
            verify(recipeRepository).save(argThat(r ->
                    r.getTitle().equals("Updated Title") &&
                            r.getDescription().equals(existing.getDescription()) &&
                            r.getIngredients().equals(existing.getIngredients()) &&
                            r.getInstructions().equals(existing.getInstructions()) &&
                            r.getServings().equals(existing.getServings())
            ));

            verify(recipeRepository).findById(1L);
            verifyNoMoreInteractions(recipeRepository);

        }

        @Test
        @DisplayName("returns empty when entity missing")
        void returnsEmpty_whenMissing() {
            // TODO: findById -> empty; assert Optional.empty; verify save never called
            when(recipeRepository.findById(1L)).thenReturn(Optional.empty());
            Recipe updatedRecipe = newRecipeNoId();
            assertEquals(Optional.empty(), recipeService.updateRecipe(1L, updatedRecipe));
            verify(recipeRepository, never()).save(any());

         }
    }

    // ------------------ extra practice ------------------

    @Nested
    @DisplayName("Advanced stubbing & verification")
    class Advanced {

        @Test
        @DisplayName("consecutive stubs on existsById (true, false)")
        void consecutiveStubs_existsById() {
            // TODO: when(existsById(1L)).thenReturn(true, false); verify two calls and no more
            when(recipeRepository.existsById(1L)).thenReturn(true,false);
            recipeService.deleteRecipe(1L);
            recipeService.deleteRecipe(1L);
            verify(recipeRepository, times(2)).existsById(1L);
            verify(recipeRepository, times(1)).deleteById(1L);
            verifyNoMoreInteractions(recipeRepository);
         }
    }
}
