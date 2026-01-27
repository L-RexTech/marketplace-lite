package com.dgliger.marketplace.auth.mapper;

import com.dgliger.marketplace.auth.dto.UserDto;
import com.dgliger.marketplace.auth.entity.Role;
import com.dgliger.marketplace.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserDto toDto(User user);

    @Named("rolesToStrings")
    default List<String> rolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
    }
}