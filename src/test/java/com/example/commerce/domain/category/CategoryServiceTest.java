package com.example.commerce.domain.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Test
    void 대중소_3단계까지_등록된다() {
        Category large = categoryService.create("의류", null);
        Category medium = categoryService.create("상의", large.getId());
        Category small = categoryService.create("티셔츠", medium.getId());

        assertThat(small.getParent()).isEqualTo(medium);
        assertThat(medium.getChildren()).containsExactly(small);
    }

    @Test
    void 네번째_단계를_등록하면_예외() {
        Category large = categoryService.create("의류", null);
        Category medium = categoryService.create("상의", large.getId());
        Category small = categoryService.create("티셔츠", medium.getId());

        assertThatThrownBy(() -> categoryService.create("반팔", small.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지_않는_부모면_예외() {
        assertThatThrownBy(() -> categoryService.create("상의", 999L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름을_바꾼다() {
        Category large = categoryService.create("의류", null);

        categoryService.rename(large.getId(), "패션");

        assertThat(categoryService.get(large.getId()).getName()).isEqualTo("패션");
    }

    @Test
    void 하위_카테고리가_있으면_삭제_불가() {
        Category large = categoryService.create("의류", null);
        categoryService.create("상의", large.getId());

        assertThatThrownBy(() -> categoryService.delete(large.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 하위가_없으면_삭제된다() {
        Category large = categoryService.create("의류", null);

        categoryService.delete(large.getId());

        assertThat(categoryService.findAll()).isEmpty();
    }
}
