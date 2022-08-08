package com.example.employee.common.converter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface Converter<E, D> {

    D convertToDto(E entity);

    E convertToEntity(D dto);

    default List<D> convertToDtos(final Collection<E> entities){
        return entities
                .stream()
                .map(entity -> convertToDto(entity))
                .collect(Collectors.toList()
        );
    }

    default List<E> convertToEntities(final Collection<D> dtos){
        return dtos
                .stream()
                .map(dto -> convertToEntity(dto))
                .collect(Collectors.toList()
        );
    }

}
