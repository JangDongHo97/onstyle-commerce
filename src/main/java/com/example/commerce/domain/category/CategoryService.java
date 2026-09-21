package com.example.commerce.domain.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    static final int MAX_DEPTH = 3;

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category create(String name, Long parentId) {
        if (parentId == null) {
            return categoryRepository.save(new Category(name));
        }
        Category parent = get(parentId);
        if (depthOf(parent) + 1 > MAX_DEPTH) {
            throw new IllegalArgumentException("카테고리는 최대 " + MAX_DEPTH + "단계까지만 만들 수 있습니다.");
        }
        return categoryRepository.save(new Category(name, parent));
    }

    public Category get(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다. id=" + id));
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    public void rename(Long id, String name) {
        get(id).rename(name);
    }

    @Transactional
    public void delete(Long id) {
        Category category = get(id);
        if (!category.getChildren().isEmpty()) {
            throw new IllegalStateException("하위 카테고리가 있어 삭제할 수 없습니다. id=" + id);
        }
        categoryRepository.delete(category);
    }

    // 루트=1. 부모 체인을 따라 올라가며 센다.
    private int depthOf(Category category) {
        int depth = 1;
        for (Category cur = category.getParent(); cur != null; cur = cur.getParent()) {
            depth++;
        }
        return depth;
    }
}
