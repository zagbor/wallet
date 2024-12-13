package com.zagbor.wallet.mapper;

import com.zagbor.wallet.dto.CategoryDto;
import com.zagbor.wallet.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);
}
