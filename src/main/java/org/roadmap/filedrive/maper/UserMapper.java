package org.roadmap.filedrive.maper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.roadmap.filedrive.dto.UserDTO;
import org.roadmap.filedrive.model.User;

@Mapper
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User fromDTO(UserDTO dto);

}
