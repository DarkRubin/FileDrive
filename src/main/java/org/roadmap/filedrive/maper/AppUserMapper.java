package org.roadmap.filedrive.maper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.roadmap.filedrive.dto.AppUserDTO;
import org.roadmap.filedrive.model.AppUser;

@Mapper
public interface AppUserMapper {

    @Mapping(target = "id", ignore = true)
    AppUser fromDTO(AppUserDTO dto);

}
